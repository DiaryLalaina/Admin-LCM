package com.work.cashier.service;

import animatefx.animation.SlideInRight;
import animatefx.animation.SlideOutRight;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleNode;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class ControlsOption {

    public void jfxButtonOption(JFXButton button,String code,Color color){
        FontIcon icon = new FontIcon(code);
        icon.setIconColor(color);
        icon.setIconSize(20);
        button.setGraphic(icon);
    }

    public void jfxToggleButton(JFXToggleNode button, String code, Color color){
        FontIcon icon = new FontIcon(code);
        icon.setIconColor(color);
        icon.setIconSize(15);
        button.setGraphic(icon);
    }

    public void hideButtonsAndShowMain(Node containerButtons, Node menu) {
        SlideOutRight slideOutRight = new SlideOutRight(containerButtons);
        slideOutRight.setOnFinished(_ -> {
            containerButtonsIsVisible(containerButtons,false);
            new SlideInRight(menu).play();
            containerButtonsIsVisible(menu,true);

        });
        slideOutRight.play();
    }

    public void hideMenuAndShowButtons(Node containerButtons, Node menu) {
        SlideOutRight slideOutRight = new SlideOutRight(menu);
        slideOutRight.setOnFinished(_ -> {
            containerButtonsIsVisible(menu,false);
            new SlideInRight(containerButtons).play();
            containerButtonsIsVisible(containerButtons,true);

        });
        slideOutRight.play();
    }

    private void containerButtonsIsVisible(Node containerButtons, boolean status){
        containerButtons.setVisible(status);
        containerButtons.setManaged(status);
    }

    public void passwordOption(JFXTextField visibleField, JFXPasswordField passwordField, JFXToggleNode toggleVisibility) {
        visibleField.setVisible(false);
        visibleField.textProperty().bindBidirectional(passwordField.textProperty());
        toggleVisibility.selectedProperty().addListener((obs, oldVal, show) -> {
            visibleField.setVisible(show);
            passwordField.setVisible(!show);
        });
    }

    public String thousandSeparator(double number){
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');

        DecimalFormat format = new DecimalFormat("#,###", symbols);
        format.setGroupingSize(3);
        format.setGroupingUsed(true);
        return format.format(number);
    }

}
