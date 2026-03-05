package com.work.cashier.data_transfert_object.customer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CashOutDTO {
    private Long idCustomer;
    private String lastName,firstName;
    private Long total, paid;
}