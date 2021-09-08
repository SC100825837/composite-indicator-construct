package com.jc.research.controller;

import com.jc.research.entity.DTO.CalcExecParamDTO;
import com.jc.research.entity.DTO.CalcResultGraphDTO;
import com.jc.research.entity.DTO.GraphDTO;
import com.jc.research.entity.DTO.ProcessResultDTO;
import com.jc.research.service.AlgorithmService;
import com.jc.research.service.impl.IndicatorsServiceImpl;
import com.jc.research.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @program: neo4j
 * @description:
 * @author: SunChao
 * @create: 2021-06-23 15:53
 **/
@RestController
@RequestMapping("/indicator")
public class IndicatorsController {

	@Autowired
	private IndicatorsServiceImpl indicatorsServiceImpl;

	@Autowired
	private AlgorithmService algorithmService;

	@GetMapping("/getBaseGraph")
	public R getBaseGraph() {
		GraphDTO baseGraph = indicatorsServiceImpl.getBaseGraph();
		if (baseGraph == null) {
			return R.failed("数据为空");
		}
		return R.ok(baseGraph);
	}

	@PostMapping("/indicatorCalc")
	public R execIndCalc(@RequestBody CalcExecParamDTO calcExecParam) {
		CalcResultGraphDTO calcResultGraphDTO;
		try {
			calcResultGraphDTO = indicatorsServiceImpl.handleDataAndAlgorithm(calcExecParam);
		} catch (Exception e) {
			return R.failed(e.getMessage());
		}
		if (calcResultGraphDTO == null) {
			return R.failed("数据不存在");
		}
		return R.ok(calcResultGraphDTO, "计算完成，综合指标数值为：" + calcResultGraphDTO.getCompositeIndicator());
	}

	@PostMapping("/calcMdComposite")
	public R calcMdComposite(@RequestBody Map<String, Double> mdBaseIndicatorMap) {
		Double mdComposite = indicatorsServiceImpl.calcModifyBaseIndicator(mdBaseIndicatorMap);
		return R.ok(mdComposite, "计算完成");
	}

	@GetMapping("/getProcessResult")
	public R<ProcessResultDTO> getProcessResult() {
		ProcessResultDTO processData = indicatorsServiceImpl.getProcessData();
		if (processData == null) {
			return R.failed("数据为空");
		}
		return R.ok(processData);
	}

	@GetMapping("/getSecondNodes")
	public void getSecondNodesByFirstNodeName() {
		indicatorsServiceImpl.getSecondNodesByFirstNodeName();
	}

}
