package com.jc.research.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CiConstructTarget implements Comparable<CiConstructTarget> {


    @TableId(type = IdType.AUTO)
    private Long id;

    private String targetName;

    private Integer belongColumnIndex;

    private Long ciFrameworkObjectId;

    private String data;

    @Override
    public int compareTo(@NotNull CiConstructTarget o) {
        return this.getBelongColumnIndex().compareTo(o.getBelongColumnIndex());
    }
}
