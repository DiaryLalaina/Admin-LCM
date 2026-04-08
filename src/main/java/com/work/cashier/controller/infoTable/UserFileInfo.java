package com.work.cashier.controller.infoTable;

import com.work.cashier.constants.VARIABLE_STATIC;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.order.OrderLineDTO;
import com.work.cashier.data_transfert_object.payment.PaymentDTO;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class UserFileInfo {

    @FXML
    private Label date,bread,price,amount,nbPayment,payment,expense,remain;

    private final ControlsOption controlsOption = new ControlsOption();

    @Setter
    private OrderDTO dto;

    public void setData(){
        List<PaymentDTO> paymentDTOList = dto.getPayments();
        int totalPayment = 0;
        int totalExpense = 0;
        int total = 0;
        if(!paymentDTOList.isEmpty()){
            for(PaymentDTO paymentDTO : paymentDTOList){
                total += paymentDTO.getAmount();
                totalPayment += (paymentDTO.getAmount()-paymentDTO.getExpense());
                totalExpense += paymentDTO.getExpense();
            }
        }
        nbPayment.setText(String.valueOf(paymentDTOList.size()));

        date.setText(LocalDate.parse(dto.getCreatedAt()).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
        OrderLineDTO orderLineDTO = dto.getOrderLines().getFirst();
        int totalPrice = orderLineDTO.getQuantity() * orderLineDTO.getPrice();
        bread.setText(controlsOption.thousandSeparator(orderLineDTO.getQuantity()));
        price.setText(controlsOption.thousandSeparator(orderLineDTO.getPrice()));
        amount.setText(controlsOption.thousandSeparator(totalPrice));
        payment.setText(controlsOption.thousandSeparator(totalPayment));
        expense.setText(controlsOption.thousandSeparator(totalExpense));
        remain.setText(controlsOption.thousandSeparator(dto.getTotalPrice()-total)+" Ar");

        if(totalPrice != dto.getTotalPrice()){
            setDataWithoutTicket(totalPrice);
            return;
        }

        VARIABLE_STATIC.sum_amount += totalPrice;
        VARIABLE_STATIC.sum_paid += totalPayment;
        VARIABLE_STATIC.sum_remain += Math.max((dto.getTotalPrice() - total),0);
        VARIABLE_STATIC.sum_expense += totalExpense;
    }

    private void setDataWithoutTicket(int totalPrice){
        int sum = 0;
        int sumPayment = 0;
        int sumExpense = 0;
        for(PaymentDTO paymentDTO : dto.getPayments()){
            sum += paymentDTO.getAmount();
            sumPayment += (paymentDTO.getAmount()-paymentDTO.getExpense());
            sumExpense += paymentDTO.getExpense();
        }

        amount.setText(controlsOption.thousandSeparator(totalPrice));
        payment.setText(controlsOption.thousandSeparator((totalPrice - dto.getTotalPrice() + sumPayment)));
        remain.setText(controlsOption.thousandSeparator((dto.getTotalPrice() - sum)));
        expense.setText(controlsOption.thousandSeparator(sumExpense));

        VARIABLE_STATIC.sum_amount += totalPrice;
        VARIABLE_STATIC.sum_paid += (totalPrice - dto.getTotalPrice() + sumPayment);
        VARIABLE_STATIC.sum_remain += Math.max((dto.getTotalPrice() - sum),0);
        VARIABLE_STATIC.sum_expense += sumExpense;
    }
}

