package com.jc.research.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jc.research.entity.*;
import com.jc.research.entity.DTO.*;
import com.jc.research.entity.algorithm.Algorithm;
import com.jc.research.entity.algorithm.result.AlgorithmExecResult;
import com.jc.research.entity.algorithm.result.FAMulValAnalysisPR;
import com.jc.research.entity.algorithm.result.FactorAnalysisPR;
import com.jc.research.indicatorAl.facade.AlgorithmFacade;
import com.jc.research.service.*;
import com.jc.research.util.AlgorithmConstants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.jc.research.util.AlgorithmUtil.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IndicatorsServiceImpl {

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private CiConstructTargetService ciConstructTargetService;

    @Autowired
    private CiFrameworkObjectService ciFrameworkObjectService;

    @Autowired
    private CiFrameworkIndicatorService ciFrameworkIndicatorService;

    @Autowired
    private CiFrameworkTreepathService ciFrameworkTreepathService;

    /**
     * 基础图对象的缓存集合(平铺结构)，只包含基础结构的节点
     * 创建前端规定的节点对象集合，其中key为节点id，value为前端格式节点对象
     */
    private List<GraphNode> graphNodeList = new ArrayList<>();
    private List<GraphEdge> graphEdgeList = new ArrayList<>();

    /**
     * 记录当前最大的节点id，用来设置带数值的节点id
     */
    private Long currentMaxNodeId = 0L;

    /**
     * 节点类型
     */
    private int category = 0;

    /**
     * 指数图对象的缓存集合，既包含基础节点，也包含增加的指数节点
     */
    @Getter
    private List<GraphNode> indicatorGraphNodeList = new ArrayList<>();
    @Getter
    private List<GraphEdge> indicatorGraphEdgeList = new ArrayList<>();

    /**
     * 校验集合，创建节点时判断该节点或连线是否已创建
     */
    private Map<Object, String> checkExitMap = new HashMap<>();

    /**
     * 数据库查询的构建对象缓存
     */
    private List<CiConstructTarget> ciConstructTargetList = new ArrayList<>();

    /**
     * 综合指数构建对象集合缓存
     */
    private Double[][] originDataArray;

    /**
     * 需要进行计算的目标 所在原始数据集的行数
     */
    private int targetObjLine;

    /**
     * 基础指标名称
     */
    private List<String> baseIndicatorName = new ArrayList<>();

    /**
     * 权重map，key为指标名称，value为权重值
     */
    private Map<String, Double> weightMap = new HashMap<>();

    /**
     * 基础指标值map，key为指标名称，value为指标值
     */
    private Map<String, Double> baseIndicatorValueMap = new HashMap<>();

    /**
     * 算法计算结果
     */
    private AlgorithmExecResult execResult;

    /**
     * 构建对象id缓存，如果实时数据和缓存不同则更新数据
     */
    private Long constructObjId = 0L;

    /**
     * 判断数据集是否修改
     */
    private boolean ifDataSetModified = true;

    /**
     * 获取基础的图数据模型
     *
     * @return
     */
    public GraphDTO getBaseGraph(Long ciFrameworkObjectId) {
        //先从缓存中取数据，如果没有数据则重新构建
        if (!graphNodeList.isEmpty() && !graphEdgeList.isEmpty()) {
            return new GraphDTO(graphNodeList, graphEdgeList);
        }
        //每次需要重新构造数据时，初始化所有数据
        resetData();

        // 查询最大id
        CiFrameworkIndicator indicatorWithMaxId = ciFrameworkIndicatorService.getBaseMapper()
                .selectOne(new QueryWrapper<CiFrameworkIndicator>()
                        .eq("ci_framework_object_id", ciFrameworkObjectId)
                        .orderByDesc("id")
                        .last("limit 1"));
        if (indicatorWithMaxId != null) {
            this.currentMaxNodeId = indicatorWithMaxId.getId();
        }


        // 查询最大层级，把层级当做前端显示的类别
        CiFrameworkIndicator indicatorWithMaxLevel = ciFrameworkIndicatorService.getBaseMapper()
                .selectOne(new QueryWrapper<CiFrameworkIndicator>()
                        .eq("ci_framework_object_id", ciFrameworkObjectId)
                        .orderByDesc("indicator_level")
                        .last("limit 1"));

        if (indicatorWithMaxLevel != null) {
            this.category = indicatorWithMaxLevel.getIndicatorLevel();
        }

        CiFrameworkObject ciFrameworkObject = ciFrameworkObjectService.getById(ciFrameworkObjectId);

        // 创建根节点
        GraphNode rootNode = new GraphNode();
        // 设置根节点的id
        rootNode.setId(++this.currentMaxNodeId);
        rootNode.setParentId(-1L);
        //设置根节点的类别
        rootNode.setCategory(++this.category);
        rootNode.getAttributes().put("name", "综合指数");
        this.graphNodeList.add(rootNode);

        // 查询父子级别  也就是相邻的层级结构
        List<CiFrameworkTreepath> treepathList = ciFrameworkTreepathService.getBaseMapper()
                .selectList(new QueryWrapper<CiFrameworkTreepath>()
                        .eq("ci_framework_object_id", ciFrameworkObjectId)
                        .eq("path_depth", 1));

        // 遍历层级结构，根据结构查询节点
        for (CiFrameworkTreepath treepath : treepathList) {
            // 查询后代位置的节点
            CiFrameworkIndicator indicator = ciFrameworkIndicatorService
                    .getOne(new QueryWrapper<CiFrameworkIndicator>()
                            .eq("ci_framework_object_id", ciFrameworkObjectId)
                            .eq("id", treepath.getDescendant())
                            .lt("indicator_level", ciFrameworkObject.getDataFirstColumn()));

            if (indicator == null) {
                continue;
            }
            // 获取该节点的 层级
            Integer indicatorLevel = indicator.getIndicatorLevel();
            // 如果层级为1，说明这个结构代表excel的前两列，也就是第一和第二指标
            if (indicatorLevel.equals(1)) {
                // 获取该结构的祖先级节点
                CiFrameworkIndicator firstLevelIndicator = ciFrameworkIndicatorService
                        .getOne(new QueryWrapper<CiFrameworkIndicator>()
                                .eq("ci_framework_object_id", ciFrameworkObjectId)
                                .eq("id", treepath.getAncestor()));
                if (firstLevelIndicator == null) {
                    continue;
                }
                // 创建excel中各一级指标节点
                createBaseNode(firstLevelIndicator, rootNode.getId());
                // 创建综合指数节点和excel中各一级指标节点的连线
                createBaseEdge(new CiFrameworkTreepath(rootNode.getId(), firstLevelIndicator.getId(), 1, ciFrameworkObjectId));

            }
            createBaseNode(indicator, treepath.getAncestor());
            createBaseEdge(treepath);
        }
        return new GraphDTO(this.graphNodeList, this.graphEdgeList);
    }

    /**
     * 创建节点
     */
    private void createBaseNode(CiFrameworkIndicator indicator, Long parentId) {
        GraphNode graphNode = new GraphNode();
        graphNode.setId(indicator.getId());
        graphNode.setCategory(indicator.getIndicatorLevel());
        graphNode.setParentId(parentId);
        graphNode.getAttributes().put("name", indicator.getIndicatorName());
        if (check(indicator.getId())) {
            this.graphNodeList.add(graphNode);
        }
    }

    /**
     * 创建连线
     */
    private void createBaseEdge(CiFrameworkTreepath treepath) {
        Long targetId = treepath.getAncestor();
        Long sourceId = treepath.getDescendant();
        GraphEdge graphEdge = new GraphEdge();
        graphEdge.setSourceId(sourceId);
        graphEdge.setTargetId(targetId);
        if (check(sourceId + "_" + targetId)) {
            this.graphEdgeList.add(graphEdge);
        }
    }

    /**
     * 检查该节点或者连线是否创建
     *
     * @param key
     * @return
     */
    private boolean check(Object key) {
        if (!checkExitMap.containsKey(key)) {
            checkExitMap.put(key, "");
            return true;
        } else {
            return false;
        }
    }

    public CalcResultGraphDTO calcHandler(CalcExecParamDTO calcExecParam) throws Exception {
        if (graphNodeList.isEmpty() || graphEdgeList.isEmpty()) {
            throw new Exception("数据异常，请尝试刷新页面");
        }
        //初始化数据，然后执行处理算法
        return handleDataAndAlgorithm(initAlgorithmAndConstructObj(calcExecParam), calcExecParam.getTargetId(), calcExecParam.getCiFrameworkObjectId());
    }

    /**
     * 初始化算法数、和构造对象数据、数据集
     *
     * @param calcExecParam
     * @return
     */
    private Map<String, String> initAlgorithmAndConstructObj(CalcExecParamDTO calcExecParam) throws Exception {
        //获取所有算法的id
        Map<String, Long> algorithmIdMap = calcExecParam.getAlgorithms().getAllAlgorithmIds();
        // 根据算法id查询算法对象
        List<Algorithm> algorithms = algorithmService.listByIds(algorithmIdMap.values());

        //key是算法步骤名称，value是算法的全类名
        Map<String, String> algorithmMap = new HashMap<>();
        for (Algorithm algorithm : algorithms) {
            algorithmMap.put(algorithm.getStepName(), algorithm.getFullClassName() == null ? "" : algorithm.getFullClassName());
        }
        // 原始数据集没有修改过就用原始数据进行计算
        if (calcExecParam.getModifiedDataList() == null || calcExecParam.getModifiedDataList().length == 0) {
            //缓存中没有数据集的数据时从数据库取出并放入缓存
            if (this.originDataArray == null || this.originDataArray.length == 0) {
                List<CiConstructTarget> ciConstructTargetList = ciConstructTargetService.list(
                        new QueryWrapper<CiConstructTarget>()
                                .eq("ci_framework_object_id", calcExecParam.getCiFrameworkObjectId()));

                this.originDataArray = new Double[ciConstructTargetList.size()][];
                // 为了保证顺序和导入的excel一致，进行排序
                this.ciConstructTargetList = ciConstructTargetList.stream()
                        .sorted(Comparator.comparingInt(CiConstructTarget::getBelongColumnIndex))
                        .collect(Collectors.toList());
                // 创建原始数据集二维数组
                for (int i = 0; i < this.ciConstructTargetList.size(); i++) {
                    if (this.ciConstructTargetList.get(i).getId().equals(calcExecParam.getTargetId())) {
                        this.targetObjLine = i;
                    }
                    this.originDataArray[i] = new ObjectMapper().readValue(this.ciConstructTargetList.get(i).getData(), Double[].class);
                }
            } else {
                for (int i = 0; i < this.ciConstructTargetList.size(); i++) {
                    if (this.ciConstructTargetList.get(i).getId().equals(calcExecParam.getTargetId())) {
                        this.targetObjLine = i;
                    }
                }
            }
        } else {
            //拿到修改后的数据集
            this.originDataArray = calcExecParam.getModifiedDataList();
            this.ifDataSetModified = true;
        }
        return algorithmMap;
    }

    /**
     * 进行综合指数计算
     *
     * @param algorithmMap 每一步的算法对象
     * @return
     * @throws Exception
     */
    public CalcResultGraphDTO handleDataAndAlgorithm(Map<String, String> algorithmMap, Long targetId, Long ciFrameworkObjectId) throws Exception {
        //判断数据集是否修改,没修改直接用缓存数据，修改了就重新计算
        if (ifDataSetModified) {
            //通过算法门面执行算法计算
            this.execResult = AlgorithmFacade.calculate(algorithmMap, this.originDataArray);
        }
        weightMap = new HashMap<>();

        //缺失值插补的结果
        Double[][] missDataImputationArr = execResult.getMissDataImputation();
        // 找到缺失值插补计算之后的构建对象数据
        Double[] targetLineData = missDataImputationArr[this.targetObjLine];

        //得到权重计算的最终结果，即权重值数组
        Double[] baseIndicatorWeight = execResult.getWeightingAndAggregation().getFinalResult()[0];

        //初始化综合指标
        double compositeIndicator = 0;
        //计算综合指标数值
        for (Double indicatorData : targetLineData) {
            for (Double weight : baseIndicatorWeight) {
                compositeIndicator += indicatorData * weight;
            }
        }
        //处理小数点位数
        compositeIndicator = handleFractional(2, compositeIndicator);

        // 基础指标节点所在的列
        int baseIndicatorNodeColumn = 0;
        // TODO 构建对象的id
        CiFrameworkObject ciFrameworkObject = ciFrameworkObjectService.getById(ciFrameworkObjectId);
        if (ciFrameworkObject != null) {
            // 基础指标节点所在的列 和 数据列 相邻
            baseIndicatorNodeColumn = ciFrameworkObject.getDataFirstColumn() - 1;
        }
        // 虽然理论上插入数据库是按照excel顺序来插入的，但是稳妥起见还是排个序
        List<CiFrameworkIndicator> baseIndicatorList = ciFrameworkIndicatorService.list(new QueryWrapper<CiFrameworkIndicator>()
                .eq("ci_framework_object_id", ciFrameworkObjectId)
                .eq("indicator_level", baseIndicatorNodeColumn).eq("head_flag", 0))
                .stream()
                .sorted(Comparator.comparingLong(CiFrameworkIndicator::getId))
                .collect(Collectors.toList());

        // key是节点id， value是节点的基础数据值（经过缺失值插补计算的）
        Map<Long, Double> targetDataMap = new HashMap<>(baseIndicatorList.size());
        Map<Long, Double> weightMap = new HashMap<>(baseIndicatorList.size());
        for (CiFrameworkIndicator baseIndicator : baseIndicatorList) {
            for (Double cellData : targetLineData) {
                targetDataMap.put(baseIndicator.getId(), cellData);
                this.baseIndicatorName.add(baseIndicator.getIndicatorName());
            }
            for (Double weight : baseIndicatorWeight) {
                weightMap.put(baseIndicator.getId(), weight);
            }
        }
        //构建带有指标值的图数据
        constructIndicatorGraph(compositeIndicator, targetId, baseIndicatorNodeColumn, targetDataMap, weightMap);

        CalcResultGraphDTO calcResultGraphDTO = new CalcResultGraphDTO();
        calcResultGraphDTO.setAlgorithmExecResult(execResult);
        calcResultGraphDTO.setCompositeIndicator(compositeIndicator);
        calcResultGraphDTO.getCompIndGraphNode().addAll(indicatorGraphNodeList);
        calcResultGraphDTO.getCompIndGraphEdge().addAll(indicatorGraphEdgeList);

        return calcResultGraphDTO;

    }

    /**
     * 构建带有指标值的图数据
     *
     * @param compositeIndicator
     */
    private void constructIndicatorGraph(Double compositeIndicator, Long targetId, int baseIndicatorNodeColumn, Map<Long, Double> targetDataMap, Map<Long, Double> weightMap) {
        //如果对象一样，就判断数据集是否修改,没修改直接用缓存数据
        if (constructObjId.equals(targetId) && !ifDataSetModified) {
            return;
        }
        if (!indicatorGraphNodeList.isEmpty() && !indicatorGraphEdgeList.isEmpty()) {
            indicatorGraphNodeList = new ArrayList<>();
            indicatorGraphEdgeList = new ArrayList<>();
        }
        indicatorGraphNodeList.addAll(graphNodeList);
        indicatorGraphEdgeList.addAll(graphEdgeList);
        constructObjId = targetId;

        for (GraphNode graphNode : graphNodeList) {
            //找到基础指标节点, 它的类别值是基础指标所在的excel列下标
            if (graphNode.getCategory() == baseIndicatorNodeColumn) {
                //创建指标节点，并设置属性
                GraphNode baseIndicatorDataNode = new GraphNode();
                baseIndicatorDataNode.setId(++this.currentMaxNodeId);
                baseIndicatorDataNode.getAttributes().put("indicatorValue", targetDataMap.get(graphNode.getId()));
                baseIndicatorDataNode.setCategory(this.category + 1);
                baseIndicatorDataNode.setLbName("基础指标值");
                baseIndicatorDataNode.setParentId(graphNode.getId());

                //创建权重节点，并设置属性
                GraphNode weightNode = new GraphNode();
                weightNode.setId(++this.currentMaxNodeId);
                weightNode.getAttributes().put("indicatorValue", weightMap.get(graphNode.getId()));
                weightNode.setCategory(this.category + 2);
                weightNode.setLbName("权重值");
                weightNode.setParentId(graphNode.getId());

                //创建连线，并设置属性，基础指标节点由指标值指向 通用指标名称
                GraphEdge indicatorGraphEdge = new GraphEdge();
                indicatorGraphEdge.setSourceId(baseIndicatorDataNode.getId());
                indicatorGraphEdge.setTargetId(graphNode.getId());
                //创建连线，并设置属性，权重节点 指向 通用指标名称
                GraphEdge weightGraphEdge = new GraphEdge();
                weightGraphEdge.setSourceId(weightNode.getId());
                weightGraphEdge.setTargetId(graphNode.getId());

                indicatorGraphNodeList.add(baseIndicatorDataNode);
                indicatorGraphNodeList.add(weightNode);
                //添加到新创建的连线放置到连线集合缓存中
                indicatorGraphEdgeList.add(indicatorGraphEdge);
                indicatorGraphEdgeList.add(weightGraphEdge);
            }
        }
        //创建综合指标值节点，并设置属性
        GraphNode compIndGraphNode = new GraphNode();
        compIndGraphNode.setId(++this.currentMaxNodeId);
        compIndGraphNode.getAttributes().put("indicatorValue", compositeIndicator);
        compIndGraphNode.setLbName("综合指标值");
        compIndGraphNode.setCategory(this.category + 3);
        compIndGraphNode.setParentId(-1L);
        //创建连线，并设置属性，综合指标值节点由 指标值 指向 通用指标名称
        GraphEdge graphEdge = new GraphEdge();
        graphEdge.setSourceId(compIndGraphNode.getId());
        graphEdge.setTargetId(graphNodeList.get(0).getId());

        //将综合指标值节点放入缓存
        indicatorGraphNodeList.add(compIndGraphNode);
        //将新创建的综合指标值和通用综合指标名称的连线关系放入缓存
        indicatorGraphEdgeList.add(graphEdge);

        this.ifDataSetModified = false;
    }

    /**
     * 获取原始数据集
     *
     * @return
     */
    public Double[][] getOriginDataArray(Long targetId, Long ciFrameworkObjectId) {
        //缓存中没有数据集的数据时从数据库取出并放入缓存
        if (this.originDataArray == null || this.originDataArray.length == 0) {
            // TODO 架构对象id
            List<CiConstructTarget> ciConstructTargetList = ciConstructTargetService.list(
                    new QueryWrapper<CiConstructTarget>()
                            .eq("ci_framework_object_id", ciFrameworkObjectId));

            this.originDataArray = new Double[ciConstructTargetList.size()][];
            // 为了保证顺序和导入的excel一致，进行排序
            ciConstructTargetList = ciConstructTargetList.stream()
                    .sorted(Comparator.comparingInt(CiConstructTarget::getBelongColumnIndex))
                    .collect(Collectors.toList());
            // 创建原始数据集二维数组
            for (int i = 0; i < ciConstructTargetList.size(); i++) {
                try {
                    this.originDataArray[i] = new ObjectMapper().readValue(ciConstructTargetList.get(i).getData(), Double[].class);
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return this.originDataArray;
    }

    /**
     * 拿到计算过程数据,封装对象并返回
     */
    public ProcessResultDTO getProcessData(Long ciFrameworkObjectId) {

        if (this.execResult == null) {
            return null;
        }
        return createWebDTO(ciFrameworkObjectId);
    }

    /**
     * 拿到计算过程数据,封装对象并返回
     *
     */
    private ProcessResultDTO createWebDTO(Long ciFrameworkObjectId) {
        //创建过程结果前端封装对象
        ProcessResultDTO processResultDTO = new ProcessResultDTO();

        //获取原始数据集
        List<CiConstructTarget> targetNameList = ciConstructTargetService.list(new QueryWrapper<CiConstructTarget>()
                .select("target_name")
                .eq("ci_framework_object_id", ciFrameworkObjectId));
        List<String> originDataList = new ArrayList<>();
        for (int i = 0; i <= this.originDataArray.length; i++) {
            if (i == 0) {
                originDataList.add("名称");
                originDataList.add(this.baseIndicatorName.get(i));
                continue;
            }
            originDataList.add(targetNameList.get(i - 1).getTargetName());
            for (int j = 0; j < this.originDataArray[i - 1].length; j++) {
                originDataList.add(String.valueOf(this.originDataArray[i - 1][j]));
            }
        }
        processResultDTO.getOriginalData().put(AlgorithmConstants.FIRST_LEVEL_TITLE, AlgorithmConstants.ORIGIN_DATA_SET_NAME_ZH);
        processResultDTO.getOriginalData().put("isContainPR", false);
        processResultDTO.getOriginalData().put("data", originDataList);

        //缺失值插补
        Double[][] missDataImputationArr = this.execResult.getMissDataImputation();
        List<String> missDataImputationList = new ArrayList<>();
        //创建新的集合，用来存储缺失值插补算法返回的数据
        for (int i = 0; i <= missDataImputationArr.length; i++) {
            if (i == 0) {
                missDataImputationList.add("名称");
                missDataImputationList.add(this.baseIndicatorName.get(i));
                continue;
            }
            missDataImputationList.add(targetNameList.get(i - 1).getTargetName());
            for (int j = 0; j < missDataImputationArr[i - 1].length; j++) {
                missDataImputationList.add(String.valueOf(missDataImputationArr[i - 1][j]));
            }
        }
        processResultDTO.getMissDataImputation().put(AlgorithmConstants.FIRST_LEVEL_TITLE, AlgorithmConstants.MISS_DATA_IMPUTATION_NAME_ZH);
        processResultDTO.getMissDataImputation().put("isContainPR", false);
        processResultDTO.getMissDataImputation().put("data", missDataImputationList);

        //多变量分析
        //拿到多变量分析计算结果
        FAMulValAnalysisPR multivariateAnalysisPR = (FAMulValAnalysisPR) this.execResult.getMultivariateAnalysis();
        Map<String, Object> multivariateAnalysisResultMap = new HashMap<>();
        Double[][] correlationMatrix = multivariateAnalysisPR.getCorrelationMatrix();
        //创建矩阵图数据对象
        CoordinateDTO correlationMatrixCoordinate = new CoordinateDTO();
        List<String> axisData = targetNameList.stream()
                .map(CiConstructTarget::getTargetName)
                .collect(Collectors.toList());
        //设置x轴
        correlationMatrixCoordinate.setXAxis(axisData);
        //设置y轴
        correlationMatrixCoordinate.setYAxis(axisData);
        //设置数据
        List<List<Double>> correlationMatrixData = new ArrayList<>();
        for (int i = 0; i < correlationMatrix.length; i++) {
            for (int j = 0; j < correlationMatrix[i].length; j++) {
                List<Double> unitData = new ArrayList<>();
                unitData.add((double) j);
                unitData.add((double) i);
                unitData.add(handleFractional(2, correlationMatrix[i][j]));
                correlationMatrixData.add(unitData);
            }
        }
        correlationMatrixCoordinate.setData(correlationMatrixData);
        correlationMatrixCoordinate.setMaxValue(1);
        correlationMatrixCoordinate.setMinValue(-1);
        correlationMatrixCoordinate.setTitle("相关性矩阵");
        multivariateAnalysisResultMap.put("correlationMatrix", correlationMatrixCoordinate);
        processResultDTO.getMultivariateAnalysis().put(AlgorithmConstants.FIRST_LEVEL_TITLE, AlgorithmConstants.MULTI_VARIATE_ANALYSIS_NAME_ZH);
        processResultDTO.getMultivariateAnalysis().put("isContainPR", true);
        processResultDTO.getMultivariateAnalysis().put("data", multivariateAnalysisResultMap);

        //标准化
        Double[][] normalisationArr = this.execResult.getNormalisation();
        //创建新的集合，用来存储标准化算法返回的数据
        List<String> normalisationList = new ArrayList<>();
        //创建新的集合，用来存储缺失值插补算法返回的数据
        for (int i = 0; i <= normalisationArr.length; i++) {
            if (i == 0) {
                missDataImputationList.add("名称");
                missDataImputationList.add(this.baseIndicatorName.get(i));
                continue;
            }
            missDataImputationList.add(targetNameList.get(i - 1).getTargetName());
            for (int j = 0; j < normalisationArr[i - 1].length; j++) {
                missDataImputationList.add(String.valueOf(normalisationArr[i - 1][j]));
            }
        }
        processResultDTO.getNormalisation().put(AlgorithmConstants.FIRST_LEVEL_TITLE, AlgorithmConstants.NORMALISATION_NAME_ZH);
        processResultDTO.getNormalisation().put("isContainPR", false);
        processResultDTO.getNormalisation().put("data", normalisationList);

        //权重和聚合
        //从计算结果中取权重和聚合算法的结果
        FactorAnalysisPR weightingAndAggregation = (FactorAnalysisPR) this.execResult.getWeightingAndAggregation();
        Map<String, Object> weightingAndAggregationResultMap = new HashMap<>();
        //取得权重和聚合算法中的负载因子加载矩阵
        Double[][] rotatedFactorLoadingsMatrix = weightingAndAggregation.getRotatedFactorLoadingsMatrix();
        //创建矩阵图数据对象
        CoordinateDTO rotatedFactorLoadingsMatrixCoordinate = new CoordinateDTO();
        //设置x轴
        rotatedFactorLoadingsMatrixCoordinate.setXAxis(Arrays.asList("因子1", "因子2", "因子3", "因子4", "因子5", "因子6", "因子7", "因子8", "因子9", "因子10"));
        //设置y轴
        rotatedFactorLoadingsMatrixCoordinate.setYAxis(axisData);
        //设置数据
        List<List<Double>> rotatedFactorLoadingsMatrixData = new ArrayList<>();
        for (int i = 0; i < rotatedFactorLoadingsMatrix.length; i++) {
            for (int j = 0; j < rotatedFactorLoadingsMatrix[i].length; j++) {
                List<Double> unitData = new ArrayList<>();
                unitData.add((double) j);
                unitData.add((double) i);
                unitData.add(handleFractional(2, rotatedFactorLoadingsMatrix[i][j]));
                rotatedFactorLoadingsMatrixData.add(unitData);
            }
        }
        rotatedFactorLoadingsMatrixCoordinate.setData(rotatedFactorLoadingsMatrixData);
        //设置颜色上下限的值
        rotatedFactorLoadingsMatrixCoordinate.setMinValue(-1);
        rotatedFactorLoadingsMatrixCoordinate.setMaxValue(1);
        rotatedFactorLoadingsMatrixCoordinate.setTitle("旋转因子载荷矩阵");
        weightingAndAggregationResultMap.put("rotatedFactorLoadingsMatrix", rotatedFactorLoadingsMatrixCoordinate);

        //取得权重和聚合算法中的特征值、方差百分比、累计方差
        Double[][] eigenvalueArr = weightingAndAggregation.getEigenvalues();
        //创建矩阵图数据对象
        CoordinateDTO eigenvalueCoordinate = new CoordinateDTO();
        //设置x轴
        eigenvalueCoordinate.setXAxis(Arrays.asList("特征值", "方差(%)", "累积方差(%)"));
        //设置y轴
        eigenvalueCoordinate.setYAxis(axisData);
        //设置数据
        List<List<Double>> eigenvalueData = new ArrayList<>();
        for (int i = 0; i < eigenvalueArr.length; i++) {
            for (int j = 0; j < eigenvalueArr[i].length; j++) {
                List<Double> unitData = new ArrayList<>();
                unitData.add((double) j);
                unitData.add((double) i);
                unitData.add(handleFractional(2, eigenvalueArr[i][j]));
                eigenvalueData.add(unitData);
            }
        }
        eigenvalueCoordinate.setData(eigenvalueData);
        //设置颜色上下限的值
        eigenvalueCoordinate.setMinValue(0);
        eigenvalueCoordinate.setMaxValue(100);
        eigenvalueCoordinate.setTitle("特征值");
        weightingAndAggregationResultMap.put("eigenvalues", eigenvalueCoordinate);

        //取得权重和聚合算法中的指标权重
        Double[] indicatorWeight = weightingAndAggregation.getIndicatorWeight()[0];
        //创建矩阵图数据对象
        CoordinateDTO indicatorWeightCoordinate = new CoordinateDTO();
        //设置x轴
        indicatorWeightCoordinate.setXAxis(Collections.singletonList("权重"));
        //设置y轴
        indicatorWeightCoordinate.setYAxis(axisData);
        //设置数据
        List<List<Double>> indicatorWeightData = new ArrayList<>();
        for (int i = 0; i < indicatorWeight.length; i++) {
            List<Double> unitData = new ArrayList<>();
            unitData.add((double) i);
            unitData.add((double) 0);
            unitData.add(handleFractional(2, indicatorWeight[i]));
            indicatorWeightData.add(unitData);
        }
        indicatorWeightCoordinate.setData(indicatorWeightData);
        //设置颜色上下限的值
        indicatorWeightCoordinate.setMinValue(0);
        indicatorWeightCoordinate.setMaxValue(1);
        indicatorWeightCoordinate.setTitle("权重");
        weightingAndAggregationResultMap.put("indicatorWeight", indicatorWeightCoordinate);
        processResultDTO.getWeightingAndAggregation().put(AlgorithmConstants.FIRST_LEVEL_TITLE, AlgorithmConstants.WEIGHTING_AND_AGGREGATION_NAME_ZH);
        processResultDTO.getWeightingAndAggregation().put("isContainPR", true);
        processResultDTO.getWeightingAndAggregation().put("data", weightingAndAggregationResultMap);

        return processResultDTO;
    }

    /**
     * 计算基础指标值修改之后的综合指标值
     *
     * @param mdBaseIndicatorMap
     * @return
     */
    public Double calcModifyBaseIndicator(Map<String, Double> mdBaseIndicatorMap) {
        //TODO 现在做的是没有标准化直接进行计算的结果，需要根据选择的标准化算法进行计算
        Double mdCompositeIndicator = (double) 0;
        for (String modifyName : mdBaseIndicatorMap.keySet()) {
            for (String baseIndicatorName : baseIndicatorValueMap.keySet()) {
                if (modifyName.equals(baseIndicatorName)) {
                    continue;
                }
                mdCompositeIndicator += baseIndicatorValueMap.get(baseIndicatorName) * weightMap.get(modifyName);
            }
            mdCompositeIndicator += mdBaseIndicatorMap.get(modifyName) * weightMap.get(modifyName);
        }
        return handleFractional(2, mdCompositeIndicator);
    }

    /**
     * 清楚所有缓存，重置数据
     *
     * @return
     */
    public boolean resetData() {
        this.graphNodeList.clear();
        this.graphEdgeList.clear();

        this.indicatorGraphNodeList.clear();
        this.indicatorGraphEdgeList.clear();

        this.currentMaxNodeId = 0L;

        this.category = 0;

        this.checkExitMap.clear();

        this.originDataArray = null;

        this.targetObjLine = 0;

        this.baseIndicatorName = new ArrayList<>();

        this.weightMap.clear();
        this.baseIndicatorValueMap.clear();

        this.execResult = null;

        this.constructObjId = 0L;

        this.ifDataSetModified = true;
        return true;
    }

}
