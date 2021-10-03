package com.cvicse.cic.module.datasource.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cvicse.cic.module.datasource.bean.DataIndicatorSystem;
import com.cvicse.cic.module.datasource.service.DataIndicatorSystemService;
import com.cvicse.cic.util.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @program:
 * @description:
 * @author: SunChao
 * @create: 2021-06-23 15:53
 **/
@Slf4j
@RestController
@RequestMapping("/dataIndicatorSystem")
public class DataIndicatorSystemController {

	@Autowired
	private DataIndicatorSystemService dataIndicatorSystemService;

	/**
	 * 分页查询
	 * @param page
	 * @param dataIndicatorSystem
	 * @return
	 */
	@GetMapping("/page")
	public ResultData<IPage> page(Page page, DataIndicatorSystem dataIndicatorSystem) {
		return ResultData.success(dataIndicatorSystemService.page(page, Wrappers.query(dataIndicatorSystem)));
	}

	@GetMapping("/list")
	public ResultData<List<DataIndicatorSystem>> getDataIndicatorSystemList() {
		return ResultData.success(dataIndicatorSystemService.list());
	}

	/**
	 * excel文件预览
	 * @param ciObjId
	 * @param maxDepth
	 * @return
	 */
	@GetMapping("/previewExcelContent/{ciObjId}/{maxDepth}")
	public ResultData previewExcelContent(@PathVariable("ciObjId") Long ciObjId, @PathVariable("maxDepth") Integer maxDepth) {
		List<Map<Integer, String>> excelContent = dataIndicatorSystemService.previewExcelContent(ciObjId, maxDepth);
		return ResultData.success(excelContent);
	}

	/**
	 * 获取最新添加的架构对象
	 * @return
	 */
	@GetMapping("getRecentlyId")
	public ResultData getRecentlyDataIndicatorSystemId() {
		Long recentlyDataIndicatorSystemId = dataIndicatorSystemService.getRecentlyDataIndicatorSystemId();
		if (recentlyDataIndicatorSystemId == null) {
			return ResultData.fail("数据为空，请导入数据");
		}
		return ResultData.success(recentlyDataIndicatorSystemId);
	}

	@GetMapping("/getDataIndicatorSystemInfo/{DataIndicatorSystemId}")
	public ResultData getDataIndicatorSystemInfo(@PathVariable("DataIndicatorSystemId") Long DataIndicatorSystemId) {
		Map<String, Object> DataIndicatorSystemInfoMap;
		try {
			DataIndicatorSystemInfoMap = dataIndicatorSystemService.getDataIndicatorSystemInfo(DataIndicatorSystemId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ResultData.fail(e.getMessage());
		}
		return ResultData.success(DataIndicatorSystemInfoMap);
	}

	@GetMapping("/delete/{DataIndicatorSystemId}")
	public ResultData deleteDataIndicatorSystem(@PathVariable("DataIndicatorSystemId") Long DataIndicatorSystemId, HttpServletRequest request) {
		log.info("ip地址为- " + request.getRemoteAddr() + " -的用户，发起了删除请求");
		dataIndicatorSystemService.deleteDataIndicatorSystemById(DataIndicatorSystemId);
		return ResultData.success();
	}

	@GetMapping("switchFrameObj/{DataIndicatorSystemId}")
	public ResultData switchFrameObj(@PathVariable("DataIndicatorSystemId") Long DataIndicatorSystemId) {
		dataIndicatorSystemService.switchFrameObj(DataIndicatorSystemId);
		return ResultData.success();
	}

}
