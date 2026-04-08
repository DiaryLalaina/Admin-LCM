package com.work.cashier.controller.floatNode;

import animatefx.animation.SlideInDown;
import com.jfoenix.controls.JFXButton;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.VARIABLE_STATIC;
import com.work.cashier.controller.infoTable.CashOutTicketingInfo;
import com.work.cashier.data_transfert_object.payment.PaymentDTO;
import com.work.cashier.graphics.SwitchScene;
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
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.ResourceBundle;

public class PaymentDetailsView implements Initializable {

    @FXML
    private Label customerData,totalAmount,paid,remain;

    @FXML
    private JFXButton hideBtn;

    @FXML
    private VBox containerPayment;

    private final ControlsOption controlsOption = new ControlsOption();

    private final List<String> data = VARIABLE_STATIC.data;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controlsOption.jfxButtonOption(hideBtn, "fa-times", Color.WHITE);
        String start = LocalDate.parse(data.get(2)).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        String end = LocalDate.parse(data.get(3)).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        customerData.setText(data.get(1)+" : Du "+start+" à "+end);
        int range = 4;
        for (Label label : new Label[]{paid,remain}){
            label.setText(data.get(range));
            range++;
        }
        Platform.runLater(this::showPaymentTicketing);
    }

    @FXML
    void close() {
        int layer = data.get(1).contains("FICHE CLIENT")?2:1;
        new SwitchScene().closeFloatScene(layer);
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

    private void showPaymentTicketing() {
        String url = "http://192.168.7.2:8080/payment/getCashOutDetails?customerId="+data.getFirst()+
                "&startDate="+data.get(2)+"&endDate="+data.get(3);
        if(data.get(1).contains("FICHE CLIENT")) {
            url = "http://192.168.7.2:8080/payment/getList?idOrder="+data.get(1).split(" / ")[1];
        }
        containerPayment.getChildren().clear();
        List<PaymentDTO> list = ApiClient.getAll(url, PaymentDTO.class);

        double delay = 0.0;

        for (PaymentDTO dto : list) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/paymentTicketingInfo.fxml"));
            try {
                HBox hBox = fxmlLoader.load();
                CashOutTicketingInfo info = fxmlLoader.getController();
                info.setDto(dto);
                info.setData();
                containerPayment.getChildren().add(hBox);

                new NodeAnimation().animate(hBox,delay,new SlideInDown());

                delay += 0.1;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
