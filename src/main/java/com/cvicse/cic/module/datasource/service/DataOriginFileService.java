package com.cvicse.cic.module.datasource.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cvicse.cic.module.datasource.bean.DataOriginFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

public interface DataOriginFileService extends IService<DataOriginFile> {

    /**
     * 解析web excel文件
     * @param file
     * @return
     */
    void handleExcel(MultipartFile file);

    boolean uploadToMinio(MultipartFile file, String bucketName) throws Exception;

    /**
     * 解析本地部署环境中的excel
     * @return
     * @throws FileNotFoundException
     */
    boolean resolveLocalExcel() throws FileNotFoundException;
}
