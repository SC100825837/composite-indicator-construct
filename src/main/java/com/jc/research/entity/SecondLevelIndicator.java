package com.jc.research.entity;

import lombok.*;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

/**
 * @program: neo4j
 * @description:
 * @author: SunChao
 * @create: 2021-06-23 15:37
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@NodeEntity
public class SecondLevelIndicator {

	@Id
	@GeneratedValue
	private Long id;

	@Property(name = "name")
	private String name;

	@Property(name = "unit")
	private String unit;

	@Property(name = "definition")
	private String definition;

}
