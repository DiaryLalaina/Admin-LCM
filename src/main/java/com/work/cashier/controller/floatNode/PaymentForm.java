package com.work.cashier.controller.floatNode;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXButton;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.controller.infoTable.UserInfo;
import com.work.cashier.controller.infoTable.OrderPaymentInfo;
import com.work.cashier.controller.listener.PaymentListener;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
import com.work.cashier.data_transfert_object.payment.PaymentDTO;
import com.work.cashier.data_transfert_object.payment.UnpaidDTO;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.mask.TextFieldMask;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class PaymentForm implements Initializable, PaymentListener {

    @FXML
    private JFXButton hideBtn, manageBtn;

    @FXML
    private VBox containerOrders, containerPayments;

    @FXML
    private Label customerLbl, ticketingAmount,referenceOrder;

    @FXML
    private TextField amount,expenseAmount;

    @FXML
    private TextField mga_20000, mga_10000, mga_5000, mga_2000, mga_1000, mga_500, mga_200, mga_100;

    @FXML
    private Label total_20000_mga, total_10000_mga, total_5000_mga, total_2000_mga, total_1000_mga,
            total_500_mga, total_200_mga, total_100_mga;

    private final ControlsOption controlsOption = new ControlsOption();

    private final CustomerDTO customer = UserInfo.getCustomerDTOClicked();

    private int sum = 0;

    @Setter @Getter
    private static long idOrder;

    @Getter @Setter
    private static VBox containerPayment;

    @Override
    public void setReferenceOrder(String reference){
        amount.setText("0");
        expenseAmount.setText("0");
        ticketingAmount.setText("0");
        refresh();
        sum = 0;
        Platform.runLater(() -> referenceOrder.setText("REFERENCE : "+reference));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TextField[] textFields = new TextField[]
                {mga_20000, mga_10000, mga_5000, mga_2000, mga_1000, mga_500, mga_200, mga_100};
        for(TextField textField : textFields){
            TextFieldMask.onlyNumbers(textField);
        }
        setContainerPayment(containerPayments);
        controlsOption.jfxButtonOption(manageBtn, "fa-check", Color.GREEN);
        controlsOption.jfxButtonOption(hideBtn, "fa-times", Color.WHITE);
        customerLbl.setText(customer.getLastName().toUpperCase()+ " " +customer.getFirstName()
                +" : "+customer.getPhoneNumber());
        Platform.runLater(this::showOrderUnpaid);
    }

    @FXML
    void close() {
        new SwitchScene().closeFloatScene(1);
    }

    @FXML
    void onKeyPressed(KeyEvent event) {

        KeyCode code = event.getCode();

        if(code != KeyCode.ENTER){
            return;
        }

        TextField[] textFields = {
                mga_20000, mga_10000, mga_5000, mga_2000,
                mga_1000, mga_500, mga_200, mga_100
        };

        TextField current = (TextField) event.getSource();

        for (int i = 0; i < textFields.length; i++) {

            if (current == textFields[i]) {

                calculateEachTicket();

                if (i < textFields.length - 1) {
                    textFields[i + 1].requestFocus();
                }

                break;
            }
        }
    }

    @FXML
    void save() {
        if(Integer.parseInt(amount.getText()) == (sum + Integer.parseInt(expenseAmount.getText()))){
            String url = "http://192.168.7.2:8080/payment/save";
            PaymentDTO paymentDTO = paymentDTO();
            ApiClient.insert(url,paymentDTO);
            OrderPaymentInfo.showPaymentByOrder(idOrder);
            refresh();
            sum = 0;amount.setText("0");ticketingAmount.setText("0");expenseAmount.setText("0");
            showOrderUnpaid();
            UnpaidDTO unpaidDTO = OrderPaymentInfo.getUnpaidDTO();
            Integer advanceUnpaid = unpaidDTO.getAdvance();
            OrderPaymentInfo.getUnpaidDTO().setAdvance(advanceUnpaid+(sum + Integer.parseInt(expenseAmount.getText())));
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Le payement et les billetages ne sont pas conformes.", ButtonType.OK);
            alert.show();
        }
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

    private PaymentDTO paymentDTO(){
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setAmount(Integer.valueOf(amount.getText()));
        paymentDTO.setExpense(Integer.parseInt(expenseAmount.getText()));
        paymentDTO.setIdCustomer(UserInfo.getCustomerDTOClicked().getId());
        paymentDTO.setIdOrder(idOrder);
        paymentDTO.setTwentyThousand(Integer.valueOf(mga_20000.getText()));
        paymentDTO.setTenThousand(Integer.parseInt(mga_10000.getText()));
        paymentDTO.setFiveThousand(Integer.parseInt(mga_5000.getText()));
        paymentDTO.setTwoThousand(Integer.parseInt(mga_2000.getText()));
        paymentDTO.setOneThousand(Integer.parseInt(mga_1000.getText()));
        paymentDTO.setFiveHundred(Integer.parseInt(mga_500.getText()));
        paymentDTO.setTwoHundred(Integer.parseInt(mga_200.getText()));
        paymentDTO.setOneHundred(Integer.parseInt(mga_100.getText()));
        return paymentDTO;
    }

    private void calculateEachTicket() {

        TextField[] textFields = {
                mga_20000, mga_10000, mga_5000, mga_2000,
                mga_1000, mga_500, mga_200, mga_100
        };

        Label[] labels = {
                total_20000_mga, total_10000_mga, total_5000_mga, total_2000_mga,
                total_1000_mga, total_500_mga, total_200_mga, total_100_mga
        };

        sum = 0; // ✅ RESET TOTAL À CHAQUE CALCUL

        for (int i = 0; i < textFields.length; i++) {

            int qty = textFields[i].getText().isBlank()
                    ? 0
                    : Integer.parseInt(textFields[i].getText());

            int ticketValue = Integer.parseInt(textFields[i].getId().split("_")[1]);
            int total = qty * ticketValue;

            labels[i].setText("= " + total + " Ar");
            sum += total;
        }

        ticketingAmount.setText(String.valueOf(sum));
    }

    public void showOrderUnpaid() {
        String url = "http://192.168.7.2:8080/order/getUnpaid?idCustomer="+customer.getId();
        List<UnpaidDTO> list = ApiClient.getAll(url, UnpaidDTO.class);
        containerOrders.getChildren().clear();
        double delay = 0.0;
        for (UnpaidDTO dto : list) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/unpaidInfo.fxml"));
            try {
                HBox hbox = fxmlLoader.load();
                OrderPaymentInfo info = fxmlLoader.getController();
                info.setPaymentListener(this);
                info.setDto(dto);
                info.setData();
                if(!Objects.equals(dto.getAdvance(), dto.getTotal())) {
                    containerOrders.getChildren().add(hbox);
                }

                new NodeAnimation().animate(hbox, delay, new FadeIn());

                delay += 0.1;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void refresh(){
        Label [] labels = {total_20000_mga, total_10000_mga, total_5000_mga, total_2000_mga, total_1000_mga,
                total_500_mga, total_200_mga, total_100_mga};
        for(Label label : labels){
            label.setText("= 0 Ar");
        }
        TextField [] textFields = {mga_20000, mga_10000, mga_5000, mga_2000, mga_1000, mga_500, mga_200, mga_100};
        for(TextField textField : textFields){
            textField.setText("0");
        }
    }
}