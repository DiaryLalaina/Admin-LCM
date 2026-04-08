package com.work.cashier.controller.infoTable;

import com.jfoenix.controls.JFXButton;
import com.work.cashier.data_transfert_object.payment.PaymentDTO;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class PaymentInfo {

    @FXML
    private Label date,ticket,amount;

    @FXML
    private JFXButton printBtn;

    @Setter @Getter
    private PaymentDTO dto;

    public void setData(){
        new ControlsOption().jfxButtonOption(printBtn,"fa-print", Color.BLACK);
        LocalDate localDate = LocalDate.parse(dto.getCreatedAt());
        date.setText(localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        ticket.setText(dto.getTicket());
        amount.setText(new ControlsOption().thousandSeparator(dto.getAmount())+" Ar");
    }

    @FXML
    void printTicket(){
        SwitchScene switchScene = new SwitchScene();
        switchScene.setPaymentDTO(dto);
        switchScene.setCustomerDTO(UserInfo.getCustomerDTOClicked());
        switchScene.showFloatNode("ticketPrint",0);
    }
}
