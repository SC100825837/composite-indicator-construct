package com.cvicse.cic.util;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

@Data
public class PropertiesUtil {
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    public static Properties getProperties(String location) {
        Properties properties = null;
        try {
            logger.info("加载资源[{}]", location);
            properties = PropertiesLoaderUtils.loadProperties(new EncodedResource(new ClassPathResource(location), "UTF-8"));
        } catch (IOException e) {
            logger.error("加载资源[{}]失败", location);
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return properties;
    }
}
