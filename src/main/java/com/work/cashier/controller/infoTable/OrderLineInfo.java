package com.work.cashier.controller.infoTable;

import com.jfoenix.controls.JFXButton;
import com.work.cashier.api.ApiClient;
import com.work.cashier.data_transfert_object.order.GapDTO;
import com.work.cashier.data_transfert_object.order.OrderLineDTO;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

public class OrderLineInfo implements Initializable {

    @FXML
    private TextField unitPrice,outputQuantity;

    @FXML
    private Label article,subTotal;

    @FXML
    private JFXButton deleteBtn;

    private final ControlsOption controlsOption = new ControlsOption();

    @Setter
    private OrderLineDTO orderLineDTO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controlsOption.jfxButtonOption(deleteBtn,"fa-trash", Color.WHITE);
    }

    @FXML
    void delete(){

    }

    public void setData(){
        String nameProduct = ApiClient.getString("http://192.168.7.2:8080/product/getName/"+orderLineDTO.getIdProduct());
        orderLineDTO.setNameProduct(nameProduct);
        article.setText(orderLineDTO.getNameProduct());
        unitPrice.setText(orderLineDTO.getPrice()+"");
        subTotal.setText(controlsOption.thousandSeparator(orderLineDTO.getQuantity()*orderLineDTO.getPrice()));
        outputQuantity.setText(orderLineDTO.getQuantity()+"");
    }

}

