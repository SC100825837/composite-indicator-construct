package com.jc.research.entity.DTO;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: neo4j
 * @description:
 * @author: SunChao
 * @create: 2021-06-23 17:30
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class TierGraphDTO {

	/**
	 * id
	 */
	private Long id;

	/**
	 * 节点名称
	 */
	private String name;

	/**
	 * 两个连线之间描述
	 */
	private String linkDes;

	/**
	 * 鼠标悬浮节点的提示框显示得内容
	 */
	private String des;

	/**
	 * 类别
	 */
	private Long category;

	/**
	 * 子节点个数
	 */
	private int childNum;

	/**
	 * 子节点
	 */
	private List<TierGraphDTO> children = new ArrayList<>();

}
