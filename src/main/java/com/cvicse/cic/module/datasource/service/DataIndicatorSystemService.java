package com.cvicse.cic.module.datasource.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cvicse.cic.module.datasource.bean.DataIndicatorSystem;

import java.util.List;
import java.util.Map;

/**
 * @program: composite-indicator-construct
 * @description:
 * @author: SunChao
 * @create: 2021-08-25 18:56
 **/
public interface DataIndicatorSystemService extends IService<DataIndicatorSystem> {

    List<Map<Integer, String>> previewExcelContent(Long ciObjId, Integer maxDepth);

    Long getRecentlyDataIndicatorSystemId();

    boolean deleteDataIndicatorSystemById(Long dataIndicatorSystemId);

    Map<String, Object> getDataIndicatorSystemInfo(Long dataIndicatorSystemId) throws Exception;

    void switchFrameObj(Long dataIndicatorSystemId);
}
