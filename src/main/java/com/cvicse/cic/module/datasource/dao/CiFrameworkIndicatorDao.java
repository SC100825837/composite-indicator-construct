package com.cvicse.cic.module.datasource.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cvicse.cic.module.datasource.bean.DataIndicatorSystemNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CiFrameworkIndicatorDao extends BaseMapper<DataIndicatorSystemNode> {

    /**
     * 查询表头
     * @param ciObjId
     * @return
     */
    List<DataIndicatorSystemNode> selectExcelHead(@Param("ciObjId") Long ciObjId);

    /**
     * 通过综合指标架构对象id 和 层级深度 查询 指标集合
     * @param ciObjId
     * @param depth
     * @return
     */
    List<DataIndicatorSystemNode> selectCiIndicatorByCiObjIdAndDepth(@Param("ciObjId") Long ciObjId, @Param("depth") Integer depth);

    /**
     * 通过祖先id 和 深度 查询指标集合
     * @param ancestor
     * @param pathDepth
     * @return
     */
    List<DataIndicatorSystemNode> selectIndicatorByAncestorAndDepth(@Param("ancestor") Long ancestor, @Param("pathDepth") Integer pathDepth);
}
