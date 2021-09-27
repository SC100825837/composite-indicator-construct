package com.jc.research.entity.DTO.ECharts;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class HistogramDTO {

    /**
     * 图例，要展示的名称
     */
    private List<String> legendData;

    /**
     * x轴
     */
    private List<String> xAxisData;

    /**
     * 图例的数据
     */
    private List<Map<String, Object>> series;

}
