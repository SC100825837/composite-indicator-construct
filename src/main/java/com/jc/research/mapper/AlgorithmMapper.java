package com.jc.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jc.research.entity.algorithm.Algorithm;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AlgorithmMapper extends BaseMapper<Algorithm> {

    List<Algorithm> getAllAlgorithms();
}
