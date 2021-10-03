package com.cvicse.cic.module.datasource.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cvicse.cic.handler.MinioTemplate;
import com.cvicse.cic.module.datasource.bean.*;
import com.cvicse.cic.module.datasource.dao.DataIndicatorSystemDao;
import com.cvicse.cic.module.datasource.dao.DataOriginFileDao;
import com.cvicse.cic.module.datasource.service.*;
import com.cvicse.cic.util.CommonConstant;
import com.cvicse.cic.util.excel.NoModelDataListener;
import com.cvicse.cic.util.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Slf4j
@Service
public class DataOriginFileServiceImpl extends ServiceImpl<DataOriginFileDao, DataOriginFile> implements DataOriginFileService {

    @Autowired
    private MinioTemplate minioTemplate;

    @Autowired
    private DataIndicatorSystemService dataIndicatorSystemService;

    @Autowired
    private DataIndicatorSystemNodeService dataIndicatorSystemNodeService;

    @Autowired
    private DataIndicatorSystemTreepathService dataIndicatorSystemTreepathService;

    @Autowired
    private DataIndicatorSystemDataService dataIndicatorSystemDataService;

    /**
     * 读取web上传的excel文件
     * @param file
     * @return
     */
    @Override
    @Transactional
    public void handleExcel(MultipartFile file) {
        try {
            List<Map<Integer, String>> excelDataList = new ArrayList<>();
            //解析excel
            EasyExcel.read(file.getInputStream(), new NoModelDataListener(excelDataList)).sheet().doRead();

            // 校验
            checkExcel(file, excelDataList);

            // 保存到数据库
            FutureTask<DataIndicatorSystem> futureTask = new FutureTask<>(() -> saveExcelDataToDB(file, excelDataList));
            new Thread(futureTask, "saveExcelDataToDBThread").start();

            // 保存excel文件到文件服务器
            uploadToMinio(file, CommonConstant.BUCKET_NAME);
            DataOriginFile dataOriginFile = new DataOriginFile(null, file.getOriginalFilename(), CommonConstant.BUCKET_NAME, file.getOriginalFilename(), null, LocalDateTime.now());
            this.save(dataOriginFile);
            // 从异步任务获取保存的指标体系对象，并设置关联源文件的id
            DataIndicatorSystem dataIndicatorSystem = futureTask.get();
            dataIndicatorSystem.setOriginFileId(dataOriginFile.getId());
            // 更新指标体系关联的源文件id
            dataIndicatorSystemService.updateById(dataIndicatorSystem);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("文件解析失败，请联系技术人员。");
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("保存数据库失败，请联系技术人员。");
        }
    }

    /**
     * 校验excel
     * @param excelDataList
     */
    private void checkExcel(MultipartFile file, List<Map<Integer, String>> excelDataList) {
        DataOriginFile dataOriginFile = this.getOne(new QueryWrapper<DataOriginFile>().eq("origin_file_name", file.getOriginalFilename())
                .last("limit 1"));
        if (dataOriginFile != null) {
            throw new BusinessException("上传失败，文件已存在。");
        }
        if (excelDataList.isEmpty()) {
            throw new BusinessException("解析到数据为空，请检查上传文件.");
        }
        if (excelDataList.size() == 1) {
            throw new BusinessException("解析到只有表头，请检查上传文件.");
        }
        // 获取表头数据，以表头数据为基准进行校验
        Map<Integer, String> excelHeadData = excelDataList.get(0);
        for (int i = 0; i < excelDataList.size(); i++) {
            if (excelDataList.get(i).size() < excelHeadData.size()) {
                throw new BusinessException("上传文件第" + i + "行出现数据缺失，请检查文件。");
            }
            if (excelDataList.get(i).size() > excelHeadData.size()) {
                throw new BusinessException("上传文件第" + i + "行出现冗余数据，请检查文件。");
            }
            /*for (Integer columnIndex : excelDataList.get(i).keySet()) {
                String cell = excelDataList.get(i).get(columnIndex);
                if (cell == null || cell.trim().equals("")) {
                    throw new BusinessException("上传文件第" + i + "行第" + columnIndex + "列单元格数据为空，请检查文件。");
                }
            }*/
        }
    }

    /**
     * 保存解析的excel数据到数据库
     * @param file
     * @param excelDataList
     */
    private DataIndicatorSystem saveExcelDataToDB(MultipartFile file, List<Map<Integer, String>> excelDataList) {
        //保存架构对象
        DataIndicatorSystem dataIndicatorSystem = new DataIndicatorSystem(null, file.getName(), excelDataList.get(0).size() - 1, null);
        dataIndicatorSystemService.save(dataIndicatorSystem);
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
        List<DataIndicatorSystemData> constructTargetList = new ArrayList<>(excelDataList.size());
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
                        DataIndicatorSystemData dataIndicatorSystemData = new DataIndicatorSystemData();
                        dataIndicatorSystemData.setDataHead(cell.replace("#", ""));
                        dataIndicatorSystemData.setBelongColumnIndex(col);
                        dataIndicatorSystemData.setIndicatorSystemId(dataIndicatorSystem.getId());
                        constructTargetList.add(dataIndicatorSystemData);
                    }
                    DataIndicatorSystemNode dataIndicatorSystemNode = new DataIndicatorSystemNode();
                    dataIndicatorSystemNode.setHeadFlag(true);
                    dataIndicatorSystemNode.setIndicatorName(cell);
                    dataIndicatorSystemNode.setIndicatorLevel(col);
                    dataIndicatorSystemNode.setDataIndicatorSystemId(dataIndicatorSystem.getId());
                    dataIndicatorSystemNodeService.save(dataIndicatorSystemNode);
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
                DataIndicatorSystemNode dataIndicatorSystemNode = new DataIndicatorSystemNode();
                dataIndicatorSystemNode.setHeadFlag(false);
                dataIndicatorSystemNode.setIndicatorName(cell);
                dataIndicatorSystemNode.setIndicatorLevel(col);
                dataIndicatorSystemNode.setDataIndicatorSystemId(dataIndicatorSystem.getId());
                dataIndicatorSystemNodeService.save(dataIndicatorSystemNode);

                // 保存数据列中的数据到集合
                // 判断该单元格是否属于数据列
                if (col >= ifDataIndex) {
                    for (DataIndicatorSystemData dataIndicatorSystemData : constructTargetList) {
                        // 取出构建对象所属的列，也就是数据列的下标
                        Integer belongColumnIndex = dataIndicatorSystemData.getBelongColumnIndex();
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
                                    log.error(e.getMessage(), e);
                                    throw new BusinessException("第" + i + "行，第" + col + "列单元格数据无法识别为数字，请重新编辑");
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
                    entireSubtree.add(dataIndicatorSystemNode.getId());
                    // 如果是最后一列，则说明完整的树结构构建完成
                    if (col == rowMap.keySet().size() - 1) {
                        // 创建用来存储完整树结构层级关系的集合
                        List<DataIndicatorSystemTreepath> oneRowTreePathList = new ArrayList<>();
                        for (int m = 0; m < entireSubtree.size(); m++) {
                            for (int n = m; n < entireSubtree.size(); n++) {
                                //将每个树结构关系对象放入集合
                                oneRowTreePathList.add(new DataIndicatorSystemTreepath(entireSubtree.get(m), entireSubtree.get(n), n - m, dataIndicatorSystem.getId()));
                            }
                        }
                        //保存完整的树结构关系
                        dataIndicatorSystemTreepathService.saveBatch(oneRowTreePathList);
                    }
                } else { //else 说明该行第一个单元格是空，也就是说这行中的数据是完整树的子节点数据
                    //如果不是重新构建完整子树的第一行数据，则更新该集合数据，保证和遍历到的该行数据一致
                    entireSubtree.set(col, dataIndicatorSystemNode.getId());
                    //存储该行树形关系的集合
                    List<DataIndicatorSystemTreepath> oneRowTreePathList = new ArrayList<>();
                    // 添加自己指向自己的节点
                    oneRowTreePathList.add(new DataIndicatorSystemTreepath(dataIndicatorSystemNode.getId(), dataIndicatorSystemNode.getId(), 0, dataIndicatorSystem.getId()));
                    // 从第一个不为null的节点开始创建关系：
                    for (int m = 0; m < col; m++) {
                        oneRowTreePathList.add(new DataIndicatorSystemTreepath(entireSubtree.get(m), dataIndicatorSystemNode.getId(), dataIndicatorSystemNode.getIndicatorLevel() - m, dataIndicatorSystem.getId()));
                    }
                    dataIndicatorSystemTreepathService.saveBatch(oneRowTreePathList);
                }
            }
            addCellFlag = false;
        }
        dataIndicatorSystemService.updateById(dataIndicatorSystem);
        // 把map中的数据转字符串保存到对象属性中
        for (Integer columnIndex : dataColumnMap.keySet()) {
            for (DataIndicatorSystemData dataIndicatorSystemData : constructTargetList) {
                Integer belongColumnIndex = dataIndicatorSystemData.getBelongColumnIndex();
                if (belongColumnIndex.equals(columnIndex)) {
                    try {
                        dataIndicatorSystemData.setDataValue(new ObjectMapper().writeValueAsString(dataColumnMap.get(columnIndex)));
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
        dataIndicatorSystemDataService.saveBatch(constructTargetList);
        return dataIndicatorSystem;
    }

    @Override
    public void uploadToMinio(MultipartFile file, String bucketName) {
        try {
            minioTemplate.createBucket(CommonConstant.BUCKET_NAME);
            minioTemplate.putObject(CommonConstant.BUCKET_NAME, file.getOriginalFilename(), file.getInputStream());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("文件上传服务器失败，请联系技术人员。");
        }
    }

    /**
     * 读取本地Excel文件并解析数据保存到数据库
     * <p>1. 创建excel对应的实体对象 参照{@link }
     * <p>2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link }
     * <p>3. 直接读即可
     */
    @Override
    public boolean resolveLocalExcel() throws FileNotFoundException {

        //获取根目录
        File fileRootPath = new File(ResourceUtils.getURL("classpath:").getPath());
        log.info("根路径为： " + fileRootPath);
        //如果上传目录为/static/images/upload/，则可以如下获取：
//        File upload = new File(path.getAbsolutePath(), "static/images/upload/");
        //在开发测试模式时，得到的地址为：{项目跟目录}/target/static/images/upload/
        //在打包成jar正式发布时，得到的地址为：{发布jar包目录}/static/images/upload/
        File filePath = new File(fileRootPath.getAbsolutePath(), "upload/raw_data_23_excel.xlsx");
//        System.out.println("filePath url:" + filePath.getAbsolutePath());
        if (!filePath.exists()) {
            log.error("目标文件" + filePath.getAbsolutePath() + "不存在");
            throw new FileNotFoundException("文件不存在");
        }

        // 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
        // 写法1：
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        log.info("开始读取excel文件，文件路径是：" + filePath.getAbsolutePath());

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
