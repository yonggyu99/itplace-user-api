package com.itplace.userapi.common.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("itPlace 유저 API 명세서")
                        .version("v1.0.0"))
                .servers(List.of(
                        new Server()
                                .url("http://itplace-api.kro.kr")
                                .description("개발용 서버")
                ));
    }
}
