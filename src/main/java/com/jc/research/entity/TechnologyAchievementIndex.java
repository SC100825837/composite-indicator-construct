package com.jc.research.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *科技成就指数实体类
 * 该对象的属性顺序必须和数据库表“technology_achievement_index”中字段顺序一致，不要随便更改
 */
@Data
public class TechnologyAchievementIndex {

    private Long id;

    private String countryName;
    /**
     *专利
     */
    private Double patents;

    /**
     * 版权
     */
    private Double royalties;

    /**
     * 互联网
     */
    private Double internet;

    /**
     * 出口
     */
    private Double exports;

    /**
     * 电话
     */
    private Double telephones;

    /**
     * 电力
     */
    private Double electricity;

    /**
     * 学校
     */
    private Double schooling;

    /**
     * 大学
     */
    private Double university;
}
