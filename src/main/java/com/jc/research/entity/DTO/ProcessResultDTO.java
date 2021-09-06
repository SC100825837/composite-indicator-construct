package com.jc.research.entity.DTO;

import com.jc.research.entity.TechnologyAchievementIndex;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProcessResultDTO {

    /**
     * 原始数据
     */
    private List<TechnologyAchievementIndex> originalData;

    /**
     * 缺失值插补
     */
    private List<TechnologyAchievementIndex> missDataImputation;

    /**
     * 多变量分析
     */
    private CoordinateDTO multivariateAnalysis;

    /**
     * 标准化
     */
    private List<TechnologyAchievementIndex> normalisation;

    /**
     * 权重和聚合
     */
    private List<CoordinateDTO> weightingAndAggregation = new ArrayList<>();

}
