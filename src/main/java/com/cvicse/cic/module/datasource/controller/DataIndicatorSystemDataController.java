package com.cvicse.cic.module.datasource.controller;

import com.cvicse.cic.module.datasource.bean.DataIndicatorSystemData;
import com.cvicse.cic.util.ResultData;
import com.cvicse.cic.module.datasource.service.DataIndicatorSystemDataService;
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
public class DataIndicatorSystemDataController {

    @Autowired
    private DataIndicatorSystemDataService dataIndicatorSystemDataService;

    @GetMapping("/getAllCiConstructTargets/{dataIndicatorSystemId}")
    public ResultData<List<DataIndicatorSystemData>> getAllCiConstructTargets(@PathVariable("dataIndicatorSystemId") Long dataIndicatorSystemId) {
        return ResultData.success(dataIndicatorSystemDataService.getAllTargetsByFrameworkId(dataIndicatorSystemId));
    }

}
