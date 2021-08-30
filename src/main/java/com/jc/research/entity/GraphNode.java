package com.jc.research.entity;

import lombok.*;

/**
 * @program: constructing-composite-indicators
 * @description: 图节点
 * @author: SunChao
 * @create: 2021-08-27 14:47
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class GraphNode extends Graph {

    private Long id;
    /**
     * 标签名
     */
    private String lbName;
}
