package com.cvicse.cic.module.algorithm.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cvicse.cic.module.algorithm.bean.Algorithm;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AlgorithmDao extends BaseMapper<Algorithm> {

    List<Algorithm> getAllAlgorithms();
}
