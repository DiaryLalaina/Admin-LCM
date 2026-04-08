package com.work.cashier.controller.infoTable;

import com.work.cashier.api.ApiClient;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.payment.PaymentDTO;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class CashOutTicketingInfo {

    @FXML
    private Label date,qtyBread,ticket;

    @FXML
    private Label mga_20000,mga_10000,mga_5000,mga_2000,mga_1000,mga_500,mga_200, mga_100;

    @FXML
    private Label subTotal,total,expense;

    @Setter
    private PaymentDTO dto;

    private final ControlsOption controlsOption = new ControlsOption();

    public void setData(){

        OrderDTO orderDTO = ApiClient.getOneEntity(
                "http://192.168.7.2:8080/order/"+dto.getIdOrder(), OrderDTO.class);
        assert orderDTO != null;
        date.setText(LocalDate.parse(orderDTO.getCreatedAt()).
                format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        qtyBread.setText(controlsOption.thousandSeparator(orderDTO.getOrderLines().getFirst().getQuantity()));
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

    @FXML
    void print(){
        String url = "http://192.168.7.2:8080/customer/"+dto.getIdCustomer();
        SwitchScene switchScene = new SwitchScene();
        switchScene.setPaymentDTO(dto);
        switchScene.setCustomerDTO(ApiClient.getOneEntity(url, CustomerDTO.class));
        switchScene.showFloatNode("ticketPrint",0);
    }
}
