package com.cvicse.cic.module.datasource.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataIndicatorSystemTreepath {

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
     * 指数体系id
     */
    private Long dataIndicatorSystemId;
}
