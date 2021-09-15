package com.jc.research.entity.DTO;

import com.jc.research.entity.GraphEdge;
import com.jc.research.entity.GraphNode;
import com.jc.research.entity.algorithm.result.AlgorithmExecResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: composite-indicator-construct
 * @description: 算法和构造对象的参数封装
 * @author: SunChao
 * @create: 2021-08-27 09:43
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalcResultGraphDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 算法执行结果，包括各步骤的计算结果，以及各步骤算法包含的过程结果
     */
    private AlgorithmExecResult algorithmExecResult;

    /**
     * 最终的综合指标值
     */
    private Double compositeIndicator;

    /**
     * 带有综合指标数值的图对象
     */
    private List<GraphNode> compIndGraphNode = new ArrayList<>();
    private List<GraphEdge> compIndGraphEdge = new ArrayList<>();
}
