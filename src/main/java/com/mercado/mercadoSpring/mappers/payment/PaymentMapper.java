package com.mercado.mercadoSpring.mappers.payment;
import com.mercado.mercadoSpring.dto.payment.CreatePaymentResponseDTO;
import com.mercado.mercadoSpring.dto.payment.PaymentDto;
import com.mercado.mercadoSpring.entity.payment.Payment;
import org.mapstruct.Mapper;
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.SET_TO_DEFAULT
)
public interface PaymentMapper {
    PaymentDto toPaymentDto(Payment payment);
    Payment toEntity(PaymentDto paymentDto);
    CreatePaymentResponseDTO toCreatePaymentResponseDTO(Payment payment);
}
