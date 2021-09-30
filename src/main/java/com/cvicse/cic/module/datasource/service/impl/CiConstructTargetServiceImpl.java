package com.cvicse.cic.module.datasource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cvicse.cic.module.datasource.bean.CiConstructTarget;
import com.cvicse.cic.module.datasource.service.CiConstructTargetService;
import com.cvicse.cic.module.datasource.dao.CiConstructTargetDao;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: composite-indicator-construct
 * @description:
 * @author: SunChao
 * @create: 2021-08-25 17:57
 **/
@Service
public class CiConstructTargetServiceImpl extends ServiceImpl<CiConstructTargetDao, CiConstructTarget> implements CiConstructTargetService {

    @Override
    public List<CiConstructTarget> getAllTargetsByFrameworkId(Long frameworkId) {
        return this.list(new QueryWrapper<CiConstructTarget>().eq("ci_framework_object_id", frameworkId));
    }
}
