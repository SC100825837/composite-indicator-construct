package com.jc.research.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: constructing-composite-indicators
 * @description:
 * @author: SunChao
 * @create: 2021-08-23 14:32
 **/
public class AlgorithmUtil {

    /**
     * 将字符串类型的二维数组转换成double类型的二维数组
     * @param str
     * @return
     */
    public static double[][] toDoubleArray(String str) {
        if (str == null || str.equals("")) {
            return new double[1][1];
        }
        String[][] strings = JSON.parseObject(str, String[][].class);
        double[][] doubles = new double[strings.length][strings[0].length];
        for (int i = 0; i < strings.length; i++) {
            for (int j = 0; j < strings[i].length; j++) {
                doubles[i][j] = Double.parseDouble(strings[i][j]);
            }
        }
        return doubles;
    }

    /**
     * 将字符串数组中的每个字符串转成double类型的二维数组并返回
     * @param strArr
     * @return
     */
    public static List<double[][]> toDoubleArray(String[] strArr) {
        if (strArr.length == 0) {
            return null;
        }
        List<double[][]> doubles = new ArrayList<>(strArr.length);
        for (String string : strArr) {
            doubles.add(toDoubleArray(string));
        }
        return doubles;
    }

    /**
     * 计算标准差
     * @param variance 方差
     * @return
     */
    public static double getStandardDeviation(double variance) {
        return Math.sqrt(variance);
    }

    /**
     * 计算方差
     * @param doubles
     * @param average
     * @return
     */
    public static double getVariance(double[] doubles, double average, int count) {
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
    public static double getAverage(double sumNumber, int count) {
        return sumNumber / count;
    }

    /**
     * 求和方法
     * @param doubles
     * @return
     */
    public static double getSum(double[] doubles) {
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
    public static double[][] transposeMatrix(double[][] originMatrix) {
        double[][] transposeMatrix = new double[originMatrix[0].length][originMatrix.length];
        for (int i = 0; i < originMatrix.length; i++) {
            for (int j = 0; j < originMatrix[i].length; j++) {
                transposeMatrix[j][i] = originMatrix[i][j];
            }
        }
        return transposeMatrix;
    }
}
