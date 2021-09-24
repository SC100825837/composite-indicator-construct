package com.jc.research.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CiFrameworkTreepath {

    /**
     * 祖先id
     */
    @TableId(type = IdType.INPUT)
    private Long ancestor;

    /**
     * 后代id
     */
    @TableId(type = IdType.INPUT)
    private Long descendant;

    /**
     * 节点深度
     */
    private Integer pathDepth;

    /**
     * 综合指标架构对象id
     */
    private Long ciFrameworkObjectId;
}
