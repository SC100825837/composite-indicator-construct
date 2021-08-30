package com.jc.research.entity.DTO;

import com.jc.research.entity.Graph;
import com.jc.research.entity.GraphEdge;
import com.jc.research.entity.GraphNode;
import lombok.*;

import java.util.List;

/**
 * @program: constructing-composite-indicators
 * @description:
 * @author: SunChao
 * @create: 2021-08-27 15:36
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class GraphDTO {

    private List<GraphNode> nodes;

    private List<GraphEdge> edges;
}
