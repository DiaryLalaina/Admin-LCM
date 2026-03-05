package com.work.cashier.controller.page;

import animatefx.animation.ZoomInUp;
import com.jfoenix.controls.JFXButton;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.controller.infoTable.CashOutInfo;
import com.work.cashier.data_transfert_object.customer.CashOutDTO;
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
import java.util.List;
import java.util.ResourceBundle;

public class Unpaid implements Initializable {

    @FXML
    private VBox containerData;

    @FXML
    private Label totalAmount,paid,remain;

    @FXML
    private JFXButton printBtn;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        Platform.runLater(this::fillTable);
        new ControlsOption().jfxButtonOption(printBtn,"fa-print", Color.DARKGREEN);
    }

    @FXML
    void print(){
        new SwitchScene().showFloatNode("printingUnpaid",0);
    }

    private void fillTable(){
        long total = 0,advance = 0,remainAmount = 0;
        String url = "http://192.168.7.2:8080/customer/getAllUnpaid";
        containerData.getChildren().clear();

        List<CashOutDTO> list = ApiClient.getAll(url, CashOutDTO.class);
        double delay = 0.0;

        for(CashOutDTO dto : list){
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/cashoutInfo.fxml"));
            try {
                HBox hbox = fxmlLoader.load();
                CashOutInfo info = fxmlLoader.getController();
                info.setCashOutDTO(dto);
                info.setWindow("unpaidDetailsView");
                info.setData();
                containerData.getChildren().add(hbox);

                new NodeAnimation().animate(hbox,delay,new ZoomInUp());

                long eachRemain = dto.getTotal() - dto.getPaid();
                remainAmount += eachRemain;
                total += dto.getTotal();
                advance += dto.getPaid();

                delay += 0.1;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        ControlsOption controlsOption = new ControlsOption();
        totalAmount.setText(controlsOption.thousandSeparator(total)+" Ar");
        paid.setText(controlsOption.thousandSeparator(advance)+ " Ar");
        remain.setText(controlsOption.thousandSeparator(remainAmount)+ " Ar");
    }

}
