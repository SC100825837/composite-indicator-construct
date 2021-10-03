package com.cvicse.cic.module.algorithm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cvicse.cic.module.algorithm.bean.AlgorithmExecStep;
import com.cvicse.cic.module.algorithm.bean.Algorithm;
import com.cvicse.cic.module.algorithm.dao.AlgorithmExecStepDao;
import com.cvicse.cic.module.algorithm.dao.AlgorithmDao;
import com.cvicse.cic.module.algorithm.service.AlgorithmService;
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
public class AlgorithmServiceImpl extends ServiceImpl<AlgorithmDao, Algorithm> implements AlgorithmService {

    @Autowired
    private AlgorithmDao algorithmDao;

    @Autowired
    private AlgorithmExecStepDao algorithmExecStepDao;

    @Override
    public Map<String, List<Algorithm>> getAllAlgorithmsByStepName() {
        //数据库查询所有算法
        List<Algorithm> allAlgorithms = algorithmDao.getAllAlgorithms();
        if (allAlgorithms.isEmpty()) {
            throw new RuntimeException("算法为空");
        }
        //按照步骤名称分组
        Map<String, List<Algorithm>> allAlgorithmsByStepName = allAlgorithms.stream()
                .collect(Collectors.groupingBy(Algorithm::getStepName));

        //当查询结果出现 有的步骤算法数据为空时，添加空值
        List<AlgorithmExecStep> allAlgorithmSteps = algorithmExecStepDao.getAllAlgorithmSteps();
        for (AlgorithmExecStep algorithmStep : allAlgorithmSteps) {
            if (!allAlgorithmsByStepName.containsKey(algorithmStep.getStepValue())) {
                allAlgorithmsByStepName.put(algorithmStep.getStepValue(), null);
            }
        }
        return allAlgorithmsByStepName;
    }

    @Override
    public List<AlgorithmExecStep> getAllAlgorithmSteps() {
        List<AlgorithmExecStep> allAlgorithmSteps = algorithmExecStepDao.getAllAlgorithmSteps();
        if (allAlgorithmSteps.isEmpty()) {
            throw new RuntimeException("算法为空");
        }
        return allAlgorithmSteps;
    }
}
