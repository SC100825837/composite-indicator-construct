package com.jc.research.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jc.research.entity.CiFrameworkObject;

import java.util.List;
import java.util.Map;

/**
 * @program: composite-indicator-construct
 * @description:
 * @author: SunChao
 * @create: 2021-08-25 18:56
 **/
public interface CiFrameworkObjectService extends IService<CiFrameworkObject> {

    List<Map<Integer, String>> previewExcelContent(Long ciObjId, Integer maxDepth);

    Long getRecentlyCiFrameworkObjectId();
}
