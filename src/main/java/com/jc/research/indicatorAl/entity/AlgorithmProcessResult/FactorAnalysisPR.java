package com.jc.research.indicatorAl.entity.AlgorithmProcessResult;

import java.util.Arrays;

/**
 * @program: constructing-composite-indicators
 * @description: 因子分析的过程结果
 * @author: SunChao
 * @create: 2021-08-23 15:28
 **/
public class FactorAnalysisPR implements ProcessResult {

    /**
     * 旋转因子负荷矩阵，判断相关性
     * 除了指标个数之外，最后两行保存 Expl.Var、Expl./Tot
     */
    private double[][]  rotatedFactorLoadingsMatrix;

    /**
     * 特征值矩阵，三列分别是：特征值、方差（%）、累积方差（%）
     * 这里的百分数转换成了小数
     */
    private double[][] eigenvalues;

    /**
     * 平方因子加载矩阵,按比例缩放，列和为1
     */
    private double[][] squaredFactorLoadingMatrix;

    /**
     * 指标权重
     */
    private double[][] indicatorWeight;

    /**
     * 最终结果，用来做上层判断
     */
    private double[][] finalResult;

    public FactorAnalysisPR() {
    }

    public FactorAnalysisPR(double[][] rotatedFactorLoadingsMatrix, double[][] eigenvalues, double[][] squaredFactorLoadingMatrix, double[][] indicatorWeight, double[][] finalResult) {
        this.rotatedFactorLoadingsMatrix = rotatedFactorLoadingsMatrix;
        this.eigenvalues = eigenvalues;
        this.squaredFactorLoadingMatrix = squaredFactorLoadingMatrix;
        this.indicatorWeight = indicatorWeight;
        this.finalResult = finalResult;
    }

    @Override
    public String toString() {
        return "FactorAnalysisPR{" +
                "rotatedFactorLoadingsMatrix=" + Arrays.toString(rotatedFactorLoadingsMatrix) +
                ", eigenvalues=" + Arrays.toString(eigenvalues) +
                ", squaredFactorLoadingMatrix=" + Arrays.toString(squaredFactorLoadingMatrix) +
                ", indicatorWeight=" + Arrays.toString(indicatorWeight) +
                ", finalResult=" + Arrays.toString(finalResult) +
                '}';
    }

    public double[][] getRotatedFactorLoadingsMatrix() {
        return rotatedFactorLoadingsMatrix;
    }

    public void setRotatedFactorLoadingsMatrix(double[][] rotatedFactorLoadingsMatrix) {
        this.rotatedFactorLoadingsMatrix = rotatedFactorLoadingsMatrix;
    }

    public double[][] getEigenvalues() {
        return eigenvalues;
    }

    public void setEigenvalues(double[][] eigenvalues) {
        this.eigenvalues = eigenvalues;
    }

    public double[][] getSquaredFactorLoadingMatrix() {
        return squaredFactorLoadingMatrix;
    }

    public void setSquaredFactorLoadingMatrix(double[][] squaredFactorLoadingMatrix) {
        this.squaredFactorLoadingMatrix = squaredFactorLoadingMatrix;
    }

    public double[][] getIndicatorWeight() {
        return indicatorWeight;
    }

    public void setIndicatorWeight(double[][] indicatorWeight) {
        this.indicatorWeight = indicatorWeight;
    }

    public double[][] getFinalResult() {
        return finalResult;
    }

    public void setFinalResult(double[][] finalResult) {
        this.finalResult = finalResult;
    }
}
