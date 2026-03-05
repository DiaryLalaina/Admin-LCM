package com.work.cashier.controller.infoTable;

import com.work.cashier.data_transfert_object.customer.TopCustomerDTO;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TopCustomerInfo {

    @FXML
    private Label customer;

    @FXML
    private Label phone;

    @FXML
    private Label info;

    private final ControlsOption controlsOption = new ControlsOption();

    public void setData(int number,TopCustomerDTO dto){
        customer.setText(number+" : "+dto.getFirstName());
        phone.setText(dto.getPhoneNumber());
        info.setText(controlsOption.thousandSeparator(dto.getTotalQuantity())+
                " pains : "+controlsOption.thousandSeparator(dto.getTotalAmount())+ "Ar");
    }
}
