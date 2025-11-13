package com.mercado.mercadoSpring.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("💖 Mercado Online Marketplace API 💖")
                        .version("v1.0.0")
                        .description("""
                                Welcome to the ❤️ Mercado Ecommerce API ❤️!  
                                This API is lovingly crafted with **Java**, **Spring Boot**, **Hibernate**, **Spring Security**, **JWT**, and **MySQL** by **Asoh Yannick**, your friendly Backend Java Developer.  
                                
                                Here, you can explore and interact with all endpoints for products, categories, searches, and more.  
                                Enjoy your journey and code with ❤️!  
                                
                                🌐 **Source Code:** [View on GitHub](https://github.com/asohyannick/mercadoSpring)
                                """)
                        .contact(new Contact()
                                .name("Asoh Yannick ❤️")
                                .email("asohyannick3@gmail.com")
                                .url("https://www.linkedin.com/in/asohyannick/"))
                        .license(new License()
                                .name("Love License ❤️")
                                .url("https://opensource.org/licenses/MIT"))
                );
    }
}
