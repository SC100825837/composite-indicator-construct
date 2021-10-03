package com.cvicse.cic.module.datasource.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class DataIndicatorSystem {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 指标体系名称
     */
    private String indicatorSystemName;

    /**
     * 最深的层级
     */
    private Integer maxDepth;

    /**
     * 所属源文件id
     */
    private Long originFileId;
}
