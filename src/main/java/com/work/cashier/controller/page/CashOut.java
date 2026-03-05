package com.work.cashier.controller.page;

import animatefx.animation.SlideInRight;
import animatefx.animation.ZoomInUp;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.controller.infoTable.CashOutInfo;
import com.work.cashier.data_transfert_object.customer.CashOutDTO;
import com.work.cashier.data_transfert_object.payment.PaymentDTO;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class CashOut implements Initializable {

    @FXML
    private JFXDatePicker startDate,endDate;

    @FXML
    private VBox containerCashOut,containerTicketing;

    @FXML
    private TextField mga_20000,mga_10000,mga_5000,mga_2000,mga_1000,mga_500,mga_200,mga_100;

    @FXML
    private Label total_20000_mga,total_10000_mga,total_5000_mga,total_2000_mga,total_1000_mga,
            total_500_mga,total_200_mga,total_100_mga;

    @FXML
    private Label ticketingAmount,paid,remain;

    @FXML
    private JFXButton jfxButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        new ControlsOption().jfxButtonOption(jfxButton,"fa-list-ul", Color.WHITE);
        startDate.setValue(LocalDate.now());
        endDate.setValue(LocalDate.now());
        Platform.runLater(this::showCashOut);
    }

    @FXML
    void onKeyPressed(KeyEvent event) {

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
    void showCashOut() {
        if(startDate.getValue().isBefore(endDate.getValue()) ||
                startDate.getValue().isEqual(endDate.getValue())) {
            setDataTicket();
            animateTicketing();
            fillTable();
        }
    }

    private void fillTable(){
        long totalAmount=0,paidAmount=0,remainAmount = 0;
        String url = "http://192.168.7.2:8080/customer/cashOut?startDate="+ startDate.getValue()+"&endDate="+endDate.getValue();
        containerCashOut.getChildren().clear();
        List<CashOutDTO> list = ApiClient.getAll(url, CashOutDTO.class);

        double delay = 0.0;

        for(CashOutDTO dto : list){
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/cashoutInfo.fxml"));
            try {
                HBox hbox = fxmlLoader.load();
                CashOutInfo info = fxmlLoader.getController();
                info.setCashOutDTO(dto);
                info.setStartDate(startDate.getValue());
                info.setEndDate(endDate.getValue());
                info.setWindow("paymentView");
                info.setData();
                containerCashOut.getChildren().add(hbox);

                new NodeAnimation().animate(hbox,delay,new ZoomInUp());

                long eachRemain = dto.getTotal() - dto.getPaid();
                totalAmount = dto.getTotal()+totalAmount;
                paidAmount = dto.getPaid()+paidAmount;
                remainAmount = eachRemain + remainAmount;

                delay += 0.1;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        ControlsOption controlsOption = new ControlsOption();
        ticketingAmount.setText(controlsOption.thousandSeparator(totalAmount));
        paid.setText(controlsOption.thousandSeparator(paidAmount));
        remain.setText(controlsOption.thousandSeparator(remainAmount));
    }

    private void animateTicketing(){
        List<Node> nodes = containerTicketing.getChildren().stream().toList();
        double delay = 0.0;
        for(Node node : nodes){
            new NodeAnimation().animate(node,delay,new SlideInRight());
            delay += 0.1;
        }
    }
    private void setDataTicket(){
        ControlsOption controlsOption = new ControlsOption();
        PaymentDTO dto = ApiClient.getOneEntity("http://192.168.7.2:8080/payment/billSummaryBetweenDates?"+
                "startDate="+startDate.getValue()+"&endDate="+endDate.getValue(), PaymentDTO.class);
        assert dto != null;
        int[] data = {dto.getTwentyThousand(),dto.getTenThousand(),dto.getFiveThousand(),dto.getTwoThousand(),
                dto.getOneThousand(),dto.getFiveHundred(),dto.getTwoHundred(),dto.getOneHundred()};
        Label[] labels = {total_20000_mga,total_10000_mga,total_5000_mga,total_2000_mga,total_1000_mga,
                total_500_mga,total_200_mga,total_100_mga};
        TextField[] textFields = {mga_20000,mga_10000,mga_5000,mga_2000,mga_1000,mga_500,mga_200,mga_100};
        for(int i = 0 ; i < data.length ; i++){
            textFields[i].setText(String.valueOf(data[i]));
            int countTicket = Integer.parseInt(textFields[i].getText());
            int ticket = Integer.parseInt(labels[i].getId().split("_")[1]);
            labels[i].setText(" = "+controlsOption.thousandSeparator(countTicket * ticket)+" Ar");
        }
    }
}
