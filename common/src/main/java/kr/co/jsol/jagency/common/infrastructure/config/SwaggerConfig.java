package kr.co.jsol.jagency.common.infrastructure.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {

    @Bean
    public OpenAPI swagger() {
        return new OpenAPI().info(
                new Info()
                        .title("헬스케어 플랫폼 API Doc")
                        .description("API 문서입니다.")
                        .version("0.0.1-SNAPSHOT")
        );
    }

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("001.api")
                .pathsToMatch("/api/**")
                .displayName("API V1 버전")
                .build();
    }
}
