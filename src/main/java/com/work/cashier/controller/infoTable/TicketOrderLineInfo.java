package com.work.cashier.controller.infoTable;

import com.work.cashier.api.ApiClient;
import com.work.cashier.data_transfert_object.order.GapDTO;
import com.work.cashier.data_transfert_object.order.OrderLineDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Setter;

public class TicketOrderLineInfo {

    @FXML
    private Label article,quantity,unitPrice;

    @Setter
    private OrderLineDTO orderLineDTO;

    public void setData(){
        article.setText(orderLineDTO.getNameProduct());
        unitPrice.setText(orderLineDTO.getPrice()+"");
        GapDTO oneEntity =  ApiClient.getOneEntity("http://192.168.7.2:8080/gap/getByOrderLine?idOrderLine="+
                orderLineDTO.getId(), GapDTO.class);
        int gap = oneEntity == null? 0 : oneEntity.getGap();
        orderLineDTO.setQuantity(orderLineDTO.getQuantity()+gap);
        quantity.setText(orderLineDTO.getQuantity()+"");
    }

}
