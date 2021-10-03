package com.cvicse.cic.module.datasource.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class DataUnitData implements Comparable<DataUnitData> {

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
    public int compareTo(@NotNull DataUnitData o) {
        return this.getBelongColumnIndex().compareTo(o.getBelongColumnIndex());
    }
}
