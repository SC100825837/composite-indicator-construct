package com.jc.research.entity.algorithm;

import com.jc.research.util.AlgorithmExecOrder;
import com.jc.research.util.AlgorithmUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: constructing-composite-indicators
 * @description: z-scores
 * @author: SunChao
 * @create: 2021-08-17 16:03
 **/
@Data
public class ZScores extends Algorithm {

    private int execOrder = AlgorithmExecOrder.NORMALISATION;

    private String stepName = "normalisation";

    @Override
    public double[][] exec(double[][] matrix) {
        double[][] transposeMatrix = transposeMatrix(matrix);
        double[][] normalizationMatrix = new double[matrix[0].length][matrix.length];
        for (int i = 0; i < transposeMatrix.length; i++) {
            //计算平均值
            double average = getAverage(getSum(transposeMatrix[i]), transposeMatrix[i].length);
            //计算标准差
            double standardDeviation = getStandardDeviation(getVariance(transposeMatrix[i], average, transposeMatrix[i].length));
            for (int j = 0; j < transposeMatrix[i].length; j++) {
                double standardisation = (transposeMatrix[i][j] - average) / standardDeviation;
                normalizationMatrix[i][j] = standardisation;
            }
        }
        return transposeMatrix(normalizationMatrix);
    }

    @Override
    public int getExecOrder() {
        return execOrder;
    }

    @Override
    public String getStepName() {
        return stepName;
    }

    /**
     * 计算标准差
     * @param variance 方差
     * @return
     */
    public double getStandardDeviation(double variance) {
        return Math.sqrt(variance);
    }

    /**
     * 计算方差
     * @param doubles
     * @param average
     * @return
     */
    public double getVariance(double[] doubles, double average, int count) {
        double variance = 0;
        for (Double doubleNum : doubles) {
            variance += (doubleNum - average) * (doubleNum - average);
        }
        return variance / count;
    }

    /**
     * 计算平均值
     * @param sumNumber 求和后的数值
     * @param count 数据总个数
     * @return
     */
    public double getAverage(double sumNumber, int count) {
        return sumNumber / count;
    }

    /**
     * 求和方法
     * @param doubles
     * @return
     */
    private double getSum(double[] doubles) {
        double sum = 0L;
        for (double number : doubles) {
            sum += number;
        }
        return sum;
    }

    /**
     * 矩阵转置
     * @param originMatrix
     * @return
     */
    private double[][] transposeMatrix(double[][] originMatrix) {
        double[][] transposeMatrix = new double[originMatrix[0].length][originMatrix.length];
        for (int i = 0; i < originMatrix.length; i++) {
            for (int j = 0; j < originMatrix[i].length; j++) {
                transposeMatrix[j][i] = originMatrix[i][j];
            }
        }
        return transposeMatrix;
    }
}
