package com.jc.research.entity.algorithm.result;

import lombok.Data;
import java.util.Arrays;
import java.util.Map;

/**
 * @program: constructing-composite-indicators
 * @description: 因子分析的过程结果
 * @author: SunChao
 * @create: 2021-08-23 15:28
 **/
@Data
public class FactorAnalysisPR implements ProcessResult {

    /**
     * 旋转因子负荷矩阵，判断相关性
     * 除了指标个数之外，最后两行保存 Expl.Var、Expl./Tot
     */
    private Double[][]  rotatedFactorLoadingsMatrix;

    /**
     * 特征值矩阵，三列分别是：特征值、方差（%）、累积方差（%）
     * 这里的百分数转换成了小数
     */
    private Double[][] eigenvalues;

    /**
     * 平方因子加载矩阵,按比例缩放，列和为1
     */
    private Double[][] squaredFactorLoadingMatrix;

    /**
     * 指标权重
     */
    private Double[][] indicatorWeight;

    /**
     * 最终结果，用来做上层判断
     */
    private Double[][] finalResult;

    /*private Map<String, Double> finalResult;

    public Map<String, Double> getFinalResult() {
        return getFinalResultMap();
    }

    @Override
    public Double[][] getFinalResultArr() {
        Double[][] Doubles = new Double[1][this.finalResult.size()];
        int count = 0;
        for (String indicatorName : this.finalResult.keySet()) {
            Doubles[0][count++] = this.finalResult.get(indicatorName);
        }
        return Doubles;
    }

    @Override
    public Map<String, Double> getFinalResultMap() {
        return this.finalResult;
    }*/
}
