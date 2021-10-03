package com.cvicse.cic.module.operation.controller;

import com.cvicse.cic.module.view.bean.CalcResultGraphDTO;
import com.cvicse.cic.module.view.bean.ProcessResultDTO;
import com.cvicse.cic.module.operation.service.IndicatorsServiceImpl;
import com.cvicse.cic.util.ResultData;
import com.cvicse.cic.module.view.bean.CalcExecParamDTO;
import com.cvicse.cic.module.view.bean.GraphDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/indicator")
public class IndicatorsController {

	@Autowired
	private IndicatorsServiceImpl indicatorsServiceImpl;

	@GetMapping("/getBaseGraph/{dataIndicatorSystemId}")
	public ResultData getBaseGraph(@PathVariable("dataIndicatorSystemId") Long dataIndicatorSystemId) {
		return ResultData.success(indicatorsServiceImpl.getBaseGraph(dataIndicatorSystemId));
	}

	@PostMapping("/indicatorCalc")
	public ResultData execIndCalc(@RequestBody CalcExecParamDTO calcExecParam) {
		CalcResultGraphDTO calcResultGraphDTO = indicatorsServiceImpl.calcHandler(calcExecParam);
		if (calcResultGraphDTO == null) {
			return ResultData.fail("计算失败，请联系技术员");
		}
		return ResultData.success(calcResultGraphDTO, "计算完成，综合指标数值为：" + calcResultGraphDTO.getCompositeIndicator());
	}

	@PostMapping("/calcMdComposite")
	public ResultData calcMdComposite(@RequestBody Map<String, Double> mdBaseIndicatorMap) {
		Double mdComposite = indicatorsServiceImpl.calcModifyBaseIndicator(mdBaseIndicatorMap);
		return ResultData.success(mdComposite, "计算完成");
	}

	/*@GetMapping("/getOriginDataList/{targetId}")
	public R<Double[][]> getOriginDataList(@PathVariable("targetId") Long targetId) {
		Double[][] originDataArray = indicatorsServiceImpl.getOriginDataArray(targetId);
		if (originDataArray == null || originDataArray.length == 0) {
			return R.failed("数据为空");
		}
		return R.ok(originDataArray);
	}*/

	@GetMapping("/getProcessResult/{dataIndicatorSystemId}")
	public ResultData<ProcessResultDTO> getProcessResult(@PathVariable("dataIndicatorSystemId") Long dataIndicatorSystemId) {
		ProcessResultDTO processData = indicatorsServiceImpl.getProcessData(dataIndicatorSystemId);
		if (processData == null) {
			return ResultData.fail("数据为空");
		}
		return ResultData.success(processData);
	}

	@GetMapping("/resetData")
	public ResultData resetData() {
		boolean resetFlag = indicatorsServiceImpl.resetData();
		if (resetFlag) {
			return ResultData.success("数据已重置");
		} else {
			return ResultData.fail("数据重置失败，请重试");
		}
	}

}
