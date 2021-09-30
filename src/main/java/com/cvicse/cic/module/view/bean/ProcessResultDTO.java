package com.cvicse.cic.module.view.bean;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class ProcessResultDTO {

    /**
     * 原始数据
     * List<TechnologyAchievementIndex>
     */
    private Map<String, Object> originalData = new HashMap<>();

    /**
     * 缺失值插补
     * List<TechnologyAchievementIndex>
     */
    private Map<String, Object> missDataImputation = new HashMap<>();

    /**
     * 多变量分析
     * Map<String, CoordinateDTO>
     */
    private Map<String, Object> multivariateAnalysis = new HashMap<>();

    /**
     * 标准化
     * List<TechnologyAchievementIndex>
     */
    private Map<String, Object> normalisation = new HashMap<>();

    /**
     * 权重和聚合
     * Map<String, CoordinateDTO>
     */
    private Map<String, Object> weightingAndAggregation = new HashMap<>();

}
