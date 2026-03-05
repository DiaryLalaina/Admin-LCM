package com.work.cashier.controller.infoTable;

import com.work.cashier.api.ApiClient;
import com.work.cashier.data_transfert_object.order.GapDTO;
import com.work.cashier.data_transfert_object.order.OrderLineDTO;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

public class OrderLineInfo implements Initializable {

    @FXML
    private ComboBox<String> productChoice;

    @FXML
    private TextField unitPrice,quantity,outputQuantity;

    @FXML
    private Label subTotal;

    private final ControlsOption controlsOption = new ControlsOption();

    @Setter
    private OrderLineDTO orderLineDTO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        String urlProduct = "http://192.168.7.2:8080/product/getNames";
        productChoice.getItems().addAll(ApiClient.getListString(urlProduct));
    }

    public void setData(){
        GapDTO oneEntity =  ApiClient.getOneEntity("http://l192.168.7.2:8080/gap/getByOrderLine?idOrderLine="+
                orderLineDTO.getId(), GapDTO.class);
        int gap = oneEntity == null? 0 : oneEntity.getGap();

        productChoice.setValue(orderLineDTO.getNameProduct());
        unitPrice.setText(orderLineDTO.getPrice()+"");
        quantity.setText( gap <= 0 ?
                orderLineDTO.getQuantity()-gap+"":
                orderLineDTO.getQuantity()+gap+"");
        subTotal.setText(controlsOption.thousandSeparator(orderLineDTO.getSubTotalPrice()));
        outputQuantity.setText(orderLineDTO.getQuantity()+"");
    }

}

