package com.jc.research.controller;

import com.jc.research.service.impl.FileServiceImpl;
import com.jc.research.util.R;
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
    private FileServiceImpl fileService;

    /**
     * 解析上传的文件并保存到数据库和minio
     * @param file
     * @return
     */
    @PostMapping("upload")
    public R upload(@RequestPart("file") MultipartFile file) {
        boolean resolveFlag = false;
        try {
            resolveFlag = fileService.resolveUploadExcel(file);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.failed(e.getMessage());
        }
        if (resolveFlag) {
            return R.ok(null, "上传并保存成功");
        } else {
            return R.failed("上传失败");
        }
    }

    @GetMapping("/resolveLocalFile")
    public R resolveLocalFile() {
        boolean resolveFlag;
        try {
            resolveFlag = fileService.resolveLocalExcel();
        } catch (FileNotFoundException e) {
            return R.failed("解析失败,文件不存在");
        }
        if (resolveFlag) {
            return R.ok(null, "解析并保存成功");
        } else {
            return R.failed("解析失败");
        }
    }
}
