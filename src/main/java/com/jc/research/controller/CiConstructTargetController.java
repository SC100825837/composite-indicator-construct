package com.jc.research.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jc.research.entity.CiConstructTarget;
import com.jc.research.entity.Country;
import com.jc.research.mapper.CiConstructTargetMapper;
import com.jc.research.service.CiConstructTargetService;
import com.jc.research.service.TAIService;
import com.jc.research.service.impl.IndicatorsServiceImpl;
import com.jc.research.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/getAllCiConstructTargets")
    public R<List<CiConstructTarget>> getAllCiConstructTargets() {
        return R.ok(ciConstructTargetService.list());
    }

}
