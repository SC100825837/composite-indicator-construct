package com.jc.research.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jc.research.entity.AlgorithmExecStep;
import com.jc.research.entity.algorithm.Algorithm;
import com.jc.research.mapper.AlgorithmExecStepMapper;
import com.jc.research.mapper.AlgorithmMapper;
import com.jc.research.service.AlgorithmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: composite-indicator-construct
 * @description:
 * @author: SunChao
 * @create: 2021-08-25 17:57
 **/
@Service
public class AlgorithmServiceImpl extends ServiceImpl<AlgorithmMapper, Algorithm> implements AlgorithmService {

    @Autowired
    private AlgorithmMapper algorithmMapper;

    @Autowired
    private AlgorithmExecStepMapper algorithmExecStepMapper;

    @Override
    public Map<String, List<Algorithm>> getAllAlgorithmsByStepName() {
        //数据库查询所有算法
        List<Algorithm> allAlgorithms = algorithmMapper.getAllAlgorithms();
        //按照步骤名称分组
        Map<String, List<Algorithm>> allAlgorithmsByStepName = allAlgorithms.stream()
                .collect(Collectors.groupingBy(Algorithm::getStepName));

        //当查询结果出现 有的步骤算法数据为空时，添加空值
        List<AlgorithmExecStep> allAlgorithmSteps = algorithmExecStepMapper.getAllAlgorithmSteps();
        for (AlgorithmExecStep algorithmStep : allAlgorithmSteps) {
            if (!allAlgorithmsByStepName.containsKey(algorithmStep.getStepValue())) {
                allAlgorithmsByStepName.put(algorithmStep.getStepValue(), null);
            }
        }
        return allAlgorithmsByStepName;
    }

    @Override
    public List<AlgorithmExecStep> getAllAlgorithmSteps() {
        return algorithmExecStepMapper.getAllAlgorithmSteps();
    }
}
