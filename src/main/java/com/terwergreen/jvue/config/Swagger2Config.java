package com.terwergreen.jvue.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 自动生成API文档
 *
 * @author Terwer
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {
    private static final Log logger = LogFactory.getLog(Swagger2Config.class);

    @Bean
    public Docket createRestApi() {
        logger.info("创建Swagger2自动Api文档");
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.terwergreen"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("JVue APIs")
                .description("JVue API 接口文档")
                .termsOfServiceUrl("http://www.terwergreen.com")
                .contact(new Contact("Terwer", "www.terwergreen.com", "youweics@sina.com"))
                .version("1.0")
                .build();
    }
}


