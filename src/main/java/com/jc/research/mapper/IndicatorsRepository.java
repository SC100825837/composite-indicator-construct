package com.jc.research.mapper;

import com.jc.research.entity.CompositeIndicators;
import com.jc.research.entity.SecondLevelIndicator;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndicatorsRepository extends Neo4jRepository<SecondLevelIndicator, Long> {

	@Query("match (fl:First_level_Indicator {name: " + " 'humman_skills' " + "}) <-[:CONSTITUTE]- (sl:Second_level_Indicator) return sl")
	List<SecondLevelIndicator> getSecondNodesByFirstNodeName();
}
