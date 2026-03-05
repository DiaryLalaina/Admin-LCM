package com.work.cashier.data_transfert_object.payment;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PaymentDTO {
    private Long id,idOrder,idCustomer;

    private String createdAt,ticket;

    private Integer twentyThousand,tenThousand,fiveThousand,twoThousand,
            oneThousand,fiveHundred,twoHundred,oneHundred,amount,expense;
}
