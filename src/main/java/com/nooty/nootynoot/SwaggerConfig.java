package com.nooty.nootynoot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket swagger() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("swagger")
                .select()
                .apis(p -> {
                    if (p.produces() != null) {
                        for (MediaType mt : p.produces()) {
                            if (mt.toString().equals("application/json")) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .build()
                .produces(Collections.singleton("application/json"))
                .apiInfo(new ApiInfoBuilder().version("1").title("hai").description("hai").build());
    }
}
