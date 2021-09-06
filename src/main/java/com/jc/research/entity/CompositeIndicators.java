package com.jc.research.entity;

import lombok.*;
import org.neo4j.ogm.annotation.*;

import java.util.List;

/**
 * @program: neo4j
 * @description:
 * @author: SunChao
 * @create: 2021-06-23 17:23
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@NodeEntity
public class CompositeIndicators {
	@Id
	@GeneratedValue
	private Long id;

	@Property(name = "name")
	private String name;

	@Property(name = "indicatorValue")
	private Double indicatorValue;

	@Relationship(type = "CONSTITUTE", direction = Relationship.INCOMING)
	private List<FirstLevelIndicator> constitute;

}
