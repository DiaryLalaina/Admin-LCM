package com.work.cashier.controller.floatNode;

import animatefx.animation.Pulse;
import com.jfoenix.controls.JFXButton;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.controller.infoTable.OrderPaymentInfo;
import com.work.cashier.controller.infoTable.TicketOrderLineInfo;
import com.work.cashier.controller.infoTable.UserInfo;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.order.OrderLineDTO;
import com.work.cashier.data_transfert_object.payment.PaymentDTO;
import com.work.cashier.data_transfert_object.payment.UnpaidDTO;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.print.PrintTicket;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;

public class TicketPrint implements Initializable {

    @FXML
    private VBox containerPrint;

    @FXML
    private Label customer,totalOrderPrice,sale;

    @FXML
    private VBox containerOrders;

    @FXML
    private Label amount,expense,amountPaid,amountTicket;

    @FXML
    private Label ticket_20000,amount_20000,ticket_10000,amount_10000,ticket_5000,amount_5000;

    @FXML
    private Label ticket_2000,amount_2000,ticket_1000,amount_1000,ticket_500,amount_500;

    @FXML
    private Label ticket_200,amount_200,ticket_100,amount_100;

    @FXML
    private Label remain,datePrint;

    @FXML
    private JFXButton printBtn;

    @Setter
    private PaymentDTO paymentDTO;

    @Setter
    private OrderDTO orderDTO;

    private int sumOrders = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        Platform.runLater(this::setData);
    }

    @FXML
    void onMouseEntered(MouseEvent event) {
        ScrollPane scrollPane = (ScrollPane) event.getSource();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    }

    @FXML
    void onMouseExited(MouseEvent event) {
        ScrollPane scrollPane = (ScrollPane) event.getSource();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    @FXML
    void print(){
        new PrintTicket().printVBox(containerPrint,"XP-80C");
        new SwitchScene().closeFloatScene(2);
    }

    private void setData(){
        CustomerDTO customerDTO = UserInfo.getCustomerDTOClicked();
        customer.setText(customerDTO.getFirstName());
        //amount.setText((paymentDTO.getAmount()-paymentDTO.getExpense())+" Ar");
        String dateSale = LocalDate.parse(orderDTO.getCreatedAt()).
                format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        sale.setText(sale.getText()+ " : "+dateSale);
        amount.setText((paymentDTO.getAmount()-paymentDTO.getExpense())+" Ar");
        amountTicket.setText(paymentDTO.getAmount()+" Ar");
        expense.setText(paymentDTO.getExpense()+" Ar");
        amountPaid.setText(paymentDTO.getTicket());
        Label[] nbTicket = {ticket_20000,ticket_10000,ticket_5000,ticket_2000,ticket_1000,
                ticket_500,ticket_200,ticket_100};
        Label[] amountTicket = {amount_20000,amount_10000,amount_5000,amount_2000,amount_1000,
                amount_500,amount_200,amount_100};
        Integer[] nbTicketData   = {paymentDTO.getTwentyThousand(),paymentDTO.getTenThousand(),
                paymentDTO.getFiveThousand(),paymentDTO.getTwoThousand(),paymentDTO.getOneThousand(),
                paymentDTO.getFiveHundred(),paymentDTO.getTwoHundred(),paymentDTO.getOneHundred()};
        showListOrderLine();
        ControlsOption controlsOption = new ControlsOption();
        for(int i = 0;i <= nbTicket.length-1;i++){
            nbTicket[i].setText(nbTicketData[i]+"");
            amountTicket[i].setText(controlsOption.thousandSeparator(nbTicketData[i]*
                    Integer.parseInt(nbTicket[i].getId().split("_")[1])));
        }

        UnpaidDTO unpaidDTO = OrderPaymentInfo.getUnpaidDTO();
        Integer remainAmount = orderDTO.getTotalPrice() - (unpaidDTO.getAdvance()+paymentDTO.getAmount());
        remain.setText(remainAmount+" Ar");
        datePrint.setText("- EDITE LE "+ LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
    }

    private void showListOrderLine(){
        containerOrders.getChildren().clear();
        double delay = 0.0;
        if(!orderDTO.getOrderLines().isEmpty()) {
            for (OrderLineDTO lineDTO : orderDTO.getOrderLines()) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(Application.class.getResource("info_table/ticketOrderLineInfo.fxml"));
                try {
                    HBox hbox = fxmlLoader.load();
                    TicketOrderLineInfo info = fxmlLoader.getController();

                    String nameProduct = ApiClient.getString("http://192.168.7.2:8080/product/getName/"+lineDTO.getIdProduct());
                    lineDTO.setNameProduct(nameProduct);

                    info.setOrderLineDTO(lineDTO);
                    info.setData();
                    int subTotal = lineDTO.getQuantity() * lineDTO.getPrice();
                    sumOrders = subTotal + sumOrders;
                    containerOrders.getChildren().add(hbox);

                    new NodeAnimation().animate(hbox, delay, new Pulse());

                    delay += 0.1;
                    System.out.println(sumOrders);
                    totalOrderPrice.setText(sumOrders+" Ar");

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
