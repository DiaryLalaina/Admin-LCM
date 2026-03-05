package com.work.cashier.notifications;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class NotificationsBuilder {

    private static Image icon;

    private static String title;

    public static void create(NotificationType type, String message) {
        setFunction(type);
        Notifications.create()
                .title(title)
                .text(message)
                .graphic(new ImageView(icon))
                .hideAfter(Duration.seconds(6))
                .position(Pos.BASELINE_RIGHT)
                .styleClass("-fx-background-color = WHITE")
                .show();
    }

    private static void setFunction(NotificationType type) {
        switch (type) {
            case INFORMATION:
                title = "¡ INFORMATION !";
                icon = new Image("info.png");
            break;
            
            case ERROR:
                title = "¡ ERREUR !";
                //icon = new Image(Constants.ERROR_IMAGE);
                icon = new Image("error.png");
            break;
            
            case SUCCESS:
                title = "¡ SUCCES !";
                //icon = new Image(Constants.SUCCESS_IMAGE);
                icon = new Image("success.png");
            break;
            
            case INVALID_ACTION:
                title = "¡ ACTION INVALIDE !";
                icon = new Image("error.png");
            break;
        }
    }
}
