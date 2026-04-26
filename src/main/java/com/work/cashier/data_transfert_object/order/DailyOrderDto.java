package com.project.sales.data_transfer_object.orderDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DailyOrderDto {
    private String date;
    private Long totalPrice,totalPayed,totalRemain;
}
