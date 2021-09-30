package com.cvicse.cic.config.minio;

import com.cvicse.cic.handler.MinioTemplate;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@EnableConfigurationProperties({MinioProperties.class})
@Configuration
public class MinioAutoConfiguration {

    @Autowired
    private MinioProperties minioProperties;

    @Bean
    @ConditionalOnMissingBean(MinioTemplate.class)
    @ConditionalOnProperty(name = "minio.url")
    public MinioTemplate template() {
        return new MinioTemplate(
                minioProperties.getUrl(),
                minioProperties.getAccessKey(),
                minioProperties.getSecretKey()
        );
    }
}
