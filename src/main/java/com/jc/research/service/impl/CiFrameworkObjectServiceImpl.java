package com.jc.research.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jc.research.entity.CiConstructTarget;
import com.jc.research.entity.CiFrameworkIndicator;
import com.jc.research.entity.CiFrameworkObject;
import com.jc.research.entity.DTO.ECharts.HistogramDTO;
import com.jc.research.mapper.CiFrameworkIndicatorMapper;
import com.jc.research.mapper.CiFrameworkObjectMapper;
import com.jc.research.mapper.CiFrameworkTreepathMapper;
import com.jc.research.service.CiConstructTargetService;
import com.jc.research.service.CiFrameworkObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CiFrameworkObjectServiceImpl extends ServiceImpl<CiFrameworkObjectMapper, CiFrameworkObject> implements CiFrameworkObjectService {

    @Autowired
    private CiFrameworkObjectMapper ciFrameworkObjectMapper;

    @Autowired
    private CiFrameworkIndicatorMapper ciFrameworkIndicatorMapper;

    @Autowired
    private CiFrameworkTreepathMapper ciFrameworkTreepathMapper;

    @Autowired
    private CiConstructTargetService ciConstructTargetService;

    @Autowired
    private IndicatorsServiceImpl indicatorsService;

    private Integer currentDepth = 0;

    @Override
    public List<Map<Integer, String>> previewExcelContent(Long ciObjId, Integer maxDepth) {
        /**
         * 实现思路：
         * 先取出表头
         * 取出第一列，也就是层级为0的数据
         * 遍历数据，每次遍历都去递归查询下一深度的数据，查询后继续该步骤
         * 直到深度达到最大
         */
        List<CiFrameworkIndicator> excelHeadList = ciFrameworkIndicatorMapper.selectExcelHead(ciObjId);
        List<CiFrameworkIndicator> cellList = ciFrameworkIndicatorMapper.selectCiIndicatorByCiObjIdAndDepth(ciObjId, 0);
        List<Map<Integer, String>> excelDataList = new ArrayList<>();
        currentDepth = 0;
        getCell(cellList, 1, excelHeadList.size(), excelDataList);
        return excelDataList;
    }

    @Override
    public Long getRecentlyCiFrameworkObjectId() {
        CiFrameworkObject ciFrameworkObject = this.getOne(new QueryWrapper<CiFrameworkObject>()
                .orderByDesc("id")
                .last("limit 1"));
        if (ciFrameworkObject != null) {
            return ciFrameworkObject.getId();
        }
        return null;
    }

    @Override
    @Transactional
    public boolean deleteCiFrameworkObjectById(Long ciFrameworkObjectId) {
        // 这里分开删除用事务控制数据一致性，联表太慢
        this.removeById(ciFrameworkObjectId);
        ciFrameworkIndicatorMapper.deleteById(ciFrameworkObjectId);
        ciFrameworkTreepathMapper.deleteById(ciFrameworkObjectId);
        ciConstructTargetService.removeById(ciFrameworkObjectId);
        return true;
    }

    @Override
    public Map<String, Object> getCiFrameworkObjectInfo(Long ciFrameworkObjectId) throws Exception {
        Map<String, Object> ciFrameworkObjectInfoMap = new HashMap<>();
        ciFrameworkObjectInfoMap.put("allTargetsCic", createHistogram(ciFrameworkObjectId));
        return ciFrameworkObjectInfoMap;
    }

    @Override
    public void switchFrameObj(Long ciFrameworkObjectId) {
        indicatorsService.switchFrameObj();
    }

    private HistogramDTO createHistogram(Long ciFrameworkObjectId) throws Exception {
        List<String> legendData = Arrays.asList("histogram", "line");
        List<String> xAxisData = new ArrayList<>();
        List<CiConstructTarget> list = ciConstructTargetService.list(new QueryWrapper<CiConstructTarget>()
                .eq("ci_framework_object_id", ciFrameworkObjectId)
                .select("target_name", "belong_column_index"));
        if (list == null) {
            throw new Exception("数据为空，请导入数据");
        }
        List<CiConstructTarget> collect = list.stream()
                .sorted(Comparator.comparing(CiConstructTarget::getBelongColumnIndex))
                .collect(Collectors.toList());
        for (CiConstructTarget ciConstructTarget : collect) {
            xAxisData.add(ciConstructTarget.getTargetName());
        }

        List<Map<String, Object>> series = new ArrayList<>();

        Map<String, Object> histogramDataMap = new HashMap<>();
        Map<Long, Double> targetsCompositeIndicatorMap = indicatorsService.getAllFrameObjectComInxMap().get(ciFrameworkObjectId);
        if (targetsCompositeIndicatorMap == null || targetsCompositeIndicatorMap.isEmpty()) {
            throw new Exception("暂未进行指数计算");
        }
        histogramDataMap.put("data", targetsCompositeIndicatorMap.values());
        series.add(histogramDataMap);

        Map<String, Object> lineDataMap = new HashMap<>();
        lineDataMap.put("data", targetsCompositeIndicatorMap.values());
        series.add(lineDataMap);

        HistogramDTO histogramDTO = new HistogramDTO();
        histogramDTO.setLegendData(legendData);
        histogramDTO.setXAxisData(xAxisData);
        histogramDTO.setSeries(series);
        return histogramDTO;
    }

    private List<Map<Integer, String>> getCell(List<CiFrameworkIndicator> cellList, Integer pathDepth, Integer maxDepth, List<Map<Integer, String>> excelDataList) {
        currentDepth++;
        for (CiFrameworkIndicator cell : cellList) {
            if (currentDepth.equals(maxDepth)) {
                for (CiFrameworkIndicator maxDepthCell : cellList) {
                    Map<Integer, String> rowMap = new HashMap<>();
                    rowMap.put(currentDepth, maxDepthCell.getIndicatorName());
                    excelDataList.add(rowMap);
                }
                currentDepth--;
                return excelDataList;
            }
            List<CiFrameworkIndicator> nextCellList = ciFrameworkIndicatorMapper.selectIndicatorByAncestorAndDepth(cell.getId(), pathDepth);
            excelDataList = getCell(nextCellList, pathDepth, maxDepth, excelDataList);
            for (Map<Integer, String> rowMap : excelDataList) {
                if (rowMap.get(currentDepth) != null) {
                    continue;
                }
                rowMap.put(currentDepth, cell.getIndicatorName());
            }
        }
        currentDepth--;
        return excelDataList;
    }
}
