package com.work.cashier.controller.infoTable;

import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.order.OrderLineDTO;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Setter;

public class OrderInformation {

    @FXML
    private Label customer,article,price,quantity,total;

    @Setter
    private OrderDTO dto;

    private final ControlsOption controlsOption = new ControlsOption();

    public void setData() {

        OrderLineDTO orderLine = dto.getOrderLines().getFirst();

        customer.setId(String.valueOf(dto.getId()));
        customer.setText(dto.getNameCustomer());

        total.setText(formatAr(orderLine.getQuantity()*orderLine.getPrice()));
        quantity.setText(format(orderLine.getQuantity()));
        price.setText(formatAr(orderLine.getPrice()));
        article.setText("PAIN GM");
    }


    private String formatAr(double value) {
        return controlsOption.thousandSeparator(value) + " Ar";
    }

    private String format(double value) {
        return controlsOption.thousandSeparator(value);
    }

}