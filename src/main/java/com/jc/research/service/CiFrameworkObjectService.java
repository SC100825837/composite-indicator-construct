package com.jc.research.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jc.research.entity.CiFrameworkObject;
import org.springframework.web.bind.annotation.PathVariable;

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

    int deleteCiFrameworkObjectById(Long ciFrameworkObjectId);

    Map<String, Object> getCiFrameworkObjectInfo(Long ciFrameworkObjectId) throws Exception;

    void switchFrameObj(Long ciFrameworkObjectId);
}
