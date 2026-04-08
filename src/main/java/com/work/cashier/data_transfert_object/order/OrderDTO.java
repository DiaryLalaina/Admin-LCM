package com.work.cashier.data_transfert_object.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.work.cashier.data_transfert_object.payment.PaymentDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OrderDTO {

    private Long id,idCustomer;

    private String reference;

    private OrderStatusType status;

    private int totalPrice;

    private String createdAt;

    @JsonIgnore
    private String nameCustomer;

    private List<OrderLineDTO> orderLines;

    private List<PaymentDTO> payments;

}
