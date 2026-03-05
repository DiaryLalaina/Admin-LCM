package com.work.cashier.data_transfert_object.customer;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class CustomerReductionDTO {
    private Long id,idCustomer,idProduct;
    private Integer price;
}
