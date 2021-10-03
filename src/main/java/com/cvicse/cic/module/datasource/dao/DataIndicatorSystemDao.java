package com.cvicse.cic.module.datasource.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cvicse.cic.module.datasource.bean.DataIndicatorSystem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DataIndicatorSystemDao extends BaseMapper<DataIndicatorSystem> {
    int save(DataIndicatorSystem dataIndicatorSystem);

    int deleteDataIndicatorSystemById(@Param("dataIndicatorSystemId") Long dataIndicatorSystemId);
}
