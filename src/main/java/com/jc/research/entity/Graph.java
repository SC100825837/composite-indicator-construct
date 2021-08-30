package com.jc.research.entity;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: constructing-composite-indicators
 * @description:
 * @author: SunChao
 * @create: 2021-08-27 14:53
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Graph {
    /**
     * 属性
     */
    private Map<String, Object> attributes = new HashMap<>();

}
