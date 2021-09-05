package com.jc.research.service.impl;

import com.google.common.collect.Maps;
import com.jc.research.entity.*;
import com.jc.research.entity.DTO.*;
import com.jc.research.entity.algorithm.Algorithm;
import com.jc.research.entity.algorithm.result.AlgorithmExecResult;
import com.jc.research.indicatorAl.facade.AlgorithmFacade;
import com.jc.research.mapper.IndicatorsRepository;
import com.jc.research.service.AlgorithmService;
import com.jc.research.service.CountryService;
import com.jc.research.service.TAIService;
import lombok.Getter;
import org.neo4j.ogm.model.Property;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.response.model.NodeModel;
import org.neo4j.ogm.response.model.RelationshipModel;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.*;

/**
 * @program: neo4j
 * @description:
 * @author: SunChao
 * @create: 2021-06-23 15:42
 **/
@Service
public class IndicatorsServiceImpl {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private IndicatorsRepository indicatorsRepository;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private TAIService taiService;

    /**
     * 指数图对象的缓存集合(层级结构)
     * 创建前端规定的节点对象集合，其中key为节点id，value为前端格式节点对象
     */
    @Getter
    private Map<Long, TierGraphDTO> tierGraphNodeMap = new HashMap<>();

    /**
     * 基础图对象的缓存集合(平铺结构)，只包含基础结构的节点
     * 创建前端规定的节点对象集合，其中key为节点id，value为前端格式节点对象
     */
    @Getter
    private List<GraphNode> graphNodeList = new ArrayList<>();
    @Getter
    private List<GraphEdge> graphEdgeList = new ArrayList<>();

    /**
     * 指数图对象的缓存集合，既包含基础节点，也包含增加的指数节点
     */
    @Getter
    private List<GraphNode> indicatorGraphNodeList = new ArrayList<>();
    @Getter
    private List<GraphEdge> indicatorGraphEdgeList = new ArrayList<>();

    /**
     * 记录当前最大的节点id，用来设置带数值的节点id
     */
    private Long currentMaxNodeId = 0L;

    /**
     * 指标节点id计数器
     */
    private Long indicatorNodeId = currentMaxNodeId;

    /**
     * 节点类型
     */
    private int category = 0;

    /**
     * 校验集合，创建节点时判断该节点或连线是否已创建
     */
    private Map<Object, String> checkExitMap = new HashMap<>();

    /**
     * 科技成就指数数据缓存
     */
    private List<TechnologyAchievementIndex> taiDataList;

    /**
     * 构建对象id缓存，如果实时数据和缓存不同则更新数据
     */
    private Long constructObjId = 0L;

    public List<SecondLevelIndicator> getSecondNodesByFirstNodeName() {
        List<SecondLevelIndicator> secondNodes = indicatorsRepository.getSecondNodesByFirstNodeName();
        System.out.println(secondNodes);
        return secondNodes;
    }

    public GraphDTO getBaseGraph() {
        //先从缓存中取数据，如果没有数据则重新构建
        if (!graphNodeList.isEmpty() && !graphEdgeList.isEmpty()) {
            return new GraphDTO(graphNodeList, graphEdgeList);
        }
        Session session = sessionFactory.openSession();
        String cypherString = "MATCH (indicators:CompositeIndicators) <-[rs1:CONSTITUTE]- (fl:First_level_Indicator) <-[rs2:CONSTITUTE]- (sl:Second_level_Indicator)\n" +
                "return indicators,fl,sl,rs1,rs2";
        //执行查询
        List<GraphNode> nodes = new ArrayList<>();
        List<GraphEdge> edges = new ArrayList<>();
        Result query = session.query(cypherString, Maps.newHashMap());
        query.forEach(record -> {
            //根据查询语句的变量取出变量所对应的节点数据
            //根节点
            NodeModel indicatorNode = (NodeModel) record.get("indicators");
            //一级节点
            NodeModel firstLevelNode = (NodeModel) record.get("fl");
            //二级节点
            NodeModel secondLevelNode = (NodeModel) record.get("sl");
            //一级节点与根节点之间的关系
            RelationshipModel indNodeAndFirstShip = (RelationshipModel) record.get("rs1");
            //二级节点与一节点之间的关系
            RelationshipModel firstAndSecondShip = (RelationshipModel) record.get("rs2");

			GraphNode node1 = createNode(new GraphNode(), indicatorNode, 0);
			if (node1 != null) {
				nodes.add(node1);
				//添加到图节点缓存中
				graphNodeList.add(node1);
			}
			GraphNode node2 = createNode(new GraphNode(), firstLevelNode, 1);
			if (node2 != null) {
				nodes.add(node2);
                graphNodeList.add(node2);
			}
			GraphNode node3 = createNode(new GraphNode(), secondLevelNode, 2);
			if (node3 != null) {
				nodes.add(node3);
                graphNodeList.add(node3);
			}

			GraphEdge edge1 = createEdge(new GraphEdge(), indNodeAndFirstShip);
			if (edge1 != null) {
				edges.add(edge1);
				graphEdgeList.add(edge1);
			}

			GraphEdge edge2 = createEdge(new GraphEdge(), firstAndSecondShip);
			if (edge2 != null) {
				edges.add(edge2);
                graphEdgeList.add(edge2);
			}

        });
		checkExitMap = new HashMap<>();
        return new GraphDTO(nodes, edges);
    }


	/**
	 * 创建节点
	 * @param graphNode
	 * @param nodeModel
	 * @return
	 */
    private GraphNode createNode(GraphNode graphNode, NodeModel nodeModel, int category) {
        if (!check(nodeModel.getId())) {
            return null;
        }
        if (currentMaxNodeId <= nodeModel.getId()) {
            currentMaxNodeId = nodeModel.getId();
            indicatorNodeId = currentMaxNodeId;
        }
        graphNode.setId(nodeModel.getId());
        graphNode.setLbName(nodeModel.getLabels()[0]);
        graphNode.setCategory(category);
        List<Property<String, Object>> propertyList = nodeModel.getPropertyList();
        for (Property<String, Object> property : propertyList) {
            graphNode.getAttributes().put(property.getKey(), property.getValue());
        }
        return graphNode;
    }

	/**
	 * 创建连线
	 * @param graphEdge
	 * @param relationshipModel
	 * @return
	 */
    private GraphEdge createEdge(GraphEdge graphEdge, RelationshipModel relationshipModel) {
        Long startNodeId = relationshipModel.getStartNode();
        Long endNodeId = relationshipModel.getEndNode();
        if (!check(startNodeId + "_" + endNodeId)) {
            return null;
        }
		graphEdge.setSourceID(relationshipModel.getStartNode());
		graphEdge.setTargetID(relationshipModel.getEndNode());
        List<Property<String, Object>> shipPropertyList2 = relationshipModel.getPropertyList();
        for (Property<String, Object> property : shipPropertyList2) {
			graphEdge.getAttributes().put(property.getKey(), property.getValue());
        }
        return graphEdge;
    }

	/**
	 * 检查该节点或者连线是否创建
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

    /**
     * 处理算法和数据
     * @param calcExecParam
     * @return
     */
    public CalcResultGraphDTO handleDataAndAlgorithm(CalcExecParamDTO calcExecParam) throws Exception {
        if (graphNodeList.isEmpty() || graphEdgeList.isEmpty()) {
            throw new Exception("数据异常，请尝试刷新页面");
        }
        //初始化数据，从数据库查询算法并实例化，从数据库查询指标构建对象
        Object[] dataAndAlgorithms = initAlgorithmAndConstructObj(calcExecParam);
        //通过算法门面执行算法计算
        AlgorithmExecResult execResult = AlgorithmFacade.calculate((Map<String, String>) dataAndAlgorithms[0], (double[][]) dataAndAlgorithms[2]);
        //得到权重计算的最终结果，即权重值数组
        double[] baseIndicatorWeight = execResult.getWeightingAndAggregation().getFinalResult()[0];
        //权重map，key为指标名称，value为权重值
        Map<String, Double> weightMap = new HashMap<>();
        TechnologyAchievementIndex tai = new TechnologyAchievementIndex();
        Field[] fields = tai.getClass().getDeclaredFields();
        int weightArrIndex = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().equals("id") || field.getName().equals("countryName")) {
                continue;
            }
            weightMap.put(field.getName(), baseIndicatorWeight[weightArrIndex++]);
        }

//        Country country = (Country) dataAndAlgorithms[1];
        //取出国家对象中的各基础指标值
//        Map baseIndicatorDataMap = JSON.parseObject(country.getBaseIndicator(), Map.class);
        Map<String, Double> baseIndicatorDataMap = (Map<String, Double>) dataAndAlgorithms[1];
        //初始化综合指标
        double compositeIndicator = 0L;
        //计算综合指标数值
        for (Object baseIndicatorName : baseIndicatorDataMap.keySet()) {
            compositeIndicator += baseIndicatorDataMap.get(baseIndicatorName.toString()) * weightMap.get(baseIndicatorName.toString());
        }
        //处理小数点位数
        compositeIndicator = handleFractional(2, compositeIndicator);

        //构建带有指标值的图数据
        constructIndicatorGraph(baseIndicatorDataMap, compositeIndicator, calcExecParam.getIndicatorConstructTarget().getId(), weightMap);

        CalcResultGraphDTO calcResultGraphDTO = new CalcResultGraphDTO();
        calcResultGraphDTO.setAlgorithmExecResult(execResult);
        calcResultGraphDTO.setCompositeIndicator(compositeIndicator);
        calcResultGraphDTO.getCompIndGraphNode().addAll(indicatorGraphNodeList);
        calcResultGraphDTO.getCompIndGraphEdge().addAll(indicatorGraphEdgeList);

        return calcResultGraphDTO;

    }

    /**
     * 构建带有指标值的图数据
     * @param baseIndicatorDataMap
     * @param compositeIndicator
     */
    private void constructIndicatorGraph(Map baseIndicatorDataMap, double compositeIndicator, Long id, Map<String, Double> weightMap) {
        //先从缓存中取数据，如果没有数据则重新构建
        if (!indicatorGraphNodeList.isEmpty() && !indicatorGraphEdgeList.isEmpty() && constructObjId.equals(id)) {
            return;
        }
        if (!indicatorGraphNodeList.isEmpty() && !indicatorGraphEdgeList.isEmpty()) {
            indicatorGraphNodeList.clear();
            indicatorGraphEdgeList.clear();
        }
        indicatorGraphNodeList.addAll(graphNodeList);
        indicatorGraphEdgeList.addAll(graphEdgeList);
        constructObjId = id;
        for (GraphNode graphNode : graphNodeList) {
            //找到类别为2的层级节点，也就是子叶节点
            if (graphNode.getCategory() == 2) {
                //创建指标节点，并设置属性
                GraphNode baseIndicatorDataNode = new GraphNode();
                baseIndicatorDataNode.setId(++indicatorNodeId);
                baseIndicatorDataNode.getAttributes().put("indicatorValue", "基础指标值：" + baseIndicatorDataMap.get(graphNode.getAttributes().get("name").toString()));
                baseIndicatorDataNode.setCategory(3);
                baseIndicatorDataNode.setLbName("基础指标值");

                //创建权重节点，并设置属性
                GraphNode weightNode = new GraphNode();
                weightNode.setId(++indicatorNodeId);
                weightNode.getAttributes().put("indicatorValue", "权重：" + handleFractional(2, weightMap.get(graphNode.getAttributes().get("name").toString())));
                weightNode.setCategory(4);
                weightNode.setLbName("权重值");

                //创建连线，并设置属性，基础指标节点由指标值指向 通用指标名称
                GraphEdge indicatorGraphEdge = new GraphEdge();
                indicatorGraphEdge.setSourceID(baseIndicatorDataNode.getId());
                indicatorGraphEdge.setTargetID(graphNode.getId());
                //创建连线，并设置属性，权重节点 指向 通用指标名称
                GraphEdge weightGraphEdge = new GraphEdge();
                weightGraphEdge.setSourceID(weightNode.getId());
                weightGraphEdge.setTargetID(graphNode.getId());

                indicatorGraphNodeList.add(baseIndicatorDataNode);
                indicatorGraphNodeList.add(weightNode);
                //添加到新创建的连线放置到连线集合缓存中
                indicatorGraphEdgeList.add(indicatorGraphEdge);
                indicatorGraphEdgeList.add(weightGraphEdge);
            }
        }
        //创建综合指标值节点，并设置属性
        GraphNode compIndGraphNode = new GraphNode();
        compIndGraphNode.setId(++indicatorNodeId);
        compIndGraphNode.getAttributes().put("indicatorValue", "综合指标值：" + compositeIndicator);
        compIndGraphNode.setLbName("综合指标值");
        compIndGraphNode.setCategory(5);
        //创建连线，并设置属性，综合指标值节点由 指标值 指向 通用指标名称
        GraphEdge graphEdge = new GraphEdge();
        graphEdge.setSourceID(compIndGraphNode.getId());
        graphEdge.setTargetID(graphNodeList.get(0).getId());

        //将综合指标值节点放入缓存
        indicatorGraphNodeList.add(compIndGraphNode);
        //将新创建的综合指标值和通用综合指标名称的连线关系放入缓存
        indicatorGraphEdgeList.add(graphEdge);

        //指标id计数器重置为原图数据的最大id
        indicatorNodeId = currentMaxNodeId;
    }

    /**
     * 初始化算法数、和构造对象数据、数据集
     *
     * @param calcExecParam
     * @return
     */
    private Object[] initAlgorithmAndConstructObj(CalcExecParamDTO calcExecParam) throws Exception {
        Map<String, Long> algorithmIdMap = calcExecParam.getAlgorithms().getAllAlgorithmIds();
        List<Algorithm> algorithms = algorithmService.listByIds(algorithmIdMap.values());

        Map<String, String> algorithmMap = new HashMap<>();
        for (Algorithm algorithm : algorithms) {
            algorithmMap.put(algorithm.getStepName(), algorithm.getFullClassName() == null ? "" : algorithm.getFullClassName());
        }
        /*Country country = countryService.getById(calcExecParam.getIndicatorConstructTarget().getId());
        if (country == null) {
            return null;
        }*/
        TechnologyAchievementIndex targetTaiObj = new TechnologyAchievementIndex();
        //缓存中没有数据集的数据时从数据库取出并放入缓存
        if (taiDataList == null || taiDataList.isEmpty()) {
            taiDataList = taiService.list();
        }
        double[][] taiDataRows = new double[taiDataList.size()][taiDataList.getClass().getDeclaredFields().length - 2];
        for (int i = 0; i < taiDataList.size(); i++) {
            if (taiDataList.get(i).getId().equals(calcExecParam.getIndicatorConstructTarget().getId())) {
                targetTaiObj = taiDataList.get(i);
            }
            double[] row = {taiDataList.get(i).getPatents(), taiDataList.get(i).getRoyalties(), taiDataList.get(i).getInternet(), taiDataList.get(i).getExports(),
                    taiDataList.get(i).getTelephones(), taiDataList.get(i).getElectricity(), taiDataList.get(i).getSchooling(), taiDataList.get(i).getUniversity()};
            taiDataRows[i] = row;
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
        return new Object[]{algorithmMap, indicatorValueMap, taiDataRows};
    }

    /**
     * 按照规定小数点位数处理小数
     *
     * @param digit
     * @param origin
     * @return
     */
    private double handleFractional(int digit, double origin) {
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setMaximumFractionDigits(digit);
        return Double.parseDouble(numberInstance.format(origin));
    }

    /**
     * 拿到所有节点，并组成成层级结构
     *
     * @return
     */
    /*@Deprecated
    public TierGraphDTO getCompIndNodeByTier() {
        Session session = sessionFactory.openSession();
        String cypherString = "MATCH (indicators:CompositeIndicators) <-[:CONSTITUTE]- (fl:First_level_Indicator) <-[:CONSTITUTE]- (sl:Second_level_Indicator)\n" +
                "return indicators,fl,sl";

        //执行查询
        Result query = session.query(cypherString, Maps.newHashMap());

        //存放从neo4j查询的节点数据，key为节点id，value为节点
        Map<Long, NodeModel> nodeMap = new HashMap<>();
        //存放节点与节点之间的关系，key为当前节点id，value为父节点id
        Map<Long, Long> nodesRelationshipMap = new HashMap<>();
        //存放节点的类别，key为当前节点id，value为类别数值
        Map<Long, Long> nodeCategoryMap = new HashMap<>();

        //用lambda表达式需要给变量加final，因此在这里声明一个存放根节点的数组
        long[] rootNodeId = new long[1];
        //query数据的总条目是子叶节点的个数，每个条目分别包含着从根节点到子叶节点整条路径的数据
        query.forEach(record -> {
            //根据查询语句的变量取出变量所对应的节点数据
            //根节点
            NodeModel indicatorNode = (NodeModel) record.get("indicators");
            //一级节点
            NodeModel firstLevelNode = (NodeModel) record.get("fl");
            //二级节点
            NodeModel secondLevelNode = (NodeModel) record.get("sl");
            //取出对应的节点id
            Long indicatorNodeId = indicatorNode.getId();
            Long firstLevelNodeId = firstLevelNode.getId();
            Long secondLevelNodeId = secondLevelNode.getId();
            //将根节点存起来，后续取出返回
            rootNodeId[0] = indicatorNodeId;
            //如果节点集合中没有当前节点，则放入
            //处理根节点
            if (!nodeMap.containsKey(indicatorNodeId)) {
                nodeMap.put(indicatorNodeId, indicatorNode);
                //向类别集合中存放当前节点的类别
                nodeCategoryMap.put(indicatorNodeId, 0L);
            }
            //处理一级节点
            if (!nodeMap.containsKey(firstLevelNodeId)) {
                nodeMap.put(firstLevelNodeId, firstLevelNode);
                //向关系集合中存放当前节点id和父节点的id
                nodesRelationshipMap.put(firstLevelNodeId, indicatorNodeId);
                nodeCategoryMap.put(firstLevelNodeId, 1L);
            }
            //处理二级节点
            if (!nodeMap.containsKey(secondLevelNodeId)) {
                nodeMap.put(secondLevelNodeId, secondLevelNode);
                nodesRelationshipMap.put(secondLevelNodeId, firstLevelNodeId);
                nodeCategoryMap.put(secondLevelNodeId, 2L);
            }
        });

        //根据id取出Neo4j节点，并将其属性设置到DTO中
        for (Long nodeId : nodeMap.keySet()) {
            NodeModel nodeModel = nodeMap.get(nodeId);
            TierGraphDTO tierGraphDTO = new TierGraphDTO();
            tierGraphDTO.setId(nodeId);
            //设置节点名称
            List<Property<String, Object>> propertyList = nodeModel.getPropertyList();
            propertyList.forEach(pro -> {
                if ("name".equals(pro.getKey())) {
                    tierGraphDTO.setName(pro.getValue().toString());
                }
            });
            //设置节点标签
            tierGraphDTO.setDes(nodeModel.getLabels()[0]);
            //设置节点的类别
            tierGraphDTO.setCategory(nodeCategoryMap.get(nodeId));
            //设置连线描述
            tierGraphDTO.setLinkDes("constitute");
            //将设置好属性的DTO放入DTO集合
            tierGraphNodeMap.put(nodeId, tierGraphDTO);
        }

        //设置子节点
        for (Long nodeId : nodesRelationshipMap.keySet()) {
            //从关系集合中取出当前节点对应的父节点
            Long parentId = nodesRelationshipMap.get(nodeId);
            //根据父节点id取出父节点，并将当前节点添加到父节点的childrenList中
            tierGraphNodeMap.get(parentId).getChildren().add(tierGraphNodeMap.get(nodeId));
        }

        //设置子节点数量
        for (Long nodeId : tierGraphNodeMap.keySet()) {
            TierGraphDTO tierGraphDTO = tierGraphNodeMap.get(nodeId);
            tierGraphDTO.setChildNum(tierGraphDTO.getChildren().size());
        }
        return tierGraphNodeMap.get(rootNodeId[0]);
    }*/

    /**
     * 将计算结果和基础指标数值加入图数据
     *
     * @param baseIndicatorDataMap
     * @param compositeIndicator
     * @return
     */
    /*@Deprecated
    private TierGraphDTO execIndCalcTierGraph(Map baseIndicatorDataMap, double compositeIndicator) {
        for (Long graphNodeId : tierGraphNodeMap.keySet()) {
            TierGraphDTO tierGraphDTO = tierGraphNodeMap.get(graphNodeId);
            if (tierGraphDTO.getCategory() == 2) {
                TierGraphDTO baseIndicatorDataNode = new TierGraphDTO();
                baseIndicatorDataNode.setName(baseIndicatorDataMap.get(tierGraphDTO.getName().toLowerCase()).toString());
                tierGraphDTO.getChildren().add(baseIndicatorDataNode);
            }
        }
        TierGraphDTO compIndGraph = new TierGraphDTO();
        compIndGraph.setName(String.valueOf(compositeIndicator));
        compIndGraph.getChildren().add(tierGraphNodeMap.get(0L));
        return compIndGraph;
    }*/

}
