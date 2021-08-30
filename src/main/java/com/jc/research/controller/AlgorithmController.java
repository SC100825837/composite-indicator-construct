package com.jc.research.controller;

import com.jc.research.entity.AlgorithmExecStep;
import com.jc.research.indicatorAl.algorithm.Algorithm;
import com.jc.research.service.AlgorithmService;
import com.jc.research.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @program: constructing-composite-indicators
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
