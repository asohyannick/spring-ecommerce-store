package com.mercado.mercadoSpring.mappers.product;

import com.mercado.mercadoSpring.dto.product.ProductDto;
import com.mercado.mercadoSpring.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT)
public interface ProductMapper {
    ProductDto toDto (Product product);
    @Mapping(target = "isAvailable", expression = "java(productDto.getIsAvailable() != null ? productDto.getIsAvailable() : true)")
    @Mapping(target = "rating", expression = "java(productDto.getRating() != null ? productDto.getRating() : 0.0)")
    @Mapping(target = "numberOfReviews", expression = "java(productDto.getNumberOfReviews())")
    Product toEntity (ProductDto productDto);
}
