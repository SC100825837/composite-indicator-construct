package com.jc.research.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jc.research.entity.TechnologyAchievementIndex;
import com.jc.research.mapper.TAIMapper;
import com.jc.research.service.TAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TAIServiceImpl extends ServiceImpl<TAIMapper, TechnologyAchievementIndex> implements TAIService {

    @Autowired
    private TAIMapper taiMapper;

}
