package com.work.cashier.controller.infoTable;

import com.jfoenix.controls.JFXCheckBox;
import com.work.cashier.constants.VARIABLE_STATIC;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.order.OrderLineDTO;
import com.work.cashier.data_transfert_object.payment.PaymentDTO;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class FileUserOrderInfo {

    @FXML
    private Label date,nbPayment,total,payed,expense,remain;

    @FXML
    private JFXCheckBox checkBox;

    @FXML @Getter
    private VBox containerOrderLines;

    @Setter
    private OrderDTO dto;

    @Getter @Setter
    private String dateData,qty,price,nbPaymentData,totalData,payedData,expenseData,remainData;

    @Getter @Setter
    private boolean status;

    private final ControlsOption controlsOption = new ControlsOption();

    public void setData(){
        List<PaymentDTO> paymentDTOList = dto.getPayments();
        int totalPayment = 0;
        int totalExpense = 0;
        int amount = 0;
        if(!paymentDTOList.isEmpty()){
            for(PaymentDTO paymentDTO : paymentDTOList){
                amount += paymentDTO.getAmount();
                totalPayment += (paymentDTO.getAmount()-paymentDTO.getExpense());
                totalExpense += paymentDTO.getExpense();
            }
        }
        nbPayment.setText(String.valueOf(paymentDTOList.size()));

        date.setText(LocalDate.parse(dto.getCreatedAt()).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
        OrderLineDTO orderLineDTO = dto.getOrderLines().getFirst();
        int totalPrice = orderLineDTO.getQuantity() * orderLineDTO.getPrice();
        total.setText(controlsOption.thousandSeparator(totalPrice)+" Ar");
        payed.setText(controlsOption.thousandSeparator(totalPayment)+" Ar");
        expense.setText(controlsOption.thousandSeparator(totalExpense)+" Ar");
        remain.setText(controlsOption.thousandSeparator(dto.getTotalPrice()-amount)+" Ar");
        checkBox.setSelected((dto.getTotalPrice() - amount) <= 0);

        setDateData(date.getText());
        setNbPaymentData(nbPayment.getText());
        setQty(orderLineDTO.getQuantity()+"");
        setPrice(orderLineDTO.getPrice()+"");
        setTotalData(totalPrice+"");
        setPayedData(totalPayment+"");
        setExpenseData(totalExpense+"");
        setRemainData((dto.getTotalPrice()-amount)+"");
        setStatus(checkBox.isSelected());

        if(totalPrice != dto.getTotalPrice()){
            setDataWithoutTicket(totalPrice);
        }

        VARIABLE_STATIC.sum_remain += Math.max(Integer.parseInt(remainData), 0);
        VARIABLE_STATIC.fileUserOrderInfoList.add(this);
    }

    @FXML
    void displayPayment(){
        VARIABLE_STATIC.data.clear();
        String[] listData = {String.valueOf(dto.getIdCustomer()),"FICHE CLIENT / "+dto.getId(),
                String.valueOf(dto.getCreatedAt()),String.valueOf(dto.getCreatedAt()),
                payed.getText(),remain.getText()};
        VARIABLE_STATIC.data.addAll(List.of(listData));
        new SwitchScene().showFloatNode("paymentView",0);
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

        total.setText(controlsOption.thousandSeparator(totalPrice));
        payed.setText(controlsOption.thousandSeparator((totalPrice - dto.getTotalPrice() + sumPayment)));
        remain.setText(controlsOption.thousandSeparator((dto.getTotalPrice() - sum)));
        expense.setText(controlsOption.thousandSeparator(sumExpense));
        setPayedData((totalPrice - dto.getTotalPrice() + sumPayment)+"");
        setRemainData((dto.getTotalPrice() - sum)+"");
    }
}