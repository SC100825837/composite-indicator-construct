package com.cvicse.cic.module.algorithm.controller;

import com.cvicse.cic.module.algorithm.bean.AlgorithmExecStep;
import com.cvicse.cic.util.R;
import com.cvicse.cic.module.algorithm.bean.Algorithm;
import com.cvicse.cic.module.algorithm.service.AlgorithmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @program: composite-indicator-construct
 * @description:
 * @author: SunChao
 * @create: 2021-08-25 19:45
 **/
@RestController
@RequestMapping("/algorithm")
public class AlgorithmController {

    @Autowired
    private AlgorithmService algorithmService;

    @GetMapping("/getAllAlgorithms")
    public R<Map<String, List<Algorithm>>> getAllAlgorithmsByStepName() {
        Map<String, List<Algorithm>> allAlgorithm = algorithmService.getAllAlgorithmsByStepName();
        if (allAlgorithm.isEmpty()) {
            return R.failed(null, "算法为空");
        }
        return R.ok(allAlgorithm);
    }

    @GetMapping("/getAllAlgorithmSteps")
    public R<List<AlgorithmExecStep>> getAllAlgorithmSteps() {
        List<AlgorithmExecStep> allAlgorithmSteps = algorithmService.getAllAlgorithmSteps();
        if (allAlgorithmSteps.isEmpty()) {
            return R.failed(null, "数据为空");
        }
        return R.ok(allAlgorithmSteps);
    }
}
