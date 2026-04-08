package com.work.cashier.controller.infoTable;

import com.work.cashier.constants.VARIABLE_STATIC;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.order.OrderLineDTO;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Getter;
import lombok.Setter;

public class DailyUnpaidInfo {

    @FXML
    private Label customer,quantity,price,total,payed,unpaid,expense;

    @Setter
    private OrderDTO orderDTO;

    @Setter
    private int amountPayed,amountUnpaid,amountExpense;

    @Getter @Setter
    private String customerData,quantityData,priceData,totalData,payedData,unpaidData,expenseData;

    public void setData(){
        ControlsOption controlsOption = new ControlsOption();
        OrderLineDTO orderLineDTO = orderDTO.getOrderLines().getFirst();
        customer.setText(orderDTO.getNameCustomer());
        quantity.setText(orderLineDTO.getQuantity()+"");
        price.setText(orderLineDTO.getPrice()+"");
        total.setText(controlsOption.thousandSeparator(orderDTO.getTotalPrice()));
        payed.setText(controlsOption.thousandSeparator(amountPayed));
        unpaid.setText(controlsOption.thousandSeparator(amountUnpaid));
        expense.setText(controlsOption.thousandSeparator(amountExpense));
        setDataPrint();
        VARIABLE_STATIC.dailyUnpaidInfoList.add(this);
    }

    public void setDataPrint(){
        setCustomerData(customer.getText());
        setQuantityData(quantity.getText());
        setPriceData(price.getText());
        setTotalData(orderDTO.getTotalPrice()+"");
        setPayedData(amountPayed+"");
        setUnpaidData(amountUnpaid+"");
        setExpenseData(amountExpense+"");
    }
}
