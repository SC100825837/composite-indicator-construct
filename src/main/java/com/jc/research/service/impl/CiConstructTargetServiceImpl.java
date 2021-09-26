package com.jc.research.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jc.research.entity.CiConstructTarget;
import com.jc.research.mapper.CiConstructTargetMapper;
import com.jc.research.service.CiConstructTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: composite-indicator-construct
 * @description:
 * @author: SunChao
 * @create: 2021-08-25 17:57
 **/
@Service
public class CiConstructTargetServiceImpl extends ServiceImpl<CiConstructTargetMapper, CiConstructTarget> implements CiConstructTargetService {

    @Override
    public List<CiConstructTarget> getAllTargetsByFrameworkId(Long frameworkId) {
        return this.list(new QueryWrapper<CiConstructTarget>().eq("ci_framework_object_id", frameworkId));
    }
}
