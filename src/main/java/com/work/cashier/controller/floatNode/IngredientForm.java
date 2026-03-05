package com.work.cashier.controller.floatNode;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.ActionDatabase;
import com.work.cashier.constants.Constants;
import com.work.cashier.controller.infoTable.IngredientInfo;
import com.work.cashier.controller.page.Stock;
import com.work.cashier.data_transfert_object.ingredient.IngredientDTO;
import com.work.cashier.data_transfert_object.unitOption.Unit;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class IngredientForm implements Initializable {

    @FXML
    private JFXTextField provider,ingredient;

    @FXML
    private JFXComboBox<Unit> unitChoice;

    @FXML
    private JFXButton button, hideBtn;

    private final ControlsOption controlsOption = new ControlsOption();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controlsOption.jfxButtonOption(button,"fa-check", Color.GREEN);
        controlsOption.jfxButtonOption(hideBtn,"fa-window-close",Color.RED);
        unitChoice.getItems().addAll(Unit.values());
        if(Constants.action == ActionDatabase.UPDATE){
            setDataUpdate();
        }
    }
    @FXML
    void closeOnAction() {
        Constants.action = ActionDatabase.INSERT;
        new SwitchScene().closeFloatScene(1);
    }

    @FXML
    void saveOnAction() {
        if(Constants.action == ActionDatabase.INSERT){
            insert();
        } else if (Constants.action == ActionDatabase.UPDATE) {
            update();
        }
    }
    private void insert(){
        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setName(ingredient.getText());
        ingredientDTO.setProvider(provider.getText());
        ingredientDTO.setUnit(unitChoice.getValue());
        ingredientDTO.setStockQuantity(0.0);

        String url = "http://192.168.7.2:8080/ingredient/save";
        ApiClient.insert(url, ingredientDTO);

        new SwitchScene().closeFloatScene(1);

        Stock.fillTableIngredient();
    }

    private void update(){
        IngredientDTO ingredientDTO = IngredientInfo.getIngredientUpdateDTO();
        ingredientDTO.setName(ingredient.getText());
        ingredientDTO.setProvider(provider.getText());
        ingredientDTO.setUnit(unitChoice.getValue());

        String url = "http://192.168.7.2:8080/ingredient/update/"+button.getId();
        ApiClient.update(url, ingredientDTO);

        new SwitchScene().closeFloatScene(1);

        Stock.fillTableIngredient();
        Constants.action = ActionDatabase.INSERT;
    }

    private void setDataUpdate(){
        IngredientDTO ingredientDTO = IngredientInfo.getIngredientUpdateDTO();
        ingredient.setText(ingredientDTO.getName());
        provider.setText(ingredientDTO.getProvider());
        unitChoice.setValue(ingredientDTO.getUnit());
        button.setId(String.valueOf(ingredientDTO.getId()));
    }

}
