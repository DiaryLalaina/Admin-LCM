package com.project.sales.data_transfer_object.stockDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionAccountingDTO {
    private String date;
    private int qty,unitPrice,total;
}
