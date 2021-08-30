package com.jc.research.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

/**
 * @program: constructing-composite-indicators
 * @description:
 * @author: SunChao
 * @create: 2021-08-26 09:35
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Country {

    @TableId(value = "id", type = IdType.AUTO)
//    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    private String countryName;

    private String baseIndicator;

    private String compositeIndicator;

}
