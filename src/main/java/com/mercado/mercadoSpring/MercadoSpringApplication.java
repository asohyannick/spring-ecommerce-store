package com.mercado.mercadoSpring;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
@EnableCaching
@SpringBootApplication
public class MercadoSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(MercadoSpringApplication.class, args);
	}

}
