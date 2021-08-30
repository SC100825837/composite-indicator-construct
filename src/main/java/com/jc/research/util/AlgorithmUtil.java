package com.jc.research.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

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

//    public static String[] toStringArray(String str) {
//        String[] strings = JSON.parseObject(str, String[].class);
//
//    }
}
