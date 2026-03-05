package com.work.cashier.controller.infoTable;

import com.work.cashier.data_transfert_object.order.OrderDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class FileUserOrderInfo {

    @FXML
    private Label date,total;

    @FXML @Getter
    private VBox containerOrderLines;

    @Setter
    private OrderDTO dto;


    public void setData(){
        date.setText(LocalDate.parse(dto.getCreatedAt()).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        total.setText(dto.getTotalPrice()+" Ar");
    }


}
