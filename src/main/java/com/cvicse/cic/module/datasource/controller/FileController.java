package com.cvicse.cic.module.datasource.controller;

import com.cvicse.cic.module.datasource.service.FileService;
import com.cvicse.cic.util.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileNotFoundException;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 解析上传的文件并保存到数据库和minio
     * @param file
     * @return
     */
    @PostMapping("resolveExcel")
    public ResultData upload(@RequestPart("file") MultipartFile file) {
        fileService.resolveExcel(file);
        return ResultData.success(null, "上传并保存成功");
    }

    @GetMapping("/resolveLocalFile")
    public ResultData resolveLocalFile() {
        boolean resolveFlag;
        try {
            resolveFlag = fileService.resolveLocalExcel();
        } catch (FileNotFoundException e) {
            return ResultData.fail("解析失败,文件不存在");
        }
        if (resolveFlag) {
            return ResultData.success(null, "解析并保存成功");
        } else {
            return ResultData.fail("解析失败");
        }
    }
}
