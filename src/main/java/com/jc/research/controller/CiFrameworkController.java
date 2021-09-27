package com.jc.research.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jc.research.entity.CiFrameworkObject;
import com.jc.research.service.CiFrameworkObjectService;
import com.jc.research.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
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
@RequestMapping("/ciFrameworkObject")
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

	/**
	 * 获取最新添加的架构对象
	 * @return
	 */
	@GetMapping("getRecentlyId")
	public R getRecentlyCiFrameworkObjectId() {
		Long recentlyCiFrameworkObjectId = ciFrameworkObjectService.getRecentlyCiFrameworkObjectId();
		return R.ok(recentlyCiFrameworkObjectId);
	}

	@GetMapping("/getCiFrameworkObjectInfo/{ciFrameworkObjectId}")
	public R getCiFrameworkObjectInfo(@PathVariable("ciFrameworkObjectId") Long ciFrameworkObjectId) {
		Map<String, Object> ciFrameworkObjectInfoMap;
		try {
			ciFrameworkObjectInfoMap = ciFrameworkObjectService.getCiFrameworkObjectInfo(ciFrameworkObjectId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return R.failed(e.getMessage());
		}
		return R.ok(ciFrameworkObjectInfoMap);
	}

	@GetMapping("/delete/{ciFrameworkObjectId}")
	public R deleteCiFrameworkObject(@PathVariable("ciFrameworkObjectId") Long ciFrameworkObjectId, HttpServletRequest request) {
		log.info("ip地址为- " + request.getRemoteAddr() + " -的用户，发起了删除请求");
		ciFrameworkObjectService.deleteCiFrameworkObjectById(ciFrameworkObjectId);
		return R.ok();
	}

	@GetMapping("switchFrameObj/{ciFrameworkObjectId}")
	public R switchFrameObj(@PathVariable("ciFrameworkObjectId") Long ciFrameworkObjectId) {
		ciFrameworkObjectService.switchFrameObj(ciFrameworkObjectId);
		return R.ok();
	}

}
