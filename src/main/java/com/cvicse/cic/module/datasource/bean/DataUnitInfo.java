package com.cvicse.cic.module.datasource.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class DataUnitInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 单位名称
     */
    private String unitName;

}
