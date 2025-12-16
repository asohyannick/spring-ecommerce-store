package com.mercado.mercadoSpring.entity.payment;
import com.mercado.mercadoSpring.constants.currency.Currency;
import com.mercado.mercadoSpring.constants.paymentProvider.PaymentProvider;
import com.mercado.mercadoSpring.constants.paymentStatus.PaymentStatus;
import jakarta.persistence.*;
import java.time.Instant;
@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payment_order_id", columnList = "orderId"),
        @Index(name = "idx_payment_user_id", columnList = "userId"),
        @Index(name = "idx_payment_provider_ref", columnList = "provider,providerRef", unique = true)
})
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private PaymentProvider provider = PaymentProvider.STRIPE;

    @Column(nullable = false)
    private String providerRef;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private String description;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
