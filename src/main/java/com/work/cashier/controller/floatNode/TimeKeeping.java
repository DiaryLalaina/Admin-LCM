package com.work.cashier.controller.floatNode;

import animatefx.animation.FadeInLeft;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.api.ApiResult;
import com.work.cashier.controller.infoTable.PaymentEmployeeInfo;
import com.work.cashier.controller.infoTable.TimeKeepingInfo;
import com.work.cashier.controller.infoTable.UserInfo;
import com.work.cashier.data_transfert_object.employee.EmployeeSalaryDTO;
import com.work.cashier.data_transfert_object.responsible.TimeKeepingDTO;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class TimeKeeping implements Initializable {

    @FXML
    private JFXButton hideBtn;

    @FXML
    private JFXDatePicker date;

    @FXML
    private VBox containerData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        new ControlsOption().jfxButtonOption(hideBtn,"fa-times", Color.WHITE);
        date.setValue(LocalDate.now());
        fillTable();
    }

    @FXML
    void close() {
        new SwitchScene().closeFloatScene(1);
    }

    @FXML
    void date_OnAction() {
        fillTable();
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

    private void fillTable(){
        String url = "http://192.168.7.2:8080/timeKeeping/getByDate?date="+ date.getValue();
        containerData.getChildren().clear();
        List<TimeKeepingDTO> list = ApiClient.getAll(url, TimeKeepingDTO.class);

        double delay = 0.0;

        for (TimeKeepingDTO dto : list) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/timeKeepingInfo.fxml"));
            try {
                HBox hBox = fxmlLoader.load();
                TimeKeepingInfo info = fxmlLoader.getController();
                info.setData(dto);
                containerData.getChildren().add(hBox);

                new NodeAnimation().animate(hBox,delay,new FadeInLeft());

                delay += 0.1;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
