package com.cvicse.cic.module.datasource.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cvicse.cic.module.datasource.bean.CiFrameworkObject;

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

    boolean deleteCiFrameworkObjectById(Long ciFrameworkObjectId);

    Map<String, Object> getCiFrameworkObjectInfo(Long ciFrameworkObjectId) throws Exception;

    void switchFrameObj(Long ciFrameworkObjectId);
}
