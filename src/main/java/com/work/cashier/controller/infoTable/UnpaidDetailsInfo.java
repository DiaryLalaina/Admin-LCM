package com.work.cashier.controller.infoTable;

import com.work.cashier.data_transfert_object.payment.UnpaidDTO;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class UnpaidDetailsInfo {

    @FXML
    private Label refOrder,totalAmount,paid,remain;

    @Setter
    private UnpaidDTO unpaidDTO;

    public void setData(){
        ControlsOption controlsOption = new ControlsOption();
        refOrder.setText(LocalDate.parse(unpaidDTO.getCreatedAt()).
                format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        totalAmount.setText(controlsOption.thousandSeparator(unpaidDTO.getTotal())+" Ar");
        paid.setText(controlsOption.thousandSeparator(unpaidDTO.getAdvance())+" Ar");
        remain.setText(controlsOption.thousandSeparator(unpaidDTO.getTotal()- unpaidDTO.getAdvance())+" Ar");
    }

}
