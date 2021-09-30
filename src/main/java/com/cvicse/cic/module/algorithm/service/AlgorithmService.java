package com.cvicse.cic.module.algorithm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cvicse.cic.module.algorithm.bean.AlgorithmExecStep;
import com.cvicse.cic.module.algorithm.bean.Algorithm;

import java.util.List;
import java.util.Map;

/**
 * @program: composite-indicator-construct
 * @description:
 * @author: SunChao
 * @create: 2021-08-25 18:56
 **/
public interface AlgorithmService extends IService<Algorithm> {

    Map<String, List<Algorithm>> getAllAlgorithmsByStepName();

    List<AlgorithmExecStep> getAllAlgorithmSteps();

}
