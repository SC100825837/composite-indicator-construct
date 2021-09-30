package com.cvicse.cic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @program: do-something
 * @description: 解决跨域
 * @author: SunChao
 * @create: 2021-04-07 16:44
 **/
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") //允许跨域访问的路径
//                .allowedOriginPatterns("*") //允许跨域访问的源
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE") //允许请求的方法
                .maxAge(168000) //预检间隔时间
                .allowedHeaders("*") //允许头部设置
                .allowCredentials(true); //是否发送cookie
    }
}
