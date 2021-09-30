package com.cvicse.cic.module.datasource.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cvicse.cic.module.datasource.bean.CiFrameworkObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CiFrameworkObjectDao extends BaseMapper<CiFrameworkObject> {
    int save(CiFrameworkObject ciFrameworkObject);

    int deleteCiFrameworkObjectById(@Param("ciFrameworkObjectId") Long ciFrameworkObjectId);
}
