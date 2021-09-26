package com.jc.research.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jc.research.entity.CiFrameworkIndicator;
import com.jc.research.entity.CiFrameworkObject;
import com.jc.research.mapper.CiFrameworkIndicatorMapper;
import com.jc.research.mapper.CiFrameworkObjectMapper;
import com.jc.research.service.CiFrameworkObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CiFrameworkObjectServiceImpl extends ServiceImpl<CiFrameworkObjectMapper, CiFrameworkObject> implements CiFrameworkObjectService {

    @Autowired
    private CiFrameworkObjectMapper ciFrameworkObjectMapper;

    @Autowired
    private CiFrameworkIndicatorMapper ciFrameworkIndicatorMapper;


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
    public void getCiFrameworkObjectCalcInfo(Long ciFrameworkObjectId) {

    }

    @Override
    @Transactional
    public int deleteCiFrameworkObjectById(Long ciFrameworkObjectId) {
        return ciFrameworkObjectMapper.deleteCiFrameworkObjectById(ciFrameworkObjectId);
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
