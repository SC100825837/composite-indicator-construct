package com.cvicse.cic.module.datasource.controller;

import com.cvicse.cic.module.datasource.bean.CiConstructTarget;
import com.cvicse.cic.util.R;
import com.cvicse.cic.module.datasource.service.CiConstructTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @program: composite-indicator-construct
 * @description:
 * @author: SunChao
 * @create: 2021-08-25 19:45
 **/
@RestController
@RequestMapping("/constructTarget")
public class CiConstructTargetController {

    @Autowired
    private CiConstructTargetService ciConstructTargetService;

    @GetMapping("/getAllCiConstructTargets/{ciFrameworkObjectId}")
    public R<List<CiConstructTarget>> getAllCiConstructTargets(@PathVariable("ciFrameworkObjectId") Long ciFrameworkObjectId) {
        return R.ok(ciConstructTargetService.getAllTargetsByFrameworkId(ciFrameworkObjectId));
    }

}
