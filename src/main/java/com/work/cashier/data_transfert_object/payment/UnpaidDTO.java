package com.work.cashier.data_transfert_object.payment;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UnpaidDTO {
    private Long idCustomer,idOrder;
    private String createdAt,reference;
    private Integer total, advance;
}
