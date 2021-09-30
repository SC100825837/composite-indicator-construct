package com.cvicse.cic.module.view.bean;

import lombok.Data;
import java.io.Serializable;

/**
 * @program: composite-indicator-construct
 * @description: 前端传递的算法执行参数封装对象
 * @author: SunChao
 * @create: 2021-08-30 09:37
 **/
@Data
public class CalcExecParamDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private AlgorithmsDTO algorithms;

    private Long targetId;

    private Double[][] modifiedDataList;

    private Long ciFrameworkObjectId;

}
