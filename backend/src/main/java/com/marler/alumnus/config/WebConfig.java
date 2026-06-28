package com.marler.alumnus.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许前端开发服务器地址（Vite 默认端口 5173）
                .allowedOriginPatterns("*")
                // 允许的 HTTP 方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                // 允许的请求头
                .allowedHeaders("*")
                // 允许携带凭证（Cookie、Authorization 头等）
                .allowCredentials(true)
                // 预检请求缓存时间（秒）
                .maxAge(3600);
    }
}
