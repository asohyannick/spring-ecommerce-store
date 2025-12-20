package com.mercado.mercadoSpring.controller.payment;
import com.mercado.mercadoSpring.dto.payment.CreatePaymentRequestDTO;
import com.mercado.mercadoSpring.dto.payment.CreatePaymentResponseDTO;
import com.mercado.mercadoSpring.entity.payment.Payment;
import com.mercado.mercadoSpring.service.payment.PaymentService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/${API_VERSION}/payment")
@Tag(name = "Payment Management Endpoints", description = "APIs for creating and retrieving payments")
public class PaymentController {
    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    @PostMapping("/create-payment-intent")
    @ApiResponse(responseCode = "201", description = "Payment intent created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid payment request payload")
    @ApiResponse(responseCode = "500", description = "Payment provider error")
    @Operation(
            summary = "Create a payment intent",
            description = "Creates a Stripe PaymentIntent and stores a corresponding payment record."
    )
    public CreatePaymentResponseDTO createPaymentIntent(
            @Valid @RequestBody CreatePaymentRequestDTO requestDTO
    ) throws StripeException {
        return paymentService.createPaymentIntent(requestDTO);
    }

    @GetMapping("/fetch-payments")
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    @Operation(
            summary = "Fetch all payments",
            description = "Returns a list of all stored payment records."
    )
    public List<CreatePaymentResponseDTO> findAllPayments() {
        return paymentService.findAllPayments()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/fetch-payment/{id}")
    @ApiResponse(responseCode = "200", description = "Payment retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    @Operation(
            summary = "Fetch a payment by ID",
            description = "Returns a single payment record by its internal payment ID."
    )
    public CreatePaymentResponseDTO findPaymentById(@PathVariable String id) {
        Payment payment = paymentService.findPaymentById(id);
        return toDto(payment);
    }

    private CreatePaymentResponseDTO toDto(Payment payment) {
        return new CreatePaymentResponseDTO(
                payment.getId().toString(),
                null,
                payment.getStatus().name(),
                payment.getProvider().name(),
                payment.getProviderRef()
        );
    }
}
