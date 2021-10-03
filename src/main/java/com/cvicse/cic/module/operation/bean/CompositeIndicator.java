package com.cvicse.cic.module.operation.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class CompositeIndicator {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 综合指标架构对象id
     */
    private Long DataIndicatorSystemId;

    /**
     * 计算得到的综合指标
     */
    private String comIndicator;

    /**
     * 所用算法
     */
    private String useAlgorithm;
}
