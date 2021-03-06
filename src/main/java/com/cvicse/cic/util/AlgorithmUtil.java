package com.cvicse.cic.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: composite-indicator-construct
 * @description:
 * @author: SunChao
 * @create: 2021-08-23 14:32
 **/
@Slf4j
public class AlgorithmUtil {

    /**
     * 获得python虚拟环境路径
     * @return
     */
    public static String getPythonEnvPath() {
        String pythonEnvPath = PropertiesUtil.getProperties("application.properties").getProperty("python.env.path");
        log.info("加载的Python环境路径为： " + pythonEnvPath);
        return pythonEnvPath;
    }

    /**
     * 获取python算法路径
     * @return
     */
    public static String getPythonAlgorithmPath() {
        String pythonAlPath = PropertiesUtil.getProperties("application.properties").getProperty("python.algorithm.path");
        log.info("加载的Python算法路径为： " + pythonAlPath);
        return pythonAlPath;
    }

    /**
     * 将字符串类型的二维数组转换成Double类型的二维数组
     * @param str
     * @return
     */
    public static Double[][] toDoubleArray(String str) {
        if (str == null || str.equals("")) {
            return null;
        }
        String[][] strings = JSON.parseObject(str, String[][].class);
        Double[][] Doubles = new Double[strings.length][strings[0].length];
        for (int i = 0; i < strings.length; i++) {
            for (int j = 0; j < strings[i].length; j++) {
                Doubles[i][j] = handleFractional(2, Double.parseDouble(strings[i][j]));
            }
        }
        return Doubles;
    }

    /**
     * 将字符串数组中的每个字符串转成Double类型的二维数组并返回
     * @param strArr
     * @return
     */
    public static List<Double[][]> toDoubleArray(String[] strArr) {
        if (strArr.length == 0) {
            return null;
        }
        List<Double[][]> Doubles = new ArrayList<>(strArr.length);
        for (String string : strArr) {
            Doubles.add(toDoubleArray(string));
        }
        return Doubles;
    }

    /**
     * 计算标准差
     * @param variance 方差
     * @return
     */
    public static Double getStandardDeviation(Double variance) {
        return Math.sqrt(variance);
    }

    /**
     * 计算方差
     * @param doubles
     * @param average
     * @return
     */
    public static Double getVariance(Double[] doubles, Double average, int count) {
        Double variance = (double) 0;
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
    public static Double getAverage(Double sumNumber, int count) {
        return sumNumber / count;
    }

    /**
     * 求和方法
     * @param doubles
     * @return
     */
    public static Double getSum(Double[] doubles) {
        Double sum = (double) 0;
        for (Double number : doubles) {
            sum += number;
        }
        return sum;
    }

    /**
     * 矩阵转置
     * @param originMatrix
     * @return
     */
    public static Double[][] transposeMatrix(Double[][] originMatrix) {
        Double[][] transposeMatrix = new Double[originMatrix[0].length][originMatrix.length];
        for (int i = 0; i < originMatrix.length; i++) {
            for (int j = 0; j < originMatrix[i].length; j++) {
                transposeMatrix[j][i] = originMatrix[i][j];
            }
        }
        return transposeMatrix;
    }

    /**
     * 按照规定小数点位数处理小数
     *
     * @param digit
     * @param origin
     * @return
     */
    public static Double handleFractional(int digit, Double origin) {
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setMaximumFractionDigits(digit);
        return Double.parseDouble(numberInstance.format(origin).replaceAll(",", ""));
    }

}
