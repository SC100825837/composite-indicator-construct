package com.jc.research.indicatorAl.facade;

import com.jc.research.indicatorAl.algorithm.Algorithm;
import com.jc.research.indicatorAl.algorithm.AlgorithmContext;
import com.jc.research.indicatorAl.algorithm.AlgorithmDB;
import com.jc.research.indicatorAl.entity.AlgorithmExecResult;
import com.jc.research.indicatorAl.factory.AlgorithmFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: constructing-composite-indicators
 * @description: 算法门面
 * @author: SunChao
 * @create: 2021-08-17 16:16
 **/
public class AlgorithmFacade {
    public static AlgorithmExecResult calculate(Map<String, String> algorithmMap) {
        //根据算法名称，从数据库查找算法的全类名
//        Map<String, String> specificAlgorithmFromDB = getSpecificAlgorithmFromDB(algorithmMap);
        //初始化算法对象
        Map<String, Algorithm> algorithmInstanceMap = AlgorithmFactory.getAlgorithm(algorithmMap);
        //产生一个算法上下文
        AlgorithmContext deductionContext = new AlgorithmContext(algorithmInstanceMap);
        //执行算法并返回结果
        return deductionContext.exec();
    }

    /**
     * 根据前端传来的算法名称，从数据库中查找算法的全类名
     * @param algorithmMap
     * @return
     */
    private static Map<String, String> getSpecificAlgorithmFromDB(Map<String, String> algorithmMap) {
        Map<String, String> algorithmFullClassNameMap = new HashMap<>();
        for (String stepName : algorithmMap.keySet()) {
            String algorithmName = algorithmMap.get(stepName);
            String value = AlgorithmDB.valueOf(algorithmName).getValue();
            algorithmFullClassNameMap.put(stepName, value);
        }
        return algorithmFullClassNameMap;
    }
}
