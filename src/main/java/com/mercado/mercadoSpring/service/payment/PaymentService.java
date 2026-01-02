package com.mercado.mercadoSpring.service.payment;
import com.mercado.mercadoSpring.constants.currency.Currency;
import com.mercado.mercadoSpring.constants.paymentProvider.PaymentProvider;
import com.mercado.mercadoSpring.constants.paymentStatus.PaymentStatus;
import com.mercado.mercadoSpring.dto.payment.CreatePaymentRequestDTO;
import com.mercado.mercadoSpring.dto.payment.CreatePaymentResponseDTO;
import com.mercado.mercadoSpring.entity.payment.Payment;
import com.mercado.mercadoSpring.repository.payment.PaymentRepository;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final StripeClient stripeClient;
    public PaymentService(
            PaymentRepository paymentRepository,
            StripeClient stripeClient
    ) {
        this.paymentRepository = paymentRepository;
        this.stripeClient = stripeClient;
    }
    public CreatePaymentResponseDTO createPaymentIntent(CreatePaymentRequestDTO requestDTO) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(requestDTO.amount())
                .setCurrency(requestDTO.currency())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .putMetadata("order_id", String.valueOf(requestDTO.orderId()))
                .putMetadata("user_id", String.valueOf(requestDTO.customerId()))
                .build();

        PaymentIntent paymentIntent = stripeClient.v1().paymentIntents().create(params);

        Payment payment = new Payment();

        payment.setProvider(PaymentProvider.STRIPE);
        payment.setProviderRef(paymentIntent.getId());

        payment.setOrderId(String.valueOf(requestDTO.orderId()));

        payment.setUserId(String.valueOf(requestDTO.customerId()));

        payment.setAmount(requestDTO.amount());

        payment.setCurrency(Currency.valueOf(
                requestDTO.currency().toUpperCase().trim()
        ));
        payment.setStatus(PaymentStatus.CREATED);

        payment = paymentRepository.save(payment);
        return new CreatePaymentResponseDTO(
                payment.getUserId(),
                paymentIntent.getClientSecret(),
                payment.getStatus().name(),
                payment.getProvider().name(),
                payment.getProviderRef()
        );
    }

    public Payment findPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public List<Payment> findAllPayments() {
        return paymentRepository.findAll();
    }
}
