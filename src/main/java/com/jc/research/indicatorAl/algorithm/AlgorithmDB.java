package com.jc.research.indicatorAl.algorithm;

/**
 * @program: constructing-composite-indicators
 * @description: 模拟算法数据库
 * @author: SunChao
 * @create: 2021-08-17 16:08
 **/
public enum AlgorithmDB {
    FACTOR_ANALYSIS("com.jc.research.indicatorAl.algorithm.FactorAnalysis", "com.jc.research.indicatorAl.entity.AlgorithmProcessResult.FactorAnalysisPR"),
    Z_SCORES("com.jc.research.indicatorAl.algorithm.ZScores", ""),
    TO_SELECT("", "");

    private String value = "";
    private String resultType = "";


    public String getValue() {
        return value;
    }

    public String getResultType() {
        return resultType;
    }

    AlgorithmDB(String value, String resultType) {
        this.value = value;
        this.resultType = resultType;
    }
}
