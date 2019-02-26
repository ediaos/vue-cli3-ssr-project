package com.terwergreen.jvue.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * 模板引擎配置
 *
 * @author Terwer
 * @version 1.0
 * 2018/12/2 22:09
 **/
@Configuration
public class ThmeleafConfig {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Bean
    public ClassLoaderTemplateResolver vueTemplateResolver() {
        ClassLoaderTemplateResolver vueTemplateResolver = new ClassLoaderTemplateResolver();
        vueTemplateResolver.setPrefix("dist/");
        vueTemplateResolver.setSuffix(".html");
        vueTemplateResolver.setTemplateMode(TemplateMode.HTML);
        vueTemplateResolver.setCharacterEncoding("UTF-8");
        vueTemplateResolver.setOrder(0);
        vueTemplateResolver.setCheckExistence(true);
        logger.info("设置Vue模板引擎解析器");
        return vueTemplateResolver;
    }
}