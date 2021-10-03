package com.cvicse.cic.module.datasource.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cvicse.cic.module.datasource.bean.DataIndicatorSystemNode;
import com.cvicse.cic.module.datasource.dao.CiFrameworkIndicatorDao;
import com.cvicse.cic.module.datasource.service.DataIndicatorSystemNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataIndicatorSystemNodeServiceImpl extends ServiceImpl<CiFrameworkIndicatorDao, DataIndicatorSystemNode> implements DataIndicatorSystemNodeService {

    @Autowired
    private CiFrameworkIndicatorDao ciFrameworkIndicatorDao;

}
