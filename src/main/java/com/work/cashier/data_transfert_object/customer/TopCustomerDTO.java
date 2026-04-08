package com.work.cashier.data_transfert_object.customer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopCustomerDTO {
    private Long id;
    private String firstName, lastName, phoneNumber;
    private Long totalQuantity,totalAmount;
}
