package com.cvicse.cic.handler.facade;

import com.cvicse.cic.module.algorithm.bean.context.AlgorithmContext;
import com.cvicse.cic.module.algorithm.bean.Algorithm;
import com.cvicse.cic.module.algorithm.bean.result.AlgorithmExecResult;
import com.cvicse.cic.handler.factory.AlgorithmFactory;

import java.util.Map;

/**
 * @program: composite-indicator-construct
 * @description: 算法门面
 * @author: SunChao
 * @create: 2021-08-17 16:16
 **/
public class AlgorithmFacade {
    public static AlgorithmExecResult calculate(Map<String, String> algorithmMap, Double[][] originMatrix) {
        //根据算法名称，从数据库查找算法的全类名
//        Map<String, String> specificAlgorithmFromDB = getSpecificAlgorithmFromDB(algorithmMap);
        //初始化算法对象
        Map<String, Algorithm> algorithmInstanceMap = AlgorithmFactory.getAlgorithm(algorithmMap);
        //产生一个算法上下文
        AlgorithmContext deductionContext = new AlgorithmContext(algorithmInstanceMap, originMatrix);
        //执行算法并返回结果
        return deductionContext.exec();
    }

    /**
     * 根据前端传来的算法名称，从数据库中查找算法的全类名
     * @param algorithmMap
     * @return
     */
    /*private static Map<String, String> getSpecificAlgorithmFromDB(Map<String, String> algorithmMap) {
        Map<String, String> algorithmFullClassNameMap = new HashMap<>();
        for (String stepName : algorithmMap.keySet()) {
            String algorithmName = algorithmMap.get(stepName);
            String value = AlgorithmDB.valueOf(algorithmName).getValue();
            algorithmFullClassNameMap.put(stepName, value);
        }
        return algorithmFullClassNameMap;
    }*/
}
