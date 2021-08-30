package com.jc.research.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jc.research.entity.AlgorithmExecStep;
import com.jc.research.indicatorAl.algorithm.Algorithm;

import java.util.List;
import java.util.Map;

/**
 * @program: constructing-composite-indicators
 * @description:
 * @author: SunChao
 * @create: 2021-08-25 18:56
 **/
public interface AlgorithmService extends IService<Algorithm> {

    Map<String, List<Algorithm>> getAllAlgorithmsByStepName();

    List<AlgorithmExecStep> getAllAlgorithmSteps();

}