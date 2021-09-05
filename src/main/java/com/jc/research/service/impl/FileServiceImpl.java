package com.jc.research.service.impl;

import com.alibaba.excel.EasyExcel;
import com.jc.research.entity.TechnologyAchievementIndex;
import com.jc.research.util.excel.TAIListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import java.io.File;
import java.io.FileNotFoundException;

@Service
public class FileServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * 读取Excel文件并解析数据保存到数据库
     * <p>1. 创建excel对应的实体对象 参照{@link TechnologyAchievementIndex}
     * <p>2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link TAIListener}
     * <p>3. 直接读即可
     */
    public boolean readResolveExcel() throws FileNotFoundException {

        //获取跟目录
        File fileRootPath = new File(ResourceUtils.getURL("classpath:").getPath());

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
        EasyExcel.read(filePath, TechnologyAchievementIndex.class, new TAIListener()).sheet().doRead();

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
