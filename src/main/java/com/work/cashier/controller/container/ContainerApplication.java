package com.work.cashier.controller.container;

import com.work.cashier.constants.Constants;
import com.work.cashier.graphics.SwitchScene;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ContainerApplication implements Initializable {
    @FXML
    private StackPane stackPane;

    @FXML
    private HBox hBox;

    public void initialize(URL url, ResourceBundle resourceBundle){
        Constants.stackPane = stackPane;
        Constants.hBox = hBox;
        new SwitchScene().navigateWindow(hBox,"login_page/login.fxml");
    }

}