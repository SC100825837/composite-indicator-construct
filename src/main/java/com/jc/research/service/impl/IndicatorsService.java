package com.jc.research.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.jc.research.entity.*;
import com.jc.research.entity.DTO.*;
import com.jc.research.indicatorAl.algorithm.Algorithm;
import com.jc.research.indicatorAl.entity.AlgorithmExecResult;
import com.jc.research.indicatorAl.facade.AlgorithmFacade;
import com.jc.research.mapper.IndicatorsRepository;
import com.jc.research.service.AlgorithmService;
import com.jc.research.service.CountryService;
import lombok.Getter;
import org.neo4j.ogm.model.Property;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.response.model.NodeModel;
import org.neo4j.ogm.response.model.RelationshipModel;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.*;

/**
 * @program: neo4j
 * @description:
 * @author: SunChao
 * @create: 2021-06-23 15:42
 **/
@Service
public class IndicatorsService {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private IndicatorsRepository indicatorsRepository;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private CountryService countryService;

    /**
     * 指数图对象的缓存集合
     * 创建前端规定的节点对象集合，其中key为节点id，value为前端格式节点对象
     */
    @Getter
    private Map<Long, TierGraphDTO> tierGraphNodeMap = new HashMap<>();

    /**
     * 记录当前最大的节点id，用来设置带数值的节点id
     */
    @Deprecated
    private Long currentMaxNodeId;

    /**
     * 校验集合，创建节点时判断该节点或连线是否已创建
     */
    Map<Object, String> checkExitMap = new HashMap<>();

    public List<SecondLevelIndicator> getSecondNodesByFirstNodeName() {
        List<SecondLevelIndicator> secondNodes = indicatorsRepository.getSecondNodesByFirstNodeName();
        System.out.println(secondNodes);
        return secondNodes;
    }

    public GraphDTO getBaseGraph() {
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
            //取出对应的节点id
            Long indicatorNodeId = indicatorNode.getId();
            Long firstLevelNodeId = firstLevelNode.getId();
            Long secondLevelNodeId = secondLevelNode.getId();

			GraphNode node1 = createNode(indicatorNodeId, new GraphNode(), indicatorNode);
			if (node1 != null) {
				nodes.add(node1);
			}
			GraphNode node2 = createNode(firstLevelNodeId, new GraphNode(), firstLevelNode);
			if (node2 != null) {
				nodes.add(node2);
			}
			GraphNode node3 = createNode(secondLevelNodeId, new GraphNode(), secondLevelNode);
			if (node3 != null) {
				nodes.add(node3);
			}

			Long startNode1 = indNodeAndFirstShip.getStartNode();
			Long endNode1 = indNodeAndFirstShip.getEndNode();
			String se1 = startNode1 + "_" + endNode1;
			GraphEdge edge1 = createEdge(se1, new GraphEdge(), indNodeAndFirstShip, startNode1, endNode1);
			if (edge1 != null) {
				edges.add(edge1);
			}

			Long startNode2 = firstAndSecondShip.getStartNode();
			Long endNode2 = firstAndSecondShip.getEndNode();
			String se2 = startNode2 + "_" + endNode2;
			GraphEdge edge2 = createEdge(se2, new GraphEdge(), firstAndSecondShip, startNode2, endNode2);
			if (edge2 != null) {
				edges.add(edge2);
			}

        });
		checkExitMap = new HashMap<>();
        return new GraphDTO(nodes, edges);
    }

	/**
	 * 创建节点
	 * @param nodeId
	 * @param graphNode
	 * @param nodeModel
	 * @return
	 */
    private GraphNode createNode(Object nodeId, GraphNode graphNode, NodeModel nodeModel) {
        if (!check(nodeId)) {
            return null;
        }
        graphNode.setId(nodeModel.getId());
        graphNode.setLbName(nodeModel.getLabels()[0]);
        List<Property<String, Object>> propertyList = nodeModel.getPropertyList();
        for (Property<String, Object> property : propertyList) {
            graphNode.getAttributes().put(property.getKey(), property.getValue());
        }
        return graphNode;
    }

	/**
	 * 创建连线
	 * @param nodeId
	 * @param graphEdge
	 * @param relationshipModel
	 * @param start
	 * @param end
	 * @return
	 */
    private GraphEdge createEdge(Object nodeId, GraphEdge graphEdge, RelationshipModel relationshipModel, Long start, Long end) {
        if (!check(nodeId)) {
            return null;
        }
		graphEdge.setSourceID(start);
		graphEdge.setTargetID(end);
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
     * 拿到所有节点，并组成成层级结构
     *
     * @return
     */
    @Deprecated
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
    }

    /**
     * 处理算法和数据
     *
     * @param algorithmAndConstObj
     * @return
     */
    public CalcResultTierGraphDTO handleDataAndAlgorithm(List<Map<String, Long>> algorithmAndConstObj) {
        //初始化数据，从数据库查询算法并实例化，从数据库查询指标构建对象
        Object[] dataAndAlgorithms = initAlgorithmAndConstructObj(algorithmAndConstObj);
        //通过算法门面执行算法计算
        AlgorithmExecResult execResult = AlgorithmFacade.calculate((Map<String, String>) dataAndAlgorithms[0]);
        //得到权重计算的最终结果，即权重值数组
        double[] baseIndicatorWeight = execResult.getWeightingAndAggregation().getFinalResult()[0];

        Country country = (Country) dataAndAlgorithms[1];
        //取出国家对象中的各基础指标值
        Map baseIndicatorDataMap = JSON.parseObject(country.getBaseIndicator(), Map.class);
        //初始化综合指标
        double compositeIndicator = 0L;
        int count = 0;
        //计算综合指标数值
        for (Object baseIndicatorName : baseIndicatorDataMap.keySet()) {
            compositeIndicator += Double.parseDouble(baseIndicatorDataMap.get(baseIndicatorName).toString()) * baseIndicatorWeight[count++];
        }
        //处理小数点位数
        compositeIndicator = handleFractional(2, compositeIndicator);

        TierGraphDTO compIndGraph = execIndCalcGraph(baseIndicatorDataMap, compositeIndicator);

        CalcResultTierGraphDTO calcResultTierGraphDTO = new CalcResultTierGraphDTO();
        calcResultTierGraphDTO.setAlgorithmExecResult(execResult);
        calcResultTierGraphDTO.setCompositeIndicator(compositeIndicator);
        calcResultTierGraphDTO.setCompIndGraph(compIndGraph);

        return calcResultTierGraphDTO;

    }

    /**
     * 将计算结果和基础指标数值加入图数据
     *
     * @param baseIndicatorDataMap
     * @param compositeIndicator
     * @return
     */
    private TierGraphDTO execIndCalcGraph(Map baseIndicatorDataMap, double compositeIndicator) {
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
    }

    /**
     * 初始化算法数据和构造对象数据
     *
     * @param algorithmAndConstObj
     * @return
     */
    private Object[] initAlgorithmAndConstructObj(List<Map<String, Long>> algorithmAndConstObj) {
        Map<String, Long> algorithmIdMap = algorithmAndConstObj.get(0);
        List<Algorithm> algorithms = algorithmService.listByIds(algorithmIdMap.values());

        Map<String, String> algorithmMap = new HashMap<>();
        for (Algorithm algorithm : algorithms) {
            algorithmMap.put(algorithm.getStepName(), algorithm.getFullClassName() == null ? "" : algorithm.getFullClassName());
        }
        Country country = countryService.getById(algorithmAndConstObj.get(1).get("constructObjId"));
        return new Object[]{algorithmMap, country};
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

}
