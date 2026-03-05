package com.work.cashier.controller.infoTable;

import com.work.cashier.api.ApiClient;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
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

        total.setText(formatAr(dto.getTotalPrice()));
        quantity.setText(format(orderLine.getQuantity()));
        price.setText(formatAr(orderLine.getPrice()));

        String productName = ApiClient.getString(
                "http://192.168.7.2:8080/product/getName/" + orderLine.getIdProduct()
        );
        article.setText(productName);
    }


    private String formatAr(double value) {
        return controlsOption.thousandSeparator(value) + " Ar";
    }

    private String format(double value) {
        return controlsOption.thousandSeparator(value);
    }

}