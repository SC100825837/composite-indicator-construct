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
}
