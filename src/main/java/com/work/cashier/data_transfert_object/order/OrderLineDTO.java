package com.work.cashier.data_transfert_object.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderLineDTO
{
    private Long id, idProduct, idOrder;

    private int quantity, subTotalPrice, price;

    @JsonIgnore
    private String nameProduct;

}