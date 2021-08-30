package com.jc.research.entity.DTO;

import com.jc.research.indicatorAl.entity.AlgorithmExecResult;
import lombok.*;

import java.io.Serializable;

/**
 * @program: constructing-composite-indicators
 * @description: 算法和构造对象的参数封装
 * @author: SunChao
 * @create: 2021-08-27 09:43
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Deprecated
public class CalcResultTierGraphDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 算法执行结果，包括各步骤的计算结果，以及各步骤算法包含的过程结果
     */
    private AlgorithmExecResult algorithmExecResult;

    /**
     * 最终的综合指标值
     */
    private double compositeIndicator;

    /**
     * 带有综合指标数值的图对象
     */
    private TierGraphDTO compIndGraph;
}
