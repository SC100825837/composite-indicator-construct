package com.cvicse.cic.module.view.bean.ECharts;

import lombok.Data;

import java.util.List;

@Data
public class CoordinateDTO {

    private List<String> xAxis;
    private List<String> yAxis;
    private List<List<Double>> data;
    private int maxValue;
    private int minValue;
    private String title;
}
