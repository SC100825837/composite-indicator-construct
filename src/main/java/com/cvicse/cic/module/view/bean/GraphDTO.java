package com.cvicse.cic.module.view.bean;

import com.cvicse.cic.module.view.bean.GraphEdge;
import com.cvicse.cic.module.view.bean.GraphNode;
import lombok.*;

import java.util.List;

/**
 * @program: composite-indicator-construct
 * @description:
 * @author: SunChao
 * @create: 2021-08-27 15:36
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphDTO {

    private List<GraphNode> nodes;

    private List<GraphEdge> edges;
}
