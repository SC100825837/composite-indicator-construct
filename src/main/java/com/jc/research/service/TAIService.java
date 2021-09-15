package com.jc.research.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jc.research.entity.Country;
import com.jc.research.entity.TechnologyAchievementIndex;

import java.util.List;

/**
 * @program: composite-indicator-construct
 * @description:
 * @author: SunChao
 * @create: 2021-08-25 18:56
 **/
public interface TAIService extends IService<TechnologyAchievementIndex> {

    List<Country> getAllCountryList();
}
