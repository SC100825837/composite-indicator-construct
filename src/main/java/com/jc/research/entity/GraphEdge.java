package com.jc.research.entity;

import lombok.*;

/**
 * @program: composite-indicator-construct
 * @description:
 * @author: SunChao
 * @create: 2021-08-27 14:49
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphEdge extends Graph {
    /**
     * 起点名称
     */
    private Long sourceID;
    /**
     * 终点名称
     */
    private Long targetID;
}
