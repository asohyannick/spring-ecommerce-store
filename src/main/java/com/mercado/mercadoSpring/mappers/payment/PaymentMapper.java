package com.mercado.mercadoSpring.mappers.payment;

import com.mercado.mercadoSpring.dto.payment.CreatePaymentResponseDTO;
import com.mercado.mercadoSpring.entity.payment.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT
)
public interface PaymentMapper {

    CreatePaymentResponseDTO toCreatePaymentResponseDTO(Payment payment);
}

