package com.cvicse.cic.module.datasource.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataIndicatorSystemData implements Comparable<DataIndicatorSystemData> {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 该数据所在的excel表头名称
     */
    private String dataHead;

    /**
     * 数据值
     */
    private String dataValue;

    /**
     * 数据所属excel的列下标
     */
    private Integer belongColumnIndex;

    /**
     * 基础节点/末级节点id
     */
    private Long baseIndicatorId;

    /**
     * 关联的指标体系id
     */
    private Long indicatorSystemId;

    /**
     * 所属源文件id
     */
    private Long originFileId;

    @Override
    public int compareTo(@NotNull DataIndicatorSystemData o) {
        return this.getBelongColumnIndex().compareTo(o.getBelongColumnIndex());
    }
}
