package com.megait.soir.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class ResourceConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(
                "/images/**",
                "/css/**",
                "/js/**",
                "/assets/**"
        ).addResourceLocations(
                "classpath:/static/images/",
                "classpath:/static/css/",
                "classpath:/static/js/",
                "classpath:/static/assets/",
                "classpath:/templates/view/"
        );
    }
}
