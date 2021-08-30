package com.jc.research.entity;

import lombok.*;
import org.neo4j.ogm.annotation.*;

import java.util.List;

/**
 * @program: neo4j
 * @description:
 * @author: SunChao
 * @create: 2021-06-23 15:31
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@NodeEntity
public class FirstLevelIndicator {

	@Id @GeneratedValue
	private Long id;

	@Property(name = "name")
	private String name;

	@Relationship(type = "CONSTITUTE", direction = Relationship.INCOMING)
	private List<SecondLevelIndicator> constitute;

}
