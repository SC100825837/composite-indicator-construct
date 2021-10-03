package com.cvicse.cic.module.datasource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cvicse.cic.module.datasource.bean.DataIndicatorSystemData;
import com.cvicse.cic.module.datasource.service.DataIndicatorSystemDataService;
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
public class DataIndicatorSystemDataServiceImpl extends ServiceImpl<CiConstructTargetDao, DataIndicatorSystemData> implements DataIndicatorSystemDataService {

    @Override
    public List<DataIndicatorSystemData> getAllTargetsByFrameworkId(Long frameworkId) {
        return this.list(new QueryWrapper<DataIndicatorSystemData>().eq("data_indicator_system_id", frameworkId));
    }
}
