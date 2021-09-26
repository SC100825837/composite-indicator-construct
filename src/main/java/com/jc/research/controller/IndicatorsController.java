package com.jc.research.controller;

import com.jc.research.entity.DTO.CalcExecParamDTO;
import com.jc.research.entity.DTO.CalcResultGraphDTO;
import com.jc.research.entity.DTO.GraphDTO;
import com.jc.research.entity.DTO.ProcessResultDTO;
import com.jc.research.service.impl.IndicatorsServiceImpl;
import com.jc.research.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @program: neo4j
 * @description:
 * @author: SunChao
 * @create: 2021-06-23 15:53
 **/
@Slf4j
@RestController
@RequestMapping("/indicator")
public class IndicatorsController {

	@Autowired
	private IndicatorsServiceImpl indicatorsServiceImpl;

	@GetMapping("/getBaseGraph/{ciFrameworkObjectId}")
	public R getBaseGraph(@PathVariable("ciFrameworkObjectId") Long ciFrameworkObjectId) {
		GraphDTO baseGraph = indicatorsServiceImpl.getBaseGraph(ciFrameworkObjectId);
		if (baseGraph == null) {
			return R.failed("数据为空");
		}
		return R.ok(baseGraph);
	}

	@PostMapping("/indicatorCalc")
	public R execIndCalc(@RequestBody CalcExecParamDTO calcExecParam) {
		CalcResultGraphDTO calcResultGraphDTO;
		try {
			calcResultGraphDTO = indicatorsServiceImpl.calcHandler(calcExecParam);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
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

	/*@GetMapping("/getOriginDataList/{targetId}")
	public R<Double[][]> getOriginDataList(@PathVariable("targetId") Long targetId) {
		Double[][] originDataArray = indicatorsServiceImpl.getOriginDataArray(targetId);
		if (originDataArray == null || originDataArray.length == 0) {
			return R.failed("数据为空");
		}
		return R.ok(originDataArray);
	}*/

	@GetMapping("/getProcessResult/{ciFrameworkObjectId}")
	public R<ProcessResultDTO> getProcessResult(@PathVariable("ciFrameworkObjectId") Long ciFrameworkObjectId) {
		ProcessResultDTO processData = indicatorsServiceImpl.getProcessData(ciFrameworkObjectId);
		if (processData == null) {
			return R.failed("数据为空");
		}
		return R.ok(processData);
	}

	@GetMapping("/resetData")
	public R resetData() {
		boolean resetFlag = indicatorsServiceImpl.resetData();
		if (resetFlag) {
			return R.ok("数据已重置");
		} else {
			return R.failed("数据重置失败，请重试");
		}
	}

}
