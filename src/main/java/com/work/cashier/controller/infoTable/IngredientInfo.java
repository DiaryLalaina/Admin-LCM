package com.work.cashier.controller.infoTable;

import com.jfoenix.controls.JFXButton;
import com.work.cashier.data_transfert_object.ingredient.IngredientDTO;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

public class IngredientInfo implements Initializable {

    @FXML
    private Label provider,ingredient,storage;

    @FXML
    private JFXButton jfxButton;

    @Setter
    @Getter
    private IngredientDTO ingredientDTO;

    @Getter
    private static IngredientDTO ingredientUpdateDTO;

    private final ControlsOption controlsOption = new ControlsOption();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controlsOption.jfxButtonOption(jfxButton,"fa-ellipsis-v", Color.BLUEVIOLET);
    }

    @FXML
    void menu() {
    }
    public void setData(){
        provider.setId(String.valueOf(ingredientDTO.getId()));
        provider.setText(ingredientDTO.getProvider());
        ingredient.setText(ingredientDTO.getName());
        storage.setText(String.format("%.2f",ingredientDTO.getStockQuantity())+" "+ingredientDTO.getUnit());
    }

}
