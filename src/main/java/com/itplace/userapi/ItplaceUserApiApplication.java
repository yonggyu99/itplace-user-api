package com.itplace.userapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories(  // jpa 사용되는 패키지
        basePackages = {
                "com.itplace.userapi.benefit",
                "com.itplace.userapi.common",
                "com.itplace.userapi.favorite",
                "com.itplace.userapi.history",
                "com.itplace.userapi.map",
                "com.itplace.userapi.partner",
                "com.itplace.userapi.security",
                "com.itplace.userapi.user",
        }
)
@EnableMongoRepositories(  // mongo 사용되는 패키지
        basePackages = "com.itplace.userapi.log"
)
public class ItplaceUserApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItplaceUserApiApplication.class, args);
    }

}
