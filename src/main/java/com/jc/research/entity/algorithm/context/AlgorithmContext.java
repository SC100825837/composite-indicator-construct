package com.jc.research.entity.algorithm.context;

import com.jc.research.entity.algorithm.Algorithm;
import com.jc.research.entity.algorithm.result.AlgorithmExecResult;
import com.jc.research.util.ContainProcessResult;
import com.jc.research.entity.algorithm.result.ProcessResult;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @program: constructing-composite-indicators
 * @description: 算法包装类,对外提供算法的执行方法，其余方法进行封装
 * @author: SunChao
 * @create: 2021-08-17 16:11
 **/
public class AlgorithmContext {
    /**
     * 算法实例化之后的map，key是步骤名称，value是算法实例
     */
    private Map<String, Algorithm> algorithmInstanceMap;

    /**
     * 算法链集合，按照顺序将算法实例放入
     */
    private Algorithm[] algorithmChainList;

    /**
     * 数据输入源，二维数组表示矩阵
     */
    private double[][] originMatrix;

    public AlgorithmContext(Map<String, Algorithm> algorithmInstanceMap, double[][] originMatrix) {
        this.algorithmInstanceMap = algorithmInstanceMap;
        this.originMatrix = originMatrix;
        this.algorithmChainList = buildAlgorithmChain();
    }

    /**
     * 构建算法顺序链
     * @return
     */
    private Algorithm[] buildAlgorithmChain() {
        Algorithm[] algorithmChainList = new Algorithm[4];
        for (String stepName : this.algorithmInstanceMap.keySet()) {
            Algorithm algorithm = this.algorithmInstanceMap.get(stepName);
            algorithmChainList[algorithm.getExecOrder()] = algorithm;
        }
        return algorithmChainList;
    }

    /**
     * 算法执行
     * @return
     */
    public AlgorithmExecResult exec() {
        //创建算法执行后的最终返回对象，每个属性对应着每一个步骤的算法结果
        AlgorithmExecResult algorithmExecResult = new AlgorithmExecResult();
        //获取算法结果对象的类对象
        Class<? extends AlgorithmExecResult> aClass = algorithmExecResult.getClass();

        double[][] matrix = originMatrix;
        //按照顺讯遍历算法链
        for (int i = 0; i < this.algorithmChainList.length; i++) {
            //如果有的步骤省略了，那么这步骤对应的算法可能为空
            if (this.algorithmChainList[i] == null) {
                continue;
            }

            try {
                //将算法实例中的步骤名称取出，通过此名称找到算法结果对象中  对应该步骤的属性，并设置结果值
                Field field = aClass.getDeclaredField(this.algorithmChainList[i].getStepName());
                field.setAccessible(true);
                //如果被注解标注，则说明该算法具有过程结果，该方法的返回值是个对象不是二维数组
                if (this.algorithmChainList[i].getClass().isAnnotationPresent(ContainProcessResult.class)) {
                    ProcessResult result = this.algorithmChainList[i].exec(matrix);
                    field.set(algorithmExecResult, result);
                    if(i < this.algorithmChainList.length) {
                        matrix = result.getFinalResult();
                    }
                } else {
                    matrix = this.algorithmChainList[i].exec(matrix);
                    field.set(algorithmExecResult, matrix);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return algorithmExecResult;
    }

}
