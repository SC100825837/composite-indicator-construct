package com.jc.research.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * @program: constructing-composite-indicators
 * @description: 前端传递的算法执行参数封装对象
 * @author: SunChao
 * @create: 2021-08-30 09:37
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalcExecParamDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private AlgorithmsDTO algorithms;

    private IndicatorConstructTargetDTO indicatorConstructTarget;
}
