package com.cvicse.cic.module.datasource.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

public interface FileService {

    /**
     * 解析web excel文件
     * @param file
     * @return
     */
    boolean resolveExcel(MultipartFile file);

    /**
     * 解析本地部署环境中的excel
     * @return
     * @throws FileNotFoundException
     */
    boolean resolveLocalExcel() throws FileNotFoundException;
}
