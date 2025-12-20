package com.mercado.mercadoSpring.config;
import com.stripe.StripeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class PaymentConfig {
    @Value("${stripe.apikey:NOT_FOUND}")
    private String stripeKey;

    @Bean
    public StripeClient stripeClient(@Value("${stripe.apikey}") String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("Stripe API key must be provided");
        }
        return new StripeClient(apiKey);
    }
}
