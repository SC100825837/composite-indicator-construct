package com.cvicse.cic.module.datasource.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cvicse.cic.module.datasource.bean.CiFrameworkIndicator;
import com.cvicse.cic.module.datasource.dao.CiFrameworkIndicatorDao;
import com.cvicse.cic.module.datasource.service.CiFrameworkIndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CiFrameworkIndicatorServiceImpl extends ServiceImpl<CiFrameworkIndicatorDao, CiFrameworkIndicator> implements CiFrameworkIndicatorService {

    @Autowired
    private CiFrameworkIndicatorDao ciFrameworkIndicatorDao;

}
