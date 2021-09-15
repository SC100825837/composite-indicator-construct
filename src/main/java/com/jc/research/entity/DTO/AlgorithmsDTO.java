package com.jc.research.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: composite-indicator-construct
 * @description: 接收前端参数封装成对象（key: 算法名称，value:算法id）
 * @author: SunChao
 * @create: 2021-08-30 10:06
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlgorithmsDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long missDataImputation;
    private Long multivariateAnalysis;
    private Long normalisation;
    private Long weightingAndAggregation;

    public Map<String, Long> getAllAlgorithmIds() {
        Map<String, Long> alIdsMap = new HashMap<>();
        alIdsMap.put("missDataImputation", missDataImputation);
        alIdsMap.put("multivariateAnalysis", multivariateAnalysis);
        alIdsMap.put("normalisation", normalisation);
        alIdsMap.put("weightingAndAggregation", weightingAndAggregation);
        return alIdsMap;
    }
}
