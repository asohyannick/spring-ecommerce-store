package com.mercado.mercadoSpring.controller.payment;
import com.mercado.mercadoSpring.dto.payment.CreatePaymentRequestDTO;
import com.mercado.mercadoSpring.dto.payment.CreatePaymentResponseDTO;
import com.mercado.mercadoSpring.repository.payment.PaymentRepository;
import com.mercado.mercadoSpring.service.payment.PaymentService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/${API_VERSION}/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    public PaymentController(
            PaymentService paymentService,
            PaymentRepository paymentRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
    }

    @PostMapping("/create-intent")
    public CreatePaymentResponseDTO createPaymentIntent(
            @Valid @RequestBody
            CreatePaymentRequestDTO requestDTO
    ) throws StripeException {
        return paymentService.createPaymentIntent(requestDTO);
    }

    @GetMapping("/fetch-payments")
    public List<CreatePaymentResponseDTO> findAllPayments() {
        var payments = paymentService.findAllPayments();
        return payments;
    }

    @GetMapping("/fetch-payments/{id}")
    public List<PaymentService> findAllPaymentById(
            @PathVariable String id
    ) {
        var payments = paymentService.findPaymentById(id);
        return payments;
    }
}