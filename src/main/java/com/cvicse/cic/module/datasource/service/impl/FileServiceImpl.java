package com.cvicse.cic.module.datasource.service.impl;

import com.alibaba.excel.EasyExcel;
import com.cvicse.cic.module.datasource.bean.CiConstructTarget;
import com.cvicse.cic.module.datasource.bean.CiFrameworkIndicator;
import com.cvicse.cic.module.datasource.bean.CiFrameworkObject;
import com.cvicse.cic.module.datasource.bean.CiFrameworkTreepath;
import com.cvicse.cic.module.datasource.service.CiFrameworkObjectService;
import com.cvicse.cic.util.excel.NoModelDataListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cvicse.cic.module.datasource.service.CiFrameworkIndicatorService;
import com.cvicse.cic.module.datasource.service.CiFrameworkTreepathService;
import com.cvicse.cic.module.datasource.service.CiConstructTargetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FileServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    private CiFrameworkObjectService ciFrameworkObjectService;

    @Autowired
    private CiFrameworkIndicatorService ciFrameworkIndicatorService;

    @Autowired
    private CiFrameworkTreepathService ciFrameworkTreepathService;

    @Autowired
    private CiConstructTargetService ciConstructTargetService;

    /**
     * 读取web上传的excel文件
     * @param file
     * @return
     */
    @Transactional
    public boolean resolveUploadExcel(MultipartFile file) throws Exception {
        List<Map<Integer, String>> excelDataList = new ArrayList<Map<Integer, String>>();
        try {
            //解析excel
            EasyExcel.read(file.getInputStream(), new NoModelDataListener(excelDataList)).sheet().doRead();
            saveExcelDataToDB(file, excelDataList);
            // TODO 保存excel文件到文件服务器
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new Exception("上传失败，请检查文件是否符合要求。");
        }
    }

    /**
     * 保存解析的excel数据到数据库
     * @param file
     * @param excelDataList
     */
    private void saveExcelDataToDB(MultipartFile file, List<Map<Integer, String>> excelDataList) throws Exception {
        //保存架构对象
        CiFrameworkObject ciFrameworkObject = new CiFrameworkObject(null, file.getOriginalFilename(), excelDataList.get(0).keySet().size(), null, null, LocalDateTime.now(), null);
        ciFrameworkObjectService.save(ciFrameworkObject);
        /**
         * 每行数据下标为0的单元格不是null，则说明这是一棵新的树；若下标的单元格为null，则说明这是树上的某级子节点
         * 创建树结构的思路：先把下标为0的这一行暂存，说明自这一行开始直到下一个下标为0 的单元格不为null的行 为止，都是这一棵树
         * 之后遍历这些行，从每行的第一个不为null的节点开始创建关系：
         * 先创建指向自身的节点，然后该节点前面的节点指向自己即可
         */
        // 保存完整的树结构id (完整的树是指 excel中第一列中某一个合并了的单元格和它右侧所有单元格组成的树结构)
        List<Long> entireSubtree = null;
        //是否需要添加数据，以保存完整树结构
        boolean addCellFlag = false;
        // 存储数据列的集合
        List<CiConstructTarget> constructTargetList = new ArrayList<>(excelDataList.size());
        // 存放数据，最后会转成json存到CiConstructTarget的data属性中
        Map<Integer, String[]> dataColumnMap = new HashMap<>();
        // 在读取每一行数据的时候，用来判断是否读到数据列的单元格
        int ifDataIndex = -1;
        //保存架构指标单元格数据以及树形层级关系
        for (int i = 0; i < excelDataList.size(); i++) {
            //取出每行数据
            Map<Integer, String> rowMap = excelDataList.get(i);
            // 表头单独处理
            if (i == 0) {
                for (Integer col : rowMap.keySet()) {
                    //取出单元格数据
                    String cell = rowMap.get(col);
                    // 如果表头的单元格中有#，则说明该单元格所在的列属于数据列
                    if (cell.contains("#")) {
                        // 保证该变量保存的永远是数据列中的第一列
                        if (ifDataIndex == -1) {
                            ifDataIndex = col;
                        }
                        CiConstructTarget ciConstructTarget = new CiConstructTarget();
                        ciConstructTarget.setTargetName(cell.replace("#", ""));
                        ciConstructTarget.setBelongColumnIndex(col);
                        ciConstructTarget.setCiFrameworkObjectId(ciFrameworkObject.getId());
                        constructTargetList.add(ciConstructTarget);
                    }
                    CiFrameworkIndicator ciFrameworkIndicator = new CiFrameworkIndicator();
                    ciFrameworkIndicator.setHeadFlag(true);
                    ciFrameworkIndicator.setIndicatorName(cell);
                    ciFrameworkIndicator.setIndicatorLevel(col);
                    ciFrameworkIndicator.setCiFrameworkObjectId(ciFrameworkObject.getId());
                    ciFrameworkIndicatorService.save(ciFrameworkIndicator);
                }
                continue;
            }
            // 如果某一行的第一个单元格不是null，则说明这是某棵完整的树的开始一行
            if (rowMap.get(0) != null) {
                // 重置保存完整树id的集合
                entireSubtree = new ArrayList<>();
                // 需要添加单元格 标志设为TRUE
                addCellFlag = true;
            }
            // 保存单元格数据
            for (Integer col : rowMap.keySet()) {
                //取出单元格数据
                String cell = rowMap.get(col);
                if (cell == null) {
                    continue;
                }
                CiFrameworkIndicator ciFrameworkIndicator = new CiFrameworkIndicator();
                ciFrameworkIndicator.setHeadFlag(false);
                ciFrameworkIndicator.setIndicatorName(cell);
                ciFrameworkIndicator.setIndicatorLevel(col);
                ciFrameworkIndicator.setCiFrameworkObjectId(ciFrameworkObject.getId());
                ciFrameworkIndicatorService.save(ciFrameworkIndicator);

                // 保存数据列中的数据到集合
                // 判断该单元格是否属于数据列
                if (col >= ifDataIndex) {
                    for (CiConstructTarget ciConstructTarget : constructTargetList) {
                        // 取出构建对象所属的列，也就是数据列的下标
                        Integer belongColumnIndex = ciConstructTarget.getBelongColumnIndex();
                        // 数据对象所属列和当前单元格所属的列相同时才保存数据
                        if (belongColumnIndex.equals(col)) {
                            // 设置数组长度时 -1 是因为excelDataList中含有表头
                            String[] oneDataArr = dataColumnMap.computeIfAbsent(belongColumnIndex, k -> new String[excelDataList.size() - 1]);

                            Double cellData = 0D;
                            if (cell.contains("%")) {
                                String cellDataStr = cell.replaceAll("%", "");
                                try {
                                    cellData = Double.valueOf(cellDataStr);
                                    // 减一时因为表头那一行已经跳过，此时的i是从1开始的
                                    oneDataArr[i - 1] = String.valueOf(cellData / 100D);
                                } catch (NumberFormatException e) {
                                    LOGGER.error(e.getMessage(), e);
                                    throw new Exception("第" + i + "行，第" + col + "列单元格数据无法识别为数字，请重新编辑");
                                }
                            } else {
                                // 减一时因为表头那一行已经跳过，此时的i是从1开始的
                                oneDataArr[i - 1] = cell;
                            }
                        }
                    }
                }

                // 第一个单元格不为null，说明这是这棵完整的子树的开始
                if (addCellFlag) {
                    //将每个单元格对象的id放入集合，以保存整个完整树结构
                    entireSubtree.add(ciFrameworkIndicator.getId());
                    // 如果是最后一列，则说明完整的树结构构建完成
                    if (col == rowMap.keySet().size() - 1) {
                        // 创建用来存储完整树结构层级关系的集合
                        List<CiFrameworkTreepath> oneRowTreePathList = new ArrayList<>();
                        for (int m = 0; m < entireSubtree.size(); m++) {
                            for (int n = m; n < entireSubtree.size(); n++) {
                                //将每个树结构关系对象放入集合
                                oneRowTreePathList.add(new CiFrameworkTreepath(entireSubtree.get(m), entireSubtree.get(n), n - m, ciFrameworkObject.getId()));
                            }
                        }
                        //保存完整的树结构关系
                        ciFrameworkTreepathService.saveBatch(oneRowTreePathList);
                    }
                } else { //else 说明该行第一个单元格是空，也就是说这行中的数据是完整树的子节点数据
                    //如果不是重新构建完整子树的第一行数据，则更新该集合数据，保证和遍历到的该行数据一致
                    entireSubtree.set(col, ciFrameworkIndicator.getId());
                    //存储该行树形关系的集合
                    List<CiFrameworkTreepath> oneRowTreePathList = new ArrayList<>();
                    // 添加自己指向自己的节点
                    oneRowTreePathList.add(new CiFrameworkTreepath(ciFrameworkIndicator.getId(), ciFrameworkIndicator.getId(), 0, ciFrameworkObject.getId()));
                    // 从第一个不为null的节点开始创建关系：
                    for (int m = 0; m < col; m++) {
                        oneRowTreePathList.add(new CiFrameworkTreepath(entireSubtree.get(m), ciFrameworkIndicator.getId(), ciFrameworkIndicator.getIndicatorLevel() - m, ciFrameworkObject.getId()));
                    }
                    ciFrameworkTreepathService.saveBatch(oneRowTreePathList);
                }
            }
            addCellFlag = false;
        }
        ciFrameworkObject.setDataFirstColumn(ifDataIndex);
        ciFrameworkObjectService.updateById(ciFrameworkObject);
        // 把map中的数据转字符串保存到对象属性中
        for (Integer columnIndex : dataColumnMap.keySet()) {
            for (CiConstructTarget ciConstructTarget : constructTargetList) {
                Integer belongColumnIndex = ciConstructTarget.getBelongColumnIndex();
                if (belongColumnIndex.equals(columnIndex)) {
                    try {
                        ciConstructTarget.setData(new ObjectMapper().writeValueAsString(dataColumnMap.get(columnIndex)));
                    } catch (JsonProcessingException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        }
        ciConstructTargetService.saveBatch(constructTargetList);
    }

    /**
     * 读取本地Excel文件并解析数据保存到数据库
     * <p>1. 创建excel对应的实体对象 参照{@link }
     * <p>2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link }
     * <p>3. 直接读即可
     */
    public boolean resolveLocalExcel() throws FileNotFoundException {

        //获取根目录
        File fileRootPath = new File(ResourceUtils.getURL("classpath:").getPath());
        LOGGER.info("根路径为： " + fileRootPath);
        //如果上传目录为/static/images/upload/，则可以如下获取：
//        File upload = new File(path.getAbsolutePath(), "static/images/upload/");
        //在开发测试模式时，得到的地址为：{项目跟目录}/target/static/images/upload/
        //在打包成jar正式发布时，得到的地址为：{发布jar包目录}/static/images/upload/
        File filePath = new File(fileRootPath.getAbsolutePath(), "upload/raw_data_23_excel.xlsx");
//        System.out.println("filePath url:" + filePath.getAbsolutePath());
        if (!filePath.exists()) {
            LOGGER.error("目标文件" + filePath.getAbsolutePath() + "不存在");
            throw new FileNotFoundException("文件不存在");
        }

        // 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
        // 写法1：
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        LOGGER.info("开始读取excel文件，文件路径是：" + filePath.getAbsolutePath());

        // 写法2：
        /*ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(filePath, TechnologyAchievementIndex.class, new TAIListener()).build();
            ReadSheet readSheet = EasyExcel.readSheet(0).build();
            excelReader.read(readSheet);
        } finally {
            if (excelReader != null) {
                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                excelReader.finish();
            }
        }*/
        return true;
    }
}
