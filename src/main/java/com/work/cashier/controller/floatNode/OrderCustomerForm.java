package com.work.cashier.controller.floatNode;

import animatefx.animation.Pulse;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.ActionDatabase;
import com.work.cashier.constants.Constants;
import com.work.cashier.controller.infoTable.OrderLineInfo;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.order.OrderLineDTO;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OrderCustomerForm implements Initializable{

    @FXML
    private JFXButton hideBtn;

    @FXML
    private JFXComboBox<String> clientChoice;

    @FXML
    private Label totalPriceOrder,refOrder;

    @FXML
    private VBox containerOrderLine;

    @Setter
    private OrderDTO orderDTOSelected;

    private final ControlsOption controlsOption = new ControlsOption();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controlsOption.jfxButtonOption(hideBtn,"fa-window-close",Color.RED);

        Platform.runLater(() -> {
            showListOrderLine();
            clientChoice.getItems().add(orderDTOSelected.getNameCustomer());
            clientChoice.setValue(orderDTOSelected.getNameCustomer());
            refOrder.setText("Ref : "+ orderDTOSelected.getReference());
            totalPriceOrder.setText(controlsOption.thousandSeparator(orderDTOSelected.getTotalPrice())+" Ar");
            clientChoice.setMouseTransparent(true);
        });
    }

    @FXML
    void close() {
        Constants.action = ActionDatabase.INSERT;
        new SwitchScene().closeFloatScene(1);
    }


    private void showListOrderLine(){
        containerOrderLine.getChildren().clear();
        double delay = 0.0;
        if(!orderDTOSelected.getOrderLines().isEmpty()) {
            for (OrderLineDTO lineDTO : orderDTOSelected.getOrderLines()) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(Application.class.getResource("info_table/orderLineInfo.fxml"));
                try {
                    HBox hbox = fxmlLoader.load();
                    OrderLineInfo info = fxmlLoader.getController();

                    hbox.getProperties().put("controller", info);

                    String nameProduct = ApiClient.getString("http://192.168.7.2:8080/product/getName/"+lineDTO.getIdProduct());
                    lineDTO.setNameProduct(nameProduct);

                    info.setOrderLineDTO(lineDTO);
                    info.setData();
                    containerOrderLine.getChildren().add(hbox);

                    new NodeAnimation().animate(hbox, delay, new Pulse());

                    delay += 0.1;

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


}
