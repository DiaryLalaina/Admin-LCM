package com.work.cashier.alert;

import animatefx.animation.SlideInDown;
import com.work.cashier.service.NodeAnimation;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class AlertMessage {
    private final String message;
    public AlertMessage(String message){
        this.message=message;
    }
    public void information(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION,message, ButtonType.OK);
        alert.setHeaderText("INFORMATION");
        alert.setTitle("LA CASA MOFO");
        alert.show();
        new NodeAnimation().animate(alert.getGraphic(), 0.5,new SlideInDown());
    }
    public void error(){
        Alert alert = new Alert(Alert.AlertType.ERROR,message, ButtonType.OK);
        alert.setHeaderText("ERREUR");
        alert.setTitle("LA CASA MOFO");
        alert.show();
        new NodeAnimation().animate(alert.getGraphic(), 0.5,new SlideInDown());
    }
    public boolean confirmMessage(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("LA CASA MOFO");
        alert.setHeaderText("CONFIRMATION");
        alert.setContentText(message);
        Optional<ButtonType> result= alert.showAndWait();
        return (result.get() ==  ButtonType.OK);
    }
}
