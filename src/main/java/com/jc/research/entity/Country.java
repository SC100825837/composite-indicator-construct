package com.jc.research.entity;

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
@ToString
@Getter
@Setter
public class Country {

    private Long id;

    private String countryName;

    private String baseIndicator;

    private String compositeIndicator;

}
