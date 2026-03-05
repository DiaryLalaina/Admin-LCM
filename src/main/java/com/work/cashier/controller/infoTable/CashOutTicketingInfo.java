package com.work.cashier.controller.infoTable;

import com.work.cashier.data_transfert_object.payment.PaymentDTO;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CashOutTicketingInfo {

    @FXML
    private Label ticket;

    @FXML
    private Label mga_20000,mga_10000,mga_5000,mga_2000,mga_1000,mga_500,mga_200, mga_100;

    @FXML
    private Label subTotal,total,expense;

    private final ControlsOption controlsOption = new ControlsOption();

    public void setData(PaymentDTO dto){
        int sum = 0;
        ticket.setText(dto.getTicket());
        expense.setText(controlsOption.thousandSeparator(dto.getExpense()));
        total.setText(controlsOption.thousandSeparator(dto.getAmount()));
        Label[] labels = {mga_20000,mga_10000,mga_5000,mga_2000,mga_1000,mga_500,mga_200, mga_100};
        Integer[] integers =  {dto.getTwentyThousand(),dto.getTenThousand(),dto.getFiveThousand(),dto.getTwoThousand(),
                dto.getOneThousand(),dto.getFiveHundred(),dto.getTwoHundred(),dto.getOneHundred()};
        int i = 0;
        for(Label label : labels){
            int resultTicket = Integer.parseInt(label.getId().split("_")[1]) * integers[i];
            label.setText(String.valueOf(integers[i]));
            sum += resultTicket;
            i++;
        }
        subTotal.setText(controlsOption.thousandSeparator(sum));
    }

}
