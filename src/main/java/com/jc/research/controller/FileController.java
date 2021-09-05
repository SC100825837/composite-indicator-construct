package com.jc.research.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.jc.research.entity.TechnologyAchievementIndex;
import com.jc.research.service.impl.FileServiceImpl;
import com.jc.research.util.R;
import com.jc.research.util.excel.TAIListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.io.FileNotFoundException;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileServiceImpl fileService;

    @GetMapping("/upload")
    public R uploadFile() {
        boolean resolveFlag = false;
        try {
            resolveFlag = fileService.readResolveExcel();
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
