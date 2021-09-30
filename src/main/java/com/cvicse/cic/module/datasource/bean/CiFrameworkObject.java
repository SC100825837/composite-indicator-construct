package com.cvicse.cic.module.datasource.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class CiFrameworkObject {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String frameworkObjectName;

    private Integer maxDepth;

    private Integer dataFirstColumn;

    private String fileUrl;

    private LocalDateTime uploadDate;

    private String uploaderId;
}
