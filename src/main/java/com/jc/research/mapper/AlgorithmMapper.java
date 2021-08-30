/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the pig4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: lengleng (wangiegie@gmail.com)
 */

package com.jc.research.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jc.research.indicatorAl.algorithm.Algorithm;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * neo4j 表
 *
 * @author pigx code generator
 * @date 2021-06-23 10:34:59
 */
@Mapper
public interface AlgorithmMapper extends BaseMapper<Algorithm> {

    List<Algorithm> getAllAlgorithms();
}
