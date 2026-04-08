package com.work.cashier.controller.infoTable;

import com.work.cashier.constants.VARIABLE_STATIC;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.order.OrderLineDTO;
import com.work.cashier.data_transfert_object.payment.PaymentDTO;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class FollowSaleInfo {

    @FXML
    private Label date,customer,nbPayment,bread,price,amount,paid,expense,remain;

    @Setter
    private OrderDTO dto;

    private final ControlsOption controlsOption = new ControlsOption();

    @Setter @Getter
    private String dateData,customerData,nbPaymentData,breadData,
            priceData,amountData,paidData,expenseData,remainData;

    public void setData(){
        date.setText(LocalDate.parse(dto.getCreatedAt()).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
        customer.setText(dto.getNameCustomer());
        nbPayment.setText(dto.getPayments().size()+"");
        OrderLineDTO orderLineDTO = dto.getOrderLines().getFirst();
        bread.setText(controlsOption.thousandSeparator(orderLineDTO.getQuantity()));
        price.setText(orderLineDTO.getPrice()+"");
        double total = orderLineDTO.getQuantity() * orderLineDTO.getPrice();
        if(dto.getTotalPrice() == total){
            amount.setText(controlsOption.thousandSeparator(dto.getTotalPrice()));
            double[] amounts = setAmount();
            paid.setText(controlsOption.thousandSeparator(amounts[1]));
            expense.setText(controlsOption.thousandSeparator(amounts[2]));
            remain.setText(controlsOption.thousandSeparator((dto.getTotalPrice()-amounts[0])));
        }else{
            amount.setText(controlsOption.thousandSeparator(total));
            double[] amounts = setAmount();
            paid.setText(controlsOption.thousandSeparator((total - dto.getTotalPrice() + amounts[1])));
            remain.setText(controlsOption.thousandSeparator((dto.getTotalPrice() - amounts[0])));
            expense.setText(controlsOption.thousandSeparator(amounts[2]));
        }
        setDataPrint();
        List<FollowSaleInfo> followSaleInfos = VARIABLE_STATIC.followSaleInfos;
        followSaleInfos.add(this);
    }

    private double[] setAmount(){
        double sum = 0;
        double sumPayment = 0;
        double sumExpense = 0;
        for(PaymentDTO paymentDTO : dto.getPayments()){
            sum += paymentDTO.getAmount();
            sumPayment += (paymentDTO.getAmount()-paymentDTO.getExpense());
            sumExpense += paymentDTO.getExpense();
        }

        return new double[]{sum,sumPayment,sumExpense};
    }

    private void setDataPrint(){
        setDateData(date.getText());
        setCustomerData(customer.getText());
        setNbPaymentData(nbPayment.getText());
        setBreadData(bread.getText());
        setPriceData(price.getText());
        setAmountData(amount.getText());
        setPaidData(paid.getText());
        setExpenseData(expense.getText());
        setRemainData(remain.getText());
    }
}
