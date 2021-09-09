package com.jc.research.entity.algorithm.result;

import lombok.Data;

import java.util.Arrays;

/**
 * @program: constructing-composite-indicators
 * @description: 一系列算法执行结果的最终对象，用来保存 缺失数据插补、多变量分析、标准化、权重和聚合等方法执行后的结果
 * 每个属性对应着每一个步骤的算法结果
 * @author: SunChao
 * @create: 2021-08-17 16:21
 **/
@Data
public class AlgorithmExecResult {
    private Double[][] missDataImputation;
    private ProcessResult multivariateAnalysis;
    private Double[][] normalisation;
    private ProcessResult weightingAndAggregation;

}
