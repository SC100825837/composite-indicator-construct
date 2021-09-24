package com.jc.research.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jc.research.entity.CiFrameworkIndicator;
import com.jc.research.mapper.CiFrameworkIndicatorMapper;
import com.jc.research.mapper.CiFrameworkObjectMapper;
import com.jc.research.service.CiFrameworkIndicatorService;
import com.jc.research.service.CiFrameworkObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CiFrameworkIndicatorServiceImpl extends ServiceImpl<CiFrameworkIndicatorMapper, CiFrameworkIndicator> implements CiFrameworkIndicatorService {

    @Autowired
    private CiFrameworkIndicatorMapper ciFrameworkIndicatorMapper;

}
