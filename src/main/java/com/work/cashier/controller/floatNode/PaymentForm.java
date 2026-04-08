package com.work.cashier.controller.floatNode;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.work.cashier.Application;
import com.work.cashier.alert.AlertMessage;
import com.work.cashier.api.ApiClient;
import com.work.cashier.controller.infoTable.UserInfo;
import com.work.cashier.controller.infoTable.OrderPaymentInfo;
import com.work.cashier.controller.listener.PaymentListener;
import com.work.cashier.controller.login.Login;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.payment.PaymentDTO;
import com.work.cashier.data_transfert_object.payment.UnpaidDTO;
import com.work.cashier.data_transfert_object.payment.ValidationDTO;
import com.work.cashier.data_transfert_object.user.UserRoleType;
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
import java.util.ResourceBundle;

public class PaymentForm implements Initializable, PaymentListener {

    @FXML
    private JFXButton hideBtn, manageBtn;

    @FXML
    private VBox containerOrders, containerPayments;

    @FXML
    private Label customerLbl, ticketingAmount,referenceOrder;

    @FXML
    private TextField amount,expenseAmount,reasonExpense;

    @FXML
    private TextField mga_20000, mga_10000, mga_5000, mga_2000, mga_1000, mga_500, mga_200, mga_100;

    @FXML
    private Label total_20000_mga, total_10000_mga, total_5000_mga, total_2000_mga, total_1000_mga,
            total_500_mga, total_200_mga, total_100_mga;

    @FXML
    private JFXComboBox<String> typePayment;

    private final ControlsOption controlsOption = new ControlsOption();

    private final CustomerDTO customer = UserInfo.getCustomerDTOClicked();

    private int sum = 0;

    @Setter @Getter
    private static long idOrder;

    @Getter @Setter
    private static VBox containerPayment;

    @Override
    public void setReferenceOrder(String reference,String date){
        amount.setText("0");
        expenseAmount.setText("0");
        ticketingAmount.setText("0");
        refresh();
        sum = 0;
        Platform.runLater(() -> referenceOrder.setText("VENTE : "+date));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typePayment.getItems().addAll("SIMPLE","SANS TICKET");
        typePayment.setValue("SIMPLE");
        typePayment.setMouseTransparent(Login.getConnected().getRole() == UserRoleType.ROLE_CASHIER);
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

        int amountValue = getValueInForm(amount.getText());
        int expenseValue = getValueInForm(expenseAmount.getText());

        if (amountValue == 0) {
            return;
        }

        if (!typePayment.getValue().equals("SIMPLE")) {
            OrderDTO orderDTO = ApiClient.getOneEntity("http://192.168.7.2:8080/order/"+idOrder, OrderDTO.class);
            assert orderDTO != null;
            if(orderDTO.getTotalPrice() < Integer.parseInt(amount.getText())){
                new AlertMessage("Le montant dépasse largement le reste à payer !!!").information();
                return;
            }
            if(new AlertMessage("Êtes-vous sûr de payer sans ticket").confirmMessage()) {
                savePaymentWithoutTicket(orderDTO);
            }
            return;
        }

        UnpaidDTO unpaidDTO = OrderPaymentInfo.getUnpaidDTO();
        int net_to_pay = unpaidDTO.getTotal() - unpaidDTO.getAdvance();
        int gap = amountValue - net_to_pay;

        if (amountValue != (sum + expenseValue)) {
            new Alert(Alert.AlertType.ERROR,
                    "Le payement et les billetages ne sont pas conformes.",
                    ButtonType.OK).show();
            return;
        }

        if (gap >= 100 ){
            new Alert(Alert.AlertType.ERROR,
                    "Paiement supérieur au montant",
                    ButtonType.OK).show();
            return;
        }

        /*if (Login.getConnected().getRole() == UserRoleType.ROLE_CASHIER && expenseValue >= 50000) {
            //sendValidationRequest(expenseValue);

            new AlertMessage("""
                    DEPENSE +50.000 AR. DEMANDE ENVOYER A L'ADMINISTRATEUR.
                    VALIDATION EN ATTENTE, VEILLEZ PATIENTER ...""");


            return;
        }*/

        if(Login.getConnected().getRole() == UserRoleType.ROLE_CASHIER
                && expenseValue >= 50000 ){
            if(reasonExpense.getText().isEmpty() || reasonExpense.getText().isBlank()){
                new AlertMessage("""
                    DEPENSE +50.000 AR. MOTIF OBLIGATOIRE !!!""").information();
                return;
            }
            savePayment();
            return;
        }
        savePayment();
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

    private void savePayment(){
        String url = "http://192.168.7.2:8080/payment/save";
        PaymentDTO paymentDTO = paymentDTO();
        ApiClient.insert(url, paymentDTO);
        OrderPaymentInfo.showPaymentByOrder(idOrder);
        refresh();
        sum = 0;
        amount.setText("0");
        ticketingAmount.setText("0");
        expenseAmount.setText("0");
        showOrderUnpaid();
        UnpaidDTO unpaidDTO = OrderPaymentInfo.getUnpaidDTO();
        Integer advanceUnpaid = unpaidDTO.getAdvance();
        unpaidDTO.setAdvance(advanceUnpaid + (sum + Integer.parseInt(expenseAmount.getText())));
        reasonExpense.clear();
    }

    private void savePaymentWithoutTicket(OrderDTO orderDTO){
        String url = "http://192.168.7.2:8080/order/paidWithoutTicket/"+idOrder;

        orderDTO.setTotalPrice(orderDTO.getTotalPrice() - Integer.parseInt(amount.getText()));
        ApiClient.update(url, orderDTO);
        OrderPaymentInfo.showPaymentByOrder(idOrder);
        refresh();
        sum = 0;
        amount.setText("0");
        ticketingAmount.setText("0");
        expenseAmount.setText("0");
        showOrderUnpaid();
        UnpaidDTO unpaidDTO = OrderPaymentInfo.getUnpaidDTO();
        Integer advanceUnpaid = unpaidDTO.getAdvance();
        OrderPaymentInfo.getUnpaidDTO().setAdvance(advanceUnpaid + (sum + Integer.parseInt(expenseAmount.getText())));
        reasonExpense.clear();
    }

    private PaymentDTO paymentDTO(){
        UnpaidDTO unpaidDTO = OrderPaymentInfo.getUnpaidDTO();
        int net_to_pay = unpaidDTO.getTotal() - unpaidDTO.getAdvance();
        int amountPayed = Integer.parseInt(amount.getText());
        int gap = net_to_pay - amountPayed;
        int expense = getValueInForm(expenseAmount.getText());

        if(gap > 0 && gap < 100){
            amountPayed += gap;
            expense += gap;
        }

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setAmount(amountPayed);
        paymentDTO.setExpense(expense);
        paymentDTO.setIdCustomer(UserInfo.getCustomerDTOClicked().getId());
        paymentDTO.setIdOrder(idOrder);
        paymentDTO.setTwentyThousand(getValueInForm(mga_20000.getText()));
        paymentDTO.setTenThousand(getValueInForm(mga_10000.getText()));
        paymentDTO.setFiveThousand(getValueInForm(mga_5000.getText()));
        paymentDTO.setTwoThousand(getValueInForm(mga_2000.getText()));
        paymentDTO.setOneThousand(getValueInForm(mga_1000.getText()));
        paymentDTO.setFiveHundred(getValueInForm(mga_500.getText()));
        paymentDTO.setTwoHundred(getValueInForm(mga_200.getText()));
        paymentDTO.setOneHundred(getValueInForm(mga_100.getText()));
        paymentDTO.setReasonExpense(reasonExpense.getText());
        return paymentDTO;
    }

    private int getValueInForm(String value){
        return (value == null || value.isBlank()) ?
                0 : Integer.parseInt(value);
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

        sum = 0;

        for (int i = 0; i < textFields.length; i++) {

            int qty = textFields[i].getText().isBlank()
                    ? 0 : Integer.parseInt(textFields[i].getText());

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
                if(dto.getTotal() > dto.getAdvance()) {
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

    private void sendValidationRequest(int expense){
        String url = "http://192.168.7.2:8080/validation/request";

        ValidationDTO validationDTO  = new ValidationDTO();

        validationDTO.setCashier(Login.getConnected().getFirstName());
        validationDTO.setReason(reasonExpense.getText());
        validationDTO.setExpense(new ControlsOption().thousandSeparator(expense)+" Ar");

        ApiClient.insert(url,validationDTO);
    }
}