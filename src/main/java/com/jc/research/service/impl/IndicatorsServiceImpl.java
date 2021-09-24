package com.jc.research.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jc.research.entity.*;
import com.jc.research.entity.DTO.*;
import com.jc.research.entity.algorithm.Algorithm;
import com.jc.research.entity.algorithm.result.AlgorithmExecResult;
import com.jc.research.entity.algorithm.result.FAMulValAnalysisPR;
import com.jc.research.entity.algorithm.result.FactorAnalysisPR;
import com.jc.research.indicatorAl.facade.AlgorithmFacade;
import com.jc.research.mapper.IndicatorsRepository;
import com.jc.research.service.*;
import com.jc.research.util.AlgorithmConstants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static com.jc.research.util.AlgorithmUtil.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @program: neo4j
 * @description:
 * @author: SunChao
 * @create: 2021-06-23 15:42
 **/
@Slf4j
@Service
public class IndicatorsServiceImpl {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private IndicatorsRepository indicatorsRepository;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private TAIService taiService;

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
    @Getter
    private List<GraphNode> graphNodeList = new ArrayList<>();
    @Getter
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
     * 科技成就指数数据缓存
     */
    private List<TechnologyAchievementIndex> taiDataList = new ArrayList<>();

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
     * @return
     */
    public GraphDTO getBaseGraph() {
        Long ciFrameworkObjectId = 36L;
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
        //初始化数据，从数据库查询算法并实例化，从数据库查询指标构建对象
        Object[] dataAndAlgorithms = initAlgorithmAndConstructObj(calcExecParam);

        return handleDataAndAlgorithm((Map<String, String>) dataAndAlgorithms[0], (Double[][]) dataAndAlgorithms[2], calcExecParam.getTargetId(), (Integer) dataAndAlgorithms[3]);
    }

    /**
     * 进行综合指数计算
     *
     * @param algorithmMap          每一步的算法对象
     * @param dataArrays            原始数据的二维数组
     * @param targetId              构建对象的id
     * @return
     * @throws Exception
     */
    public CalcResultGraphDTO handleDataAndAlgorithm(Map<String, String> algorithmMap, Double[][] dataArrays, Long targetId, int targetObjLine) throws Exception {
        //判断数据集是否修改,没修改直接用缓存数据，修改了就重新计算
        if (ifDataSetModified) {
            //通过算法门面执行算法计算
            this.execResult = AlgorithmFacade.calculate(algorithmMap, dataArrays);
        }
        weightMap = new HashMap<>();

        //缺失值插补的结果
        Double[][] missDataImputationArr = execResult.getMissDataImputation();
        Double[] targetLineData = missDataImputationArr[targetObjLine];
        TechnologyAchievementIndex tai = new TechnologyAchievementIndex();
        //定义基础指标值集合，key是基础指标名称，value是基础指标值，
        Map<String, Double> baseIndicatorValueMap = new HashMap<>();

        //得到权重计算的最终结果，即权重值数组
        Double[] baseIndicatorWeight = execResult.getWeightingAndAggregation().getFinalResult()[0];
        Field[] fields = tai.getClass().getDeclaredFields();
        int weightArrIndex = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().equals("id") || field.getName().equals("countryName")) {
                continue;
            }
            weightMap.put(field.getName(), baseIndicatorWeight[weightArrIndex]);
            baseIndicatorValueMap.put(field.getName(), targetLineData[weightArrIndex]);
            weightArrIndex++;
        }

        //初始化综合指标
        double compositeIndicator = 0;
        //计算综合指标数值
        for (Object baseIndicatorName : baseIndicatorValueMap.keySet()) {
            compositeIndicator += baseIndicatorValueMap.get(baseIndicatorName.toString()) * weightMap.get(baseIndicatorName.toString());
        }
        //处理小数点位数
        compositeIndicator = handleFractional(2, compositeIndicator);

        //构建带有指标值的图数据
        constructIndicatorGraph(baseIndicatorValueMap, compositeIndicator, targetId, weightMap);

        CalcResultGraphDTO calcResultGraphDTO = new CalcResultGraphDTO();
        calcResultGraphDTO.setAlgorithmExecResult(execResult);
        calcResultGraphDTO.setCompositeIndicator(compositeIndicator);
        calcResultGraphDTO.getCompIndGraphNode().addAll(indicatorGraphNodeList);
        calcResultGraphDTO.getCompIndGraphEdge().addAll(indicatorGraphEdgeList);

        return calcResultGraphDTO;

    }

    /**
     * 获取原始数据集
     *
     * @return
     */
    public List<TechnologyAchievementIndex> getOriginDataList() {
        if (this.taiDataList == null || this.taiDataList.isEmpty()) {
            this.taiDataList = taiService.list();
        }
        return this.taiDataList;
    }

    /**
     * 拿到计算过程数据,封装对象并返回
     */
    public ProcessResultDTO getProcessData() {

        if (this.execResult == null) {
            return null;
        }
        return createWebDTO(this.execResult);
    }

    /**
     * 拿到计算过程数据,封装对象并返回
     *
     * @param execResult
     */
    private ProcessResultDTO createWebDTO(AlgorithmExecResult execResult) {
        //创建过程结果前端封装对象
        ProcessResultDTO processResultDTO = new ProcessResultDTO();

        //获取原始数据集
        processResultDTO.getOriginalData().put(AlgorithmConstants.FIRST_LEVEL_TITLE, AlgorithmConstants.ORIGIN_DATA_SET_NAME_ZH);
        processResultDTO.getOriginalData().put("isContainPR", false);
        processResultDTO.getOriginalData().put("data", taiDataList);

        //缺失值插补
        Double[][] missDataImputationArr = execResult.getMissDataImputation();
        //创建新的集合，用来存储缺失值插补算法返回的数据
        List<TechnologyAchievementIndex> missDataImputationList = new ArrayList<>();
        try {
            for (int i = 0; i < taiDataList.size(); i++) {
                //创建集合中的行对象
                TechnologyAchievementIndex taiObj = new TechnologyAchievementIndex();
                //设置id和国家名称
                taiObj.setId(taiDataList.get(i).getId());
                taiObj.setCountryName(taiDataList.get(i).getCountryName());
                //反射拿到对象的属性值，顺序按照类里面声明属性的顺序，所以对象中的属性顺序不能动
                Field[] fields = taiObj.getClass().getDeclaredFields();
                for (int j = 0; j < fields.length; j++) {
                    fields[j].setAccessible(true);
                    //缺失值插补返回的数据没有这两项，直接跳过
                    if (fields[j].getName().equals("id") || fields[j].getName().equals("countryName")) {
                        continue;
                    }
                    //向创建的对象中设置属性，i-2是因为前面两次循环跳过了两次，此时i为2，取不到缺失值插补结果中的前两项数据，并且最后数组下标会越界
                    fields[j].set(taiObj, missDataImputationArr[i][j - 2]);
                }
                missDataImputationList.add(taiObj);
            }

        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e.getCause());
        }
        processResultDTO.getMissDataImputation().put(AlgorithmConstants.FIRST_LEVEL_TITLE, AlgorithmConstants.MISS_DATA_IMPUTATION_NAME_ZH);
        processResultDTO.getMissDataImputation().put("isContainPR", false);
        processResultDTO.getMissDataImputation().put("data", missDataImputationList);

        //多变量分析
        //拿到多变量分析计算结果
        FAMulValAnalysisPR multivariateAnalysisPR = (FAMulValAnalysisPR) execResult.getMultivariateAnalysis();
        Map<String, Object> multivariateAnalysisResultMap = new HashMap<>();
        Double[][] correlationMatrix = multivariateAnalysisPR.getCorrelationMatrix();
        //创建矩阵图数据对象
        CoordinateDTO correlationMatrixCoordinate = new CoordinateDTO();
        String[] axisData = {"patents", "royalties", "internet", "exports", "telephones", "electricity", "schooling", "university"};
        //设置x轴
        correlationMatrixCoordinate.setXAxis(Arrays.asList(axisData));
        //设置y轴
        correlationMatrixCoordinate.setYAxis(Arrays.asList(axisData));
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
        Double[][] normalisationArr = execResult.getNormalisation();
        //创建新的集合，用来存储标准化算法返回的数据
        List<TechnologyAchievementIndex> normalisationList = new ArrayList<>();
        try {
            for (int i = 0; i < taiDataList.size(); i++) {
                //创建集合中的行对象
                TechnologyAchievementIndex taiObj = new TechnologyAchievementIndex();
                //设置id和国家名称
                taiObj.setId(taiDataList.get(i).getId());
                taiObj.setCountryName(taiDataList.get(i).getCountryName());
                //反射拿到对象的属性值，顺序按照类里面声明属性的顺序，所以对象中的属性顺序不能动
                Field[] fields = taiObj.getClass().getDeclaredFields();
                for (int j = 0; j < fields.length; j++) {
                    fields[j].setAccessible(true);
                    //缺失值插补返回的数据没有这两项，直接跳过
                    if (fields[j].getName().equals("id") || fields[j].getName().equals("countryName")) {
                        continue;
                    }
                    //向创建的对象中设置属性，i-2是因为前面两次循环跳过了两次，此时i为2，取不到缺失值插补结果中的前两项数据，并且最后数组下标会越界
                    fields[j].set(taiObj, handleFractional(2, normalisationArr[i][j - 2]));
                }
                normalisationList.add(taiObj);
            }

        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e.getCause());
        }
        processResultDTO.getNormalisation().put(AlgorithmConstants.FIRST_LEVEL_TITLE, AlgorithmConstants.NORMALISATION_NAME_ZH);
        processResultDTO.getNormalisation().put("isContainPR", false);
        processResultDTO.getNormalisation().put("data", normalisationList);

        //权重和聚合
        //从计算结果中取权重和聚合算法的结果
        FactorAnalysisPR weightingAndAggregation = (FactorAnalysisPR) execResult.getWeightingAndAggregation();
        Map<String, Object> weightingAndAggregationResultMap = new HashMap<>();
        //取得权重和聚合算法中的负载因子加载矩阵
        Double[][] rotatedFactorLoadingsMatrix = weightingAndAggregation.getRotatedFactorLoadingsMatrix();
        //创建矩阵图数据对象
        CoordinateDTO rotatedFactorLoadingsMatrixCoordinate = new CoordinateDTO();
        //设置x轴
        rotatedFactorLoadingsMatrixCoordinate.setXAxis(Arrays.asList("因子1", "因子2", "因子3", "因子4"));
        //设置y轴
        rotatedFactorLoadingsMatrixCoordinate.setYAxis(Arrays.asList(axisData));
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
        eigenvalueCoordinate.setYAxis(Arrays.asList(axisData));
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
        indicatorWeightCoordinate.setYAxis(Arrays.asList(axisData));
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
     * 构建带有指标值的图数据
     *
     * @param baseIndicatorDataMap
     * @param compositeIndicator
     */
    private void constructIndicatorGraph(Map baseIndicatorDataMap, Double compositeIndicator, Long id, Map<String, Double> weightMap) {
        //如果对象一样，就判断数据集是否修改,没修改直接用缓存数据
        if (constructObjId.equals(id) && !ifDataSetModified) {
            return;
        }
        if (!indicatorGraphNodeList.isEmpty() && !indicatorGraphEdgeList.isEmpty()) {
            indicatorGraphNodeList = new ArrayList<>();
            indicatorGraphEdgeList = new ArrayList<>();
        }
        indicatorGraphNodeList.addAll(graphNodeList);
        indicatorGraphEdgeList.addAll(graphEdgeList);
        constructObjId = id;
        for (GraphNode graphNode : graphNodeList) {
            //找到类别为2的层级节点，也就是子叶节点
            if (graphNode.getCategory() == 2) {
                //创建指标节点，并设置属性
                GraphNode baseIndicatorDataNode = new GraphNode();
                baseIndicatorDataNode.setId(++this.currentMaxNodeId);
                baseIndicatorDataNode.getAttributes().put("indicatorValue", baseIndicatorDataMap.get(graphNode.getAttributes().get("name").toString()));
                baseIndicatorDataNode.setCategory(3);
                baseIndicatorDataNode.setLbName("基础指标值");
                baseIndicatorDataNode.setParentId(graphNode.getId());

                //创建权重节点，并设置属性
                GraphNode weightNode = new GraphNode();
                weightNode.setId(++this.currentMaxNodeId);
                weightNode.getAttributes().put("indicatorValue", handleFractional(2, weightMap.get(graphNode.getAttributes().get("name").toString())));
                weightNode.setCategory(4);
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
        compIndGraphNode.setCategory(5);
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
     * 初始化算法数、和构造对象数据、数据集
     *
     * @param calcExecParam
     * @return
     */
    private Object[] initAlgorithmAndConstructObj(CalcExecParamDTO calcExecParam) throws Exception {
        //获取所有算法的id
        Map<String, Long> algorithmIdMap = calcExecParam.getAlgorithms().getAllAlgorithmIds();
        // 根据算法id查询算法对象
        List<Algorithm> algorithms = algorithmService.listByIds(algorithmIdMap.values());

        //key是算法步骤名称，value是算法的全类名
        Map<String, String> algorithmMap = new HashMap<>();
        for (Algorithm algorithm : algorithms) {
            algorithmMap.put(algorithm.getStepName(), algorithm.getFullClassName() == null ? "" : algorithm.getFullClassName());
        }
        // 创建科技成就指数对象
        TechnologyAchievementIndex targetTaiObj = new TechnologyAchievementIndex();
        if (calcExecParam.getModifiedDataList() == null || calcExecParam.getModifiedDataList().isEmpty()) {
            //缓存中没有数据集的数据时从数据库取出并放入缓存
            if (taiDataList == null || taiDataList.isEmpty()) {
                taiDataList = taiService.list();
            }
        } else {
            //拿到修改后的数据集
            taiDataList = calcExecParam.getModifiedDataList();
            this.ifDataSetModified = true;
        }

        int targetObjLine = 0;
        // 创建原始数据集二维数组
        Double[][] originDataArr = new Double[taiDataList.size()][taiDataList.getClass().getDeclaredFields().length - 2];
        for (int i = 0; i < taiDataList.size(); i++) {
            // 从数据集中找到要测算的对象
            if (taiDataList.get(i).getId().equals(calcExecParam.getTargetId())) {
                targetTaiObj = taiDataList.get(i);
                // 保存行数
                targetObjLine = i;
            }
            // 构造行数据
            Double[] row = {taiDataList.get(i).getPatents(), taiDataList.get(i).getRoyalties(), taiDataList.get(i).getInternet(), taiDataList.get(i).getExports(),
                    taiDataList.get(i).getTelephones(), taiDataList.get(i).getElectricity(), taiDataList.get(i).getSchooling(), taiDataList.get(i).getUniversity()};
            // 进行数据替换
            originDataArr[i] = row;
        }

        if (targetTaiObj.getId() == null) {
            throw new Exception("没有这个构件对象");
        }
        //遍历对象属性，将指标属性及属性值放入map，用于后续计算
        Map<String, Double> indicatorValueMap = new HashMap<>();

        Field[] fields = targetTaiObj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().equals("id") || field.getName().equals("countryName")) {
                continue;
            }
            try {
                indicatorValueMap.put(field.getName(), (Double) field.get(targetTaiObj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return new Object[]{algorithmMap, indicatorValueMap, originDataArr, targetObjLine};
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

        this.taiDataList.clear();

        this.weightMap.clear();
        this.baseIndicatorValueMap.clear();

        this.execResult = null;

        this.constructObjId = 0L;

        this.ifDataSetModified = true;
        return true;
    }

}
