package com.work.cashier.mask;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class RequiredFieldsValidators {

    private static final FontIcon WARNING_ICON = new FontIcon("fa-warning");

    private static final String MESSAGE = " obligatoire";

    public static void toJFXTextField(JFXTextField txt) {
        RequiredFieldValidator validator = new RequiredFieldValidator();

        WARNING_ICON.setIconColor(Color.TOMATO);
        validator.setIcon(WARNING_ICON);


        txt.getValidators().add(validator);
        txt.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                txt.validate();
            }
        });
    }

    public static void toJFXPasswordField(JFXPasswordField txt) {
        RequiredFieldValidator validator = new RequiredFieldValidator(txt.getPromptText()+MESSAGE);
        validator.setIcon(WARNING_ICON);
        WARNING_ICON.setIconColor(Color.TOMATO);

        txt.getValidators().add(validator);
        txt.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                txt.validate();
            }
        });
    }

    public static void toJFXTextArea(JFXTextArea txt) {
        RequiredFieldValidator validator = new RequiredFieldValidator(txt.getPromptText()+MESSAGE);
        validator.setIcon(WARNING_ICON);
        WARNING_ICON.setIconColor(Color.TOMATO);

        txt.getValidators().add(validator);
        txt.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                txt.validate();
            }
        });
    }

    public static void toJFXComboBox(JFXComboBox<String> comboBox) {
        RequiredFieldValidator validator = new RequiredFieldValidator(comboBox.getPromptText()+MESSAGE);
        //validator.setIcon(WARNING_ICON>);
        //WARNING_ICON.setIconColor(Color.TOMATO);
        
        comboBox.getValidators().add(validator);
        comboBox.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                comboBox.validate();
            }
        });
    }

    public static void toJFXDatePicker(JFXDatePicker datePicker) {
        RequiredFieldValidator validator = new RequiredFieldValidator(datePicker.getPromptText()+MESSAGE);
        validator.setIcon(WARNING_ICON);
        WARNING_ICON.setIconColor(Color.TOMATO);
        
        datePicker.getValidators().add(validator);
        datePicker.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                datePicker.validate();
            }
        });
    }

    public static void setPickerValue(JFXDatePicker jfxDatePicker){
        String date = String.valueOf(jfxDatePicker.getValue());
        if(!date.equals("null")) {
            jfxDatePicker.setId(date);
            jfxDatePicker.getEditor().clear();
            jfxDatePicker.setPromptText(LocalDate.parse(date).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
        }
    }

    public static JFXButton setIcon(String iconCode, Color color){
        FontIcon icon = new FontIcon(iconCode);
        icon.setIconColor(color);
        icon.setIconSize(20);
        return new JFXButton("",icon);
    }
}
