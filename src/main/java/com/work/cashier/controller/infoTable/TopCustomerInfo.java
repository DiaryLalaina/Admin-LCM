package com.work.cashier.controller.infoTable;

import com.work.cashier.data_transfert_object.customer.TopCustomerDTO;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Setter;

public class TopCustomerInfo {

    @FXML
    private Label customer;

    @FXML
    private Label phone;

    @FXML
    private Label info;

    @Setter
    private String month;

    private final ControlsOption controlsOption = new ControlsOption();

    public void setData(int number,TopCustomerDTO dto){
        customer.setId(String.valueOf(dto.getId()));
        customer.setText(number+" : "+dto.getFirstName());
        phone.setText(dto.getPhoneNumber());
        info.setText(controlsOption.thousandSeparator(dto.getTotalQuantity())+
                " pains : "+controlsOption.thousandSeparator(dto.getTotalAmount())+ "Ar");
    }

    @FXML
    void showFileMonthly(){
        SwitchScene switchScene = new SwitchScene();
        switchScene.setNameCustomer(customer.getText().split(" : ")[1]);
        switchScene.setIdCustomer(customer.getId());
        switchScene.setMonth(month.toUpperCase());
        switchScene.showFloatNode("monthlyUserFile",0);
    }
}
