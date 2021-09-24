package com.jc.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jc.research.entity.CiFrameworkObject;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CiFrameworkObjectMapper extends BaseMapper<CiFrameworkObject> {
    int save(CiFrameworkObject ciFrameworkObject);
}
