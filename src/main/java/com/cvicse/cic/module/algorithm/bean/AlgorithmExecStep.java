package com.cvicse.cic.module.algorithm.bean;

import lombok.*;

import java.util.Map;

/**
 * @program: composite-indicator-construct
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
