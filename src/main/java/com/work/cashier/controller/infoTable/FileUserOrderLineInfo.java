package com.work.cashier.controller.infoTable;

import com.work.cashier.api.ApiClient;
import com.work.cashier.data_transfert_object.order.GapDTO;
import com.work.cashier.data_transfert_object.order.OrderLineDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Getter;

@Getter
public class FileUserOrderLineInfo {

    @FXML
    private Label article,unitPrice,quantityOrder,outputQuantity,subTotal;

    public void setData(OrderLineDTO dto){
        article.setText(dto.getNameProduct());
        unitPrice.setText(dto.getPrice()+"");
        quantityOrder.setText(dto.getQuantity()+"");
        GapDTO oneEntity =  ApiClient.getOneEntity("http://192.168.7.2:8080/gap/getByOrderLine?idOrderLine="+
                dto.getId(), GapDTO.class);

        int gap = (oneEntity == null? 0 : oneEntity.getGap());
        int qtyFinal = dto.getQuantity() + gap;
        outputQuantity.setText(qtyFinal+"");
        int subtotalValue = dto.getPrice() * qtyFinal;
        subTotal.setText(subtotalValue+"");
    }
}
