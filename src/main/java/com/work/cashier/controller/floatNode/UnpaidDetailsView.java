package com.work.cashier.controller.floatNode;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXButton;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.VARIABLE_STATIC;
import com.work.cashier.controller.infoTable.UnpaidDetailsInfo;
import com.work.cashier.data_transfert_object.payment.UnpaidDTO;
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

public class UnpaidDetailsView implements Initializable {

    @FXML
    private JFXButton hideBtn;

    @FXML
    private VBox containerData;

    @FXML
    private Label title;

    private final ControlsOption controlsOption = new ControlsOption();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controlsOption.jfxButtonOption(hideBtn, "fa-times", Color.WHITE);
        title.setText(VARIABLE_STATIC.data.get(1));
        Platform.runLater(this::fillTable);
    }

    @FXML
    void close() {
        new SwitchScene().closeFloatScene(1);
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

    public void fillTable() {
        String url = "http://192.168.7.2:8080/order/getUnpaid?idCustomer="+ VARIABLE_STATIC.data.getFirst();
        List<UnpaidDTO> list = ApiClient.getAll(url, UnpaidDTO.class);
        containerData.getChildren().clear();
        double delay = 0.0;
        for (UnpaidDTO dto : list) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/unpaidDetailsInfo.fxml"));
            try {
                HBox hbox = fxmlLoader.load();
                UnpaidDetailsInfo info = fxmlLoader.getController();
                info.setUnpaidDTO(dto);
                info.setData();
                containerData.getChildren().add(hbox);

                new NodeAnimation().animate(hbox, delay, new FadeIn());

                delay += 0.1;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
