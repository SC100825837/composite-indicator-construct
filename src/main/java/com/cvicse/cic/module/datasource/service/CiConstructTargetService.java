package com.cvicse.cic.module.datasource.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cvicse.cic.module.datasource.bean.CiConstructTarget;

import java.util.List;

/**
 * @program: composite-indicator-construct
 * @description:
 * @author: SunChao
 * @create: 2021-08-25 18:56
 **/
public interface CiConstructTargetService extends IService<CiConstructTarget> {

    List<CiConstructTarget> getAllTargetsByFrameworkId(Long frameworkId);
}
