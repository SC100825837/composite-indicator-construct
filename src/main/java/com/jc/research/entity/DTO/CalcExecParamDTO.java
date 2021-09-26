package com.jc.research.entity.DTO;

import com.jc.research.entity.CiConstructTarget;
import lombok.Data;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
