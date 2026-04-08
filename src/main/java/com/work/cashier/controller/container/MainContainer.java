package com.work.cashier.controller.container;

import com.jfoenix.controls.JFXButton;
import com.work.cashier.constants.Constants;
import com.work.cashier.controller.login.Login;
import com.work.cashier.data_transfert_object.responsible.ResponsibleDTO;
import com.work.cashier.data_transfert_object.user.UserRoleType;
import com.work.cashier.graphics.SwitchScene;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;

public class MainContainer implements Initializable {

    @FXML
    private Circle circle;

    @FXML
    private Label nameUser,titleNavigation,date;

    @FXML
    private HBox hBoxContainer,contentResponsible;

    private JFXButton button;

    private final ResponsibleDTO responsibleDTO = Login.getConnected();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        date.setText(LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)));
        contentResponsible.setVisible(responsibleDTO.getRole()== UserRoleType.ROLE_ADMIN);
        nameUser.setText(responsibleDTO.getFirstName().toUpperCase() +" "+responsibleDTO.getLastName()+" : "+
                responsibleDTO.getRole());
        new SwitchScene().navigateWindow(hBoxContainer,"app_page/main.fxml");
    }

    @FXML
    void cashOutOnClick() {
        titleNavigation.setText("GESTION DE CAISSE");
        new SwitchScene().navigateWindow(hBoxContainer,"app_page/cashout.fxml");
        //buttonClicked(event);
    }

    @FXML
    void followSaleOnClick() {
        titleNavigation.setText("SUIVI DES VENTES");
        new SwitchScene().navigateWindow(hBoxContainer,"app_page/followSale.fxml");
        //buttonClicked(event);
    }

    @FXML
    void customerOnClick() {
        titleNavigation.setText("SUIVI DES CLIENTS");
        SwitchScene switchScene = new SwitchScene();
        switchScene.setUserType("customer");
        switchScene.navigateWindow(hBoxContainer,"app_page/customer.fxml");
        //buttonClicked(event);0
    }

    @FXML
    void customerOrderOnClick() {
        titleNavigation.setText("SUIVI DES COMMANDES");
        new SwitchScene().navigateWindow(hBoxContainer,"app_page/commands.fxml");
        //buttonClicked(event);
    }

    @FXML
    void stockOnClick(){
        titleNavigation.setText("SUIVI DES STOCKS");
        new SwitchScene().navigateWindow(hBoxContainer,"app_page/stock.fxml");
    }

    @FXML
    void homeOnClick() {
        titleNavigation.setText("TABLEAU DE BORD");
        new SwitchScene().navigateWindow(hBoxContainer,"app_page/main.fxml");
        //buttonClicked(event);
    }

    @FXML
    void unpaidOnClick() {
        titleNavigation.setText("LES IMPAYES");
        //new SwitchScene().navigateWindow(hBoxContainer,"app_page/followSale.fxml");
        new SwitchScene().navigateWindow(hBoxContainer,"app_page/dailyUnpaid.fxml");
        //buttonClicked(event);
    }

    @FXML
    void employeeOnClick() {
        titleNavigation.setText("GESTION DES EMPLOYES");
        SwitchScene switchScene = new SwitchScene();
        switchScene.setUserType("employee");
        switchScene.navigateWindow(hBoxContainer,"app_page/customer.fxml");
        //buttonClicked(event);
    }

    @FXML
    void responsibleOnClick(ActionEvent event) {
        titleNavigation.setText("GESTION DES UTILISATEURS");
        SwitchScene switchScene = new SwitchScene();
        switchScene.navigateWindow(hBoxContainer,"app_page/responsible.fxml");
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
