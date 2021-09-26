package com.jc.research.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jc.research.entity.CiConstructTarget;
import com.jc.research.entity.CiFrameworkObject;
import com.jc.research.entity.DTO.CalcExecParamDTO;
import com.jc.research.entity.DTO.CalcResultGraphDTO;
import com.jc.research.entity.DTO.GraphDTO;
import com.jc.research.entity.DTO.ProcessResultDTO;
import com.jc.research.entity.TechnologyAchievementIndex;
import com.jc.research.service.AlgorithmService;
import com.jc.research.service.CiFrameworkObjectService;
import com.jc.research.service.impl.IndicatorsServiceImpl;
import com.jc.research.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @program:
 * @description:
 * @author: SunChao
 * @create: 2021-06-23 15:53
 **/
@RestController
@RequestMapping("/CiFrameworkObject")
public class CiFrameworkController {

	@Autowired
	private CiFrameworkObjectService ciFrameworkObjectService;

	/**
	 * 分页查询
	 * @param page
	 * @param ciFrameworkObject
	 * @return
	 */
	@GetMapping("/page")
	public R<IPage> page(Page page, CiFrameworkObject ciFrameworkObject) {
		return R.ok(ciFrameworkObjectService.page(page, Wrappers.query(ciFrameworkObject)));
	}

	@GetMapping("/list")
	public R<List<CiFrameworkObject>> getCiFrameworkObjectList() {
		return R.ok(ciFrameworkObjectService.list());
	}

	/**
	 * excel文件预览
	 * @param ciObjId
	 * @param maxDepth
	 * @return
	 */
	@GetMapping("/previewExcelContent/{ciObjId}/{maxDepth}")
	public R previewExcelContent(@PathVariable("ciObjId") Long ciObjId, @PathVariable("maxDepth") Integer maxDepth) {
		List<Map<Integer, String>> excelContent = ciFrameworkObjectService.previewExcelContent(ciObjId, maxDepth);
		return R.ok(excelContent);
	}

	@GetMapping("getRecentlyId")
	public R getRecentlyCiFrameworkObjectId() {
		Long recentlyCiFrameworkObjectId = ciFrameworkObjectService.getRecentlyCiFrameworkObjectId();
		return R.ok(recentlyCiFrameworkObjectId);
	}
}
