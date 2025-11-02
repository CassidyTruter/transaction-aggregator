package com.fintech.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI transactionAggregationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Transaction Aggregation System API")
                        .description("A comprehensive REST API for aggregating, categorizing, and analyzing financial transactions. " +
                                "This system provides endpoints for transaction management, category-based summaries, " +
                                "account-level analytics, and flexible filtering capabilities. " +
                                "Supports multiple transaction sources and automated categorization.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FinTech Team")
                                .email("support@fintech.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
