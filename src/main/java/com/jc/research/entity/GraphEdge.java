package com.jc.research.entity;

import lombok.*;

/**
 * @program: constructing-composite-indicators
 * @description:
 * @author: SunChao
 * @create: 2021-08-27 14:49
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class GraphEdge extends Graph {
    /**
     * 起点id
     */
    private Long sourceID;
    /**
     * 终点id
     */
    private Long targetID;
}
