package com.payetonkawa.order.mapper;

import org.mapstruct.Mapper;

import com.payetonkawa.order.dto.PatchOrderDto;
import com.payetonkawa.order.dto.PostOrderDto;
import com.payetonkawa.order.entity.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order fromPostDto(PostOrderDto postOrderDto);
    Order fromPatchDto(PatchOrderDto patchOrderDto);
}
