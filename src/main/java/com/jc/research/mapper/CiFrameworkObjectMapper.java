package com.jc.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jc.research.entity.CiFrameworkObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CiFrameworkObjectMapper extends BaseMapper<CiFrameworkObject> {
    int save(CiFrameworkObject ciFrameworkObject);

    int deleteCiFrameworkObjectById(@Param("ciFrameworkObjectId") Long ciFrameworkObjectId);
}
