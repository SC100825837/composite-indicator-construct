package com.cvicse.cic.module.datasource.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cvicse.cic.module.datasource.bean.DataIndicatorSystem;
import com.cvicse.cic.module.datasource.bean.DataIndicatorSystemTreepath;
import com.cvicse.cic.module.datasource.service.DataIndicatorSystemService;
import com.cvicse.cic.module.datasource.bean.DataIndicatorSystemData;
import com.cvicse.cic.module.datasource.bean.DataIndicatorSystemNode;
import com.cvicse.cic.module.view.bean.ECharts.HistogramDTO;
import com.cvicse.cic.module.datasource.dao.CiFrameworkIndicatorDao;
import com.cvicse.cic.module.datasource.dao.DataIndicatorSystemDao;
import com.cvicse.cic.module.datasource.dao.CiFrameworkTreepathDao;
import com.cvicse.cic.module.datasource.service.DataIndicatorSystemDataService;
import com.cvicse.cic.module.operation.service.IndicatorsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataIndicatorSystemServiceImpl extends ServiceImpl<DataIndicatorSystemDao, DataIndicatorSystem> implements DataIndicatorSystemService {

    @Autowired
    private DataIndicatorSystemDao dataIndicatorSystemDao;

    @Autowired
    private CiFrameworkIndicatorDao ciFrameworkIndicatorDao;

    @Autowired
    private CiFrameworkTreepathDao ciFrameworkTreepathDao;

    @Autowired
    private DataIndicatorSystemDataService dataIndicatorSystemDataService;

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
        List<DataIndicatorSystemNode> excelHeadList = ciFrameworkIndicatorDao.selectExcelHead(ciObjId);
        List<DataIndicatorSystemNode> cellList = ciFrameworkIndicatorDao.selectCiIndicatorByCiObjIdAndDepth(ciObjId, 0);
        List<Map<Integer, String>> excelDataList = new ArrayList<>();
        currentDepth = 0;
        getCell(cellList, 1, excelHeadList.size(), excelDataList);
        return excelDataList;
    }

    @Override
    public Long getRecentlyDataIndicatorSystemId() {
        DataIndicatorSystem dataIndicatorSystem = this.getOne(new QueryWrapper<DataIndicatorSystem>()
                .orderByDesc("id")
                .last("limit 1"));
        if (dataIndicatorSystem != null) {
            return dataIndicatorSystem.getId();
        }
        return null;
    }

    @Override
    @Transactional
    public boolean deleteDataIndicatorSystemById(Long dataIndicatorSystemId) {
        // 这里分开删除用事务控制数据一致性，联表太慢
        this.removeById(dataIndicatorSystemId);
        ciFrameworkIndicatorDao.delete(new QueryWrapper<DataIndicatorSystemNode>()
                .eq("data_indicator_system_id", dataIndicatorSystemId));
        ciFrameworkTreepathDao.delete(new QueryWrapper<DataIndicatorSystemTreepath>()
                .eq("data_indicator_system_id", dataIndicatorSystemId));
        dataIndicatorSystemDataService.remove(new QueryWrapper<DataIndicatorSystemData>()
                .eq("data_indicator_system_id", dataIndicatorSystemId));
        return true;
    }

    @Override
    public Map<String, Object> getDataIndicatorSystemInfo(Long DataIndicatorSystemId) throws Exception {
        Map<String, Object> DataIndicatorSystemInfoMap = new HashMap<>();
        DataIndicatorSystemInfoMap.put("allTargetsCic", createHistogram(DataIndicatorSystemId));
        return DataIndicatorSystemInfoMap;
    }

    @Override
    public void switchFrameObj(Long DataIndicatorSystemId) {
        indicatorsService.switchFrameObj();
    }

    private HistogramDTO createHistogram(Long DataIndicatorSystemId) throws Exception {
        List<String> legendData = Arrays.asList("histogram", "line");
        List<String> xAxisData = new ArrayList<>();
        List<DataIndicatorSystemData> list = dataIndicatorSystemDataService.list(new QueryWrapper<DataIndicatorSystemData>()
                .eq("data_indicator_system_id", DataIndicatorSystemId)
                .select("target_name", "belong_column_index"));
        if (list == null) {
            throw new Exception("数据为空，请导入数据");
        }
        List<DataIndicatorSystemData> collect = list.stream()
                .sorted(Comparator.comparing(DataIndicatorSystemData::getBelongColumnIndex))
                .collect(Collectors.toList());
        for (DataIndicatorSystemData dataIndicatorSystemData : collect) {
            xAxisData.add(dataIndicatorSystemData.getDataHead());
        }

        List<Map<String, Object>> series = new ArrayList<>();

        Map<String, Object> histogramDataMap = new HashMap<>();
        Map<Long, Double> targetsCompositeIndicatorMap = indicatorsService.getAllFrameObjectComInxMap().get(DataIndicatorSystemId);
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

    private List<Map<Integer, String>> getCell(List<DataIndicatorSystemNode> cellList, Integer pathDepth, Integer maxDepth, List<Map<Integer, String>> excelDataList) {
        currentDepth++;
        for (DataIndicatorSystemNode cell : cellList) {
            if (currentDepth.equals(maxDepth)) {
                for (DataIndicatorSystemNode maxDepthCell : cellList) {
                    Map<Integer, String> rowMap = new HashMap<>();
                    rowMap.put(currentDepth, maxDepthCell.getIndicatorName());
                    excelDataList.add(rowMap);
                }
                currentDepth--;
                return excelDataList;
            }
            List<DataIndicatorSystemNode> nextCellList = ciFrameworkIndicatorDao.selectIndicatorByAncestorAndDepth(cell.getId(), pathDepth);
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
