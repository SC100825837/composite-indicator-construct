package com.jc.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jc.research.entity.Country;
import com.jc.research.entity.TechnologyAchievementIndex;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TAIMapper extends BaseMapper<TechnologyAchievementIndex> {
    List<Country> getAllCountryList();
}
