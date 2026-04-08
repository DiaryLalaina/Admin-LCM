package com.work.cashier.controller.login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleNode;
import com.work.cashier.alert.AlertMessage;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.Constants;
import com.work.cashier.data_transfert_object.responsible.ResponsibleDTO;
import com.work.cashier.data_transfert_object.responsible.TimeKeepingDTO;
import com.work.cashier.data_transfert_object.user.UserRoleType;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.mask.TextFieldMask;
import com.work.cashier.notifications.NotificationType;
import com.work.cashier.notifications.NotificationsBuilder;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.net.URL;
import java.util.ResourceBundle;


public class Login implements Initializable {

    @FXML
    private JFXTextField phone,visiblePassword;

    @FXML
    private JFXPasswordField password;

    @FXML
    private JFXToggleNode togglePassword;

    @FXML
    private JFXButton login_btn;

    @Getter
    private static ResponsibleDTO connected;
    private final ControlsOption controlsOption = new ControlsOption();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        TextFieldMask.onlyNumbers(phone);
        controlsOption.jfxToggleButton(togglePassword,"fa-eye",Color.BLACK);
        controlsOption.passwordOption(visiblePassword,password,togglePassword);
    }

    @FXML
    void login() throws Exception {
        connect();
    }

    @FXML
    void onKeyPressed(KeyEvent event) throws Exception {
        
        if(event.getCode() == KeyCode.ENTER) {
            if (phone.isFocused()) {
                if (password.isVisible())   password.requestFocus();
                else  visiblePassword.requestFocus();
            }else if (password.isFocused() || visiblePassword.isFocused()) {
                login_btn.requestFocus();
                login();
            }
        }
    }

    @FXML
    void togglePasswordOnAction(){
        String icon = !togglePassword.isSelected()?"fa-eye":"fa-eye-slash";
        controlsOption.jfxToggleButton(togglePassword,icon, Color.BLACK);
    }

    private void connect() {
        String phoneParam = phone.getText();
        String url = "http://192.168.7.2:8080/responsible/by-phone?phone="+phoneParam;

        ResponsibleDTO result = ApiClient.getOneEntity(url, ResponsibleDTO.class);
        if(result != null){

            PasswordEncoder encoder = new BCryptPasswordEncoder();
            String passwordFromUser = password.getText();
            String passwordFromDB = result.getPassword();

            if(result.getRole() == UserRoleType.ROLE_CASHIER
                    || result.getRole() == UserRoleType.ROLE_ADMIN) {
                if (encoder.matches(passwordFromUser, passwordFromDB)) {
                    if (result.getAuthorisation()) {
                        connected = result;
                        NotificationsBuilder.create(NotificationType.SUCCESS, "Utilisateur connecté avec succés");
                        saveTimeKeeping(result);
                        HBox container = Constants.hBox;
                        new SwitchScene().navigateWindow(container, "main/mainPageContainer.fxml");
                    } else {
                        String message = "CONTACTEZ VOTRE ADMINISTRATEUR POUR DEBLOQUER VOTRE COMPTE.";
                        AlertMessage alert = new AlertMessage(message);
                        alert.information();
                    }
                } else {
                    String message = "MOT DE PASSE INCORRECT";
                    AlertMessage alert = new AlertMessage(message);
                    alert.error();
                }
            }else {
                String message = "ACCES REFUSE";
                AlertMessage alert = new AlertMessage(message);
                alert.error();
            }
        }
    }

    private void saveTimeKeeping(ResponsibleDTO responsibleDTO){
        TimeKeepingDTO dto = new TimeKeepingDTO();
        dto.setIdResponsible(responsibleDTO.getId());
        ApiClient.insert("http://192.168.7.2:8080/timeKeeping/save",dto);
    }
}
