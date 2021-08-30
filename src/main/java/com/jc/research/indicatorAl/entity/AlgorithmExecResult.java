package com.jc.research.indicatorAl.entity;

import com.jc.research.indicatorAl.entity.AlgorithmProcessResult.ProcessResult;

import java.util.Arrays;

/**
 * @program: constructing-composite-indicators
 * @description: 一系列算法执行结果的最终对象，用来保存 缺失数据填补、多变量分析、标准化、权重和聚合等方法执行后的结果
 * 每个属性对应着每一个步骤的算法结果
 * @author: SunChao
 * @create: 2021-08-17 16:21
 **/
public class AlgorithmExecResult {
    private double[][] missDataImputation;
    private double[][] multivariateAnalysis;
    private double[][] normalisation;
    private ProcessResult weightingAndAggregation;

    public AlgorithmExecResult() {
    }

    public AlgorithmExecResult(double[][] missDataImputation, double[][] multivariateAnalysis, double[][] normalisation, ProcessResult weightingAndAggregation) {
        this.missDataImputation = missDataImputation;
        this.multivariateAnalysis = multivariateAnalysis;
        this.normalisation = normalisation;
        this.weightingAndAggregation = weightingAndAggregation;
    }

    @Override
    public String toString() {
        return "AlgorithmExecResult{" +
                "missDataImputation=" + Arrays.deepToString(missDataImputation) +
                ", multivariateAnalysis=" + Arrays.deepToString(multivariateAnalysis) +
                ", normalisation=" + Arrays.deepToString(normalisation) +
                ", weightingAndAggregation=" + weightingAndAggregation +
                '}';
    }

    public double[][] getMissDataImputation() {
        return missDataImputation;
    }

    public void setMissDataImputation(double[][] missDataImputation) {
        this.missDataImputation = missDataImputation;
    }

    public double[][] getMultivariateAnalysis() {
        return multivariateAnalysis;
    }

    public void setMultivariateAnalysis(double[][] multivariateAnalysis) {
        this.multivariateAnalysis = multivariateAnalysis;
    }

    public double[][] getNormalisation() {
        return normalisation;
    }

    public void setNormalisation(double[][] normalisation) {
        this.normalisation = normalisation;
    }

    public ProcessResult getWeightingAndAggregation() {
        return weightingAndAggregation;
    }

    public void setWeightingAndAggregation(ProcessResult weightingAndAggregation) {
        this.weightingAndAggregation = weightingAndAggregation;
    }
}
