package com.cvicse.cic.module.datasource.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataIndicatorSystemNode implements Comparable<DataIndicatorSystemNode> {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 指标名称
     */
    private String indicatorName;

    /**
     * 指标层级
     */
    private Integer indicatorLevel;

    /**
     * 是否是表头
     */
    private boolean headFlag;

    /**
     * 指数体系id
     */
    private Long dataIndicatorSystemId;

    @Override
    public int compareTo(@NotNull DataIndicatorSystemNode o) {
        return this.getId().compareTo(o.getId());
    }
}
