package com.payetonkawa.order.mapper;

import com.payetonkawa.order.dto.PatchOrderDetailDto;
import com.payetonkawa.order.dto.PostOrderDetailDto;
import com.payetonkawa.order.entity.OrderDetail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {
    OrderDetail fromPostDto(PostOrderDetailDto postOrderDto);
    OrderDetail fromPatchDto(PatchOrderDetailDto patchOrderDto);
}
