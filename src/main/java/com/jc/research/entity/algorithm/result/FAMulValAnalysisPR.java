package com.jc.research.entity.algorithm.result;

import lombok.Data;

/**
 * @program: composite-indicator-construct
 * @description: 因子分析的过程结果
 * @author: SunChao
 * @create: 2021-08-23 15:28
 **/
@Data
public class FAMulValAnalysisPR implements ProcessResult {

    /**
     * 旋转因子负荷矩阵，判断相关性
     * 除了指标个数之外，最后两行保存 Expl.Var、Expl./Tot
     */
    private Double[][]  rotatedFactorLoadingsMatrix;

    /**
     * 相关性矩阵
     */
    private Double[][] correlationMatrix;

    /**
     * 多变量分析中最终结果为传进来的原始数据集
     * 因为多变量分析不参与后续计算
     */
    private Double[][] finalResult;

    @Override
    public Double[][] getFinalResult() {
        return this.finalResult;
    }
}
