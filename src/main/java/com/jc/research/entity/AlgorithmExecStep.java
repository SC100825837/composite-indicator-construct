package com.jc.research.entity;

import lombok.*;

import java.util.Map;

/**
 * @program: constructing-composite-indicators
 * @description:
 * @author: SunChao
 * @create: 2021-08-26 17:49
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlgorithmExecStep {

    private Long id;

    private String stepName;

    private String stepValue;

    private int execOrder;

}
