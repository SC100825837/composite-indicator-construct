package com.jc.research.config;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @program: neo4j
 * @description:
 * @author: SunChao
 * @create: 2021-06-23 16:33
 **/
@Configuration
@EnableNeo4jRepositories(basePackages = "com.pig4cloud.pigx.neo4j.mapper")
@EnableTransactionManagement
public class Neo4jConfiguration {

	@Value("${spring.data.neo4j.uri}")
	private String databaseUrl;

	@Value("${spring.data.neo4j.username}")
	private String userName;

	@Value("${spring.data.neo4j.password}")
	private String password;

	@Bean
	public SessionFactory sessionFactory() {
		// with domain entity base package(s)
		return new SessionFactory(configuration(), "com.pig4cloud.pigx.neo4j.entity");
	}

	@Bean
	public org.neo4j.ogm.config.Configuration configuration() {
		return new org.neo4j.ogm.config.Configuration.Builder()
				.uri(databaseUrl)
				.credentials(userName, password)
				.build();
	}

	@Bean
	public Neo4jTransactionManager transactionManager() {
		return new Neo4jTransactionManager(sessionFactory());
	}
}
