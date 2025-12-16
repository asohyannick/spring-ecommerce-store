package com.mercado.mercadoSpring.repository.payment;
import com.mercado.mercadoSpring.entity.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByProviderAndProviderRef(String provider, String providerRef);
}
