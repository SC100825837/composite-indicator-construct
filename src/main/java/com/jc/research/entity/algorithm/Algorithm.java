package com.jc.research.entity.algorithm;

import lombok.*;
import org.springframework.data.annotation.Transient;

/**
 * @program: composite-indicator-construct
 * @description:
 * @author: SunChao
 * @create: 2021-08-25 19:58
 **/
@Data
public class Algorithm {

    private Long id;

    private String algorithmName;

    private String displayName;

    /**
     * 获取算法的执行顺序，在实现类中定义该属性
     */
    private int execOrder;

    /**
     * 获取算法所在的步骤名称，在实现类中定义该属性
     */
    private String stepName;

    @Transient
    private String fullClassName;

    /**
     * 算法执行的方法
     * @return
     */
    public <T> T exec() {
        return null;
    }

    public <T> T exec(Double[][] matrix) {
        return null;
    }

}
