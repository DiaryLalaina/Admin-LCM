package com.work.cashier.data_transfert_object.order;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GapDTO {
    private Long id,idOrderLine;
    private String createdAt;
    private Integer gap;
}
