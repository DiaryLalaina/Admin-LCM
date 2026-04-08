package com.work.cashier.controller.infoTable;

import com.work.cashier.Application;
import com.work.cashier.alert.AlertMessage;
import com.work.cashier.api.ApiClient;
import com.work.cashier.controller.page.CustomerOrders;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.order.OrderLineDTO;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Setter;

import java.io.IOException;
import java.util.List;

public class OrderInfo {

    @FXML
    private Label nameCustomer,totalPrice;

    @FXML
    private VBox contentOrderLines;

    @Setter
    private OrderDTO dto;

    private final ControlsOption controlsOption = new ControlsOption();

    public void setData(){
        CustomerDTO customerDTO = ApiClient.getOneEntity(
                "http://192.168.7.2:8080/customer/"+dto.getIdCustomer(), CustomerDTO.class);
        assert customerDTO != null;
        nameCustomer.setText(customerDTO.getFirstName());
        dto.setNameCustomer(nameCustomer.getText());
        int total = 0;
        for (OrderLineDTO orderLineDTO : dto.getOrderLines()){
            total += (orderLineDTO.getQuantity()*orderLineDTO.getPrice());
        }
        totalPrice.setText(controlsOption.thousandSeparator(total)+" Ar");
        showOrderLineList();
    }

    @FXML
    void delete() {
        if(new AlertMessage("Êtes-vous sûr de supprimer ?").confirmMessage()) {
            for(OrderLineDTO lineDTO : dto.getOrderLines()) {
                String urlOrderLine = "http://192.168.7.2:8080/orderLine/delete/" + lineDTO.getId();
                ApiClient.delete(urlOrderLine);
            }
            String urlOrder = "http://192.168.7.2:8080/order/delete/" + dto.getId();
            if (ApiClient.delete(urlOrder)) {
                CustomerOrders.showOrderList();
            }
        }
    }

    private void showOrderLineList(){
        List<OrderLineDTO> orderLineDTOList = dto.getOrderLines();
        for (OrderLineDTO dto : orderLineDTOList) {
            addOrderInTable(dto);
        }
    }

    private void addOrderInTable(OrderLineDTO dto){
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Application.class.getResource("info_table/orderLineInfo.fxml"));
        try {
            HBox hBox = fxmlLoader.load();
            OrderLineInfo info = fxmlLoader.getController();
            info.setOrderLineDTO(dto);
            info.setData();
            contentOrderLines.getChildren().add(hBox);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
