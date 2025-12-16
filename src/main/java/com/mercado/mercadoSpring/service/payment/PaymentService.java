package com.mercado.mercadoSpring.service.payment;
import com.mercado.mercadoSpring.constants.paymentProvider.PaymentProvider;
import com.mercado.mercadoSpring.constants.paymentStatus.PaymentStatus;
import com.mercado.mercadoSpring.dto.payment.CreatePaymentRequestDTO;
import com.mercado.mercadoSpring.dto.payment.CreatePaymentResponseDTO;
import com.mercado.mercadoSpring.entity.payment.Payment;
import com.mercado.mercadoSpring.repository.payment.PaymentRepository;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
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

    public CreatePaymentResponseDTO createPaymentIntent(CreatePaymentRequestDTO requestDTO)
     throws StripeException
    {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(requestDTO.getAmount())
                .setCurrency(requestDTO.getCurrency())
                .setPaymentMethodTypes(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .putMetadata("order_id", requestDTO.getOrderId().toString())
                .putMetadata("customer_id", requestDTO.getCustomerId().toString())
                .build();

           Payment pi = stripeClient.v1().paymentIntents().create(params);
           Payment payment = new Payment();
              payment.setPaymentIntentId(pi.getId());
                payment.setAmount(requestDTO.getAmount());
                payment.setCurrency(requestDTO.getCurrency());
                payment.setStatus(PaymentStatus.CREATED);
                payment.setProvider(PaymentProvider.STRIPE);
                payment.setOrderId(requestDTO.getOrderId());
                payment.setCustomerId(requestDTO.getCustomerId());
            paymentRepository.save(payment);
        return new CreatePaymentResponseDTO(
                payment.getId(),
                pi.getClientSecret(),
                payment.getStatus(),
                payment.getProvider(),
                pi.getId()
        );
    }

    public List<Payment>  findPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public List<Payment> findAllPayments() {
        return paymentRepository.findAll();
    }
}
