package com.work.cashier.data_transfert_object.stock;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StockTransactionDTO {
    private Long id;

    private Long ingredientId;

    private String ingredientName,reason;

    private Double quantityBefore, quantityUsed, quantityAfter;

    private String transactionDate,transactionTime;

    private String unitType;



}
