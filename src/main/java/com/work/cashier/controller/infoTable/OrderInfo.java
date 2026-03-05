package com.work.cashier.controller.infoTable;

import com.jfoenix.controls.JFXButton;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.ActionDatabase;
import com.work.cashier.constants.Constants;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

public class OrderInfo implements Initializable {

    @FXML
    private JFXButton statisticBtn,orderLineBtn;

    @FXML
    private Label reference,nameCustomer,totalPrice;

    @Setter
    private OrderDTO dto;

    private final ControlsOption controlsOption = new ControlsOption();

    public void setData(){
        reference.setText(dto.getReference());
        CustomerDTO customerDTO = ApiClient.getOneEntity(
                "http://192.168.7.2:8080/customer/"+dto.getIdCustomer(), CustomerDTO.class);
        assert customerDTO != null;
        nameCustomer.setText(customerDTO.getFirstName());
        dto.setNameCustomer(nameCustomer.getText());
        totalPrice.setText(controlsOption.thousandSeparator(dto.getTotalPrice())+" Ar");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controlsOption.jfxButtonOption(statisticBtn,"fa-pie-chart", Color.DARKVIOLET);
        controlsOption.jfxButtonOption(orderLineBtn,"fa-list-ul", Color.DARKGREEN);
    }

    @FXML
    void statistic() {
        SwitchScene switchScene = new SwitchScene();
        switchScene.setOrderDTO(dto);
        switchScene.showFloatNode("ingredientProportion",0);
    }

    @FXML
    void orderLine(){
        Constants.action = ActionDatabase.UPDATE;
        SwitchScene switchScene = new SwitchScene();
        switchScene.setOrderDTO(dto);
        switchScene.showFloatNode("order",0);
    }

}
