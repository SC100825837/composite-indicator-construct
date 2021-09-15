package com.jc.research.indicatorAl.factory;

import com.jc.research.entity.algorithm.Algorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: composite-indicator-construct
 * @description: 算法工厂,根据算法名称创建算法对象
 * @author: SunChao
 * @create: 2021-08-17 16:07
 **/
public class AlgorithmFactory {
    public static Map<String, Algorithm> getAlgorithm(Map<String, String> algorithmClassNameMap) {
        //创建存放算法实例的map
        Map<String, Algorithm> algorithmInstanceMap = new HashMap<>(algorithmClassNameMap.size());
        try {
            for (String key : algorithmClassNameMap.keySet()) {
                String fullClassName = algorithmClassNameMap.get(key);
                if (fullClassName.equals("")) {
                    continue;
                }
                algorithmInstanceMap.put(key, (Algorithm) Class.forName(fullClassName).newInstance());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return algorithmInstanceMap;
    }
}
