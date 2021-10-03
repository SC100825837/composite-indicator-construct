package com.cvicse.cic.module.datasource.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cvicse.cic.module.datasource.bean.DataIndicatorSystemData;

import java.util.List;

/**
 * @program: composite-indicator-construct
 * @description:
 * @author: SunChao
 * @create: 2021-08-25 18:56
 **/
public interface DataIndicatorSystemDataService extends IService<DataIndicatorSystemData> {

    List<DataIndicatorSystemData> getAllTargetsByFrameworkId(Long frameworkId);
}
