package com.cvicse.cic.module.view.bean;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: composite-indicator-construct
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
