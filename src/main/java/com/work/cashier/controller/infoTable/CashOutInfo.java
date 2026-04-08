package com.work.cashier.controller.infoTable;

import com.jfoenix.controls.JFXButton;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.VARIABLE_STATIC;
import com.work.cashier.data_transfert_object.customer.CashOutDTO;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
import com.work.cashier.data_transfert_object.order.OrderDTO;
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
import java.util.List;
import java.util.stream.IntStream;

public class CashOutInfo {

    @FXML
    private Label nbPayment,customer,dateSale,amount,expense,remain,ticket,paid;

    @FXML
    private JFXButton paymentBtn;

    @Setter
    private CashOutDTO cashOutDTO;

    @Setter
    private LocalDate startDate,endDate;

    @Setter @Getter
    private String nbPaymentData,customerData,dateSaleData,amountData,
            expenseData,remainData,ticketData,paidData;

    private final ControlsOption controlsOption = new ControlsOption();

    @Setter
    private String window = "paymentView" ;

    @Setter
    private PaymentDTO paymentDTO;

    @FXML
    void payment() {
        VARIABLE_STATIC.data.clear();
        String[] listData = {String.valueOf(paymentDTO.getIdCustomer()),customer.getText(),
                String.valueOf(startDate),String.valueOf(endDate),
                paid.getText(),remain.getText()};
        VARIABLE_STATIC.data.addAll(List.of(listData));
        new SwitchScene().showFloatNode(window,0);
    }

    public void setData() {

        Long orderId = paymentDTO.getIdOrder();
        Long customerId = paymentDTO.getIdCustomer();

        OrderDTO orderDTO = ApiClient.getOneEntity(
                "http://192.168.7.2:8080/order/" + orderId, OrderDTO.class);

        CustomerDTO customerDTO = ApiClient.getOneEntity(
                "http://192.168.7.2:8080/customer/" + customerId, CustomerDTO.class);

        assert orderDTO != null;
        List<PaymentDTO> payments = orderDTO.getPayments();

        // Trouver le numéro du ticket
        int index = IntStream.range(0, payments.size())
                .filter(i -> payments.get(i).getId().equals(paymentDTO.getId()))
                .findFirst()
                .orElse(0);

        // SOMME DES PAIEMENTS D'AVANT
        int total = payments.stream()
                .filter(p -> p.getId() < paymentDTO.getId())
                .mapToInt(PaymentDTO::getAmount)
                .sum();

        nbPayment.setText(String.valueOf(index + 1));

        // UI
        controlsOption.jfxButtonOption(paymentBtn, "fa-ellipsis-h", Color.DARKGREEN);
        paymentBtn.setId(String.valueOf(customerId));

        if (customerDTO != null) {
            customer.setText(customerDTO.getFirstName());
        }

        int net_to_pay = orderDTO.getTotalPrice() - total;
        int remain_to_pay = orderDTO.getTotalPrice() - (total + paymentDTO.getAmount());
        amount.setText(controlsOption.thousandSeparator(net_to_pay)+" Ar");
        remain.setText(controlsOption.thousandSeparator(
                remain_to_pay)+" Ar");
        dateSale.setText(
                LocalDate.parse(orderDTO.getCreatedAt())
                        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
        );

        // SOMME DES MONTANTS ET DES RESTES
        VARIABLE_STATIC.sum_amount += net_to_pay;
        VARIABLE_STATIC.sum_remain += Math.max(remain_to_pay, 0);

        ticket.setText(paymentDTO.getTicket());

        int paidAmount = paymentDTO.getAmount() - paymentDTO.getExpense();

        paid.setText(controlsOption.thousandSeparator(paidAmount) + " Ar");
        expense.setText(controlsOption.thousandSeparator(paymentDTO.getExpense()) + " Ar");

        setDataPrint();
        // SOMME DES DEPENSES ET DES PAIEMENTS
        VARIABLE_STATIC.sum_expense += paymentDTO.getExpense();
        VARIABLE_STATIC.sum_paid += paidAmount;
        VARIABLE_STATIC.cashOutInfoList.add(this);
    }

    private void setDataPrint(){
        setDateSaleData(dateSale.getText());
        setNbPaymentData(nbPayment.getText());
        setTicketData(ticket.getText());
        setCustomerData(customer.getText());
        setAmountData(amount.getText().split(" ")[0]);
        setPaidData(paid.getText().split(" ")[0]);
        setExpenseData(expense.getText().split(" ")[0]);
        setRemainData(remain.getText().split(" ")[0]);
    }
}