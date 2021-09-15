package com.jc.research.entity;

import lombok.*;

/**
 * @program: composite-indicator-construct
 * @description: 图节点
 * @author: SunChao
 * @create: 2021-08-27 14:47
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphNode extends Graph {

    private Long id;
    /**
     * 标签名
     */
    private String lbName;

    /**
     * 类别
     */
    private int category;

    /**
     * 父节点id
     */
    private Long parentId;
}
