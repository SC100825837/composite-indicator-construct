package com.jc.research.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CiConstructTarget {


    @TableId(type = IdType.AUTO)
    private Long id;

    private String targetName;

    private Integer belongColumnIndex;

    private String data;

}
