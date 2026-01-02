package com.mercado.mercadoSpring.repository.payment;
import com.mercado.mercadoSpring.entity.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PaymentRepository extends JpaRepository<Payment, String> {}
