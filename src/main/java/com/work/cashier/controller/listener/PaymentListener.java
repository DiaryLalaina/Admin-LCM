package com.work.cashier.controller.listener;

import com.work.cashier.data_transfert_object.payment.UnpaidDTO;

public interface PaymentListener {
    void setReferenceOrder(String referenceOrder);;
}
