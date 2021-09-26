package com.jc.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jc.research.entity.AlgorithmExecStep;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AlgorithmExecStepMapper extends BaseMapper<AlgorithmExecStep> {

    List<AlgorithmExecStep> getAllAlgorithmSteps();
}
