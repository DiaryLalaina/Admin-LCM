package com.work.cashier.controller.container;

import com.jfoenix.controls.JFXButton;
import com.work.cashier.constants.Constants;
import com.work.cashier.controller.login.Login;
import com.work.cashier.data_transfert_object.ResponsibleDTO;
import com.work.cashier.graphics.SwitchScene;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;

public class MainContainer implements Initializable {

    @FXML
    private Circle circle;

    @FXML
    private Label nameUser,titleNavigation;

    @FXML
    private HBox hBoxContainer;

    private JFXButton button;


    private final ResponsibleDTO responsibleDTO = Login.getConnected();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        nameUser.setText(responsibleDTO.getFirstName().toUpperCase() +" "+responsibleDTO.getLastName()+" : "+
                responsibleDTO.getRole());
        new SwitchScene().navigateWindow(hBoxContainer,"app_page/main.fxml");
    }

    @FXML
    void cashOutOnClick(ActionEvent event) {
        titleNavigation.setText("GESTION DE CAISSE");
        new SwitchScene().navigateWindow(hBoxContainer,"app_page/cashout.fxml");
        //buttonClicked(event);
    }

    @FXML
    void customerOnClick(ActionEvent event) {
        titleNavigation.setText("SUIVI DES CLIENTS");
        SwitchScene switchScene = new SwitchScene();
        switchScene.setUserType("customer");
        switchScene.navigateWindow(hBoxContainer,"app_page/customer.fxml");
        //buttonClicked(event);
    }

    @FXML
    void customerOrderOnClick(ActionEvent event) {
        titleNavigation.setText("SUIVI DES COMMANDES");
        new SwitchScene().navigateWindow(hBoxContainer,"app_page/commands.fxml");
        //buttonClicked(event);
    }

    @FXML
    void stockOnClick(ActionEvent event){
        titleNavigation.setText("SUIVI DES STOCKS");
        new SwitchScene().navigateWindow(hBoxContainer,"app_page/stock.fxml");
    }

    @FXML
    void homeOnClick(ActionEvent event) {
        titleNavigation.setText("TABLEAU DE BORD");
        new SwitchScene().navigateWindow(hBoxContainer,"app_page/main.fxml");
        //buttonClicked(event);
    }

    @FXML
    void unpaidOnClick(ActionEvent event) {
        titleNavigation.setText("LES IMPAYES");
        new SwitchScene().navigateWindow(hBoxContainer,"app_page/unpaid.fxml");
        //buttonClicked(event);
    }

    @FXML
    void employeeOnClick(ActionEvent event) {
        titleNavigation.setText("GESTION DES EMPLOYES");
        SwitchScene switchScene = new SwitchScene();
        switchScene.setUserType("employee");
        switchScene.navigateWindow(hBoxContainer,"app_page/customer.fxml");
        //buttonClicked(event);
    }

    @FXML
    void logOut(ActionEvent event) {
        new SwitchScene().navigateWindow(Constants.hBox,"login_page/login.fxml");
        //buttonClicked(event);
    }

    private void buttonClicked(ActionEvent event){
        if(button != null) {
            button.setMouseTransparent(false);
            button = (JFXButton) event.getSource();
            button.setMouseTransparent(true);
        }
    }

}
