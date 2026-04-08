package com.work.cashier.controller.infoTable;

import com.jfoenix.controls.JFXButton;
import com.work.cashier.data_transfert_object.ingredient.IngredientDTO;
import com.work.cashier.data_transfert_object.unitOption.Unit;
import com.work.cashier.data_transfert_object.unitOption.UnitConverter;
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
    private Label provider,ingredient,storage,conversion;

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
        storage.setText(ingredientDTO.getStockQuantity()+" "+
                UnitConverter.abbreviate(String.valueOf(ingredientDTO.getUnit())));
        switch (ingredientDTO.getName()) {
            case "FARINE" -> conversion.setText(String.format("%.2f", UnitConverter.
                    convert(ingredientDTO.getStockQuantity(), Unit.KILOGRAM, Unit.SAC_50Kg)) + " " +
                    UnitConverter.abbreviate(String.valueOf(Unit.SAC_50Kg)));
            case "AMELIORANT", "LEVURE" -> conversion.setText(String.format("%.2f", UnitConverter.
                    convert(ingredientDTO.getStockQuantity(), Unit.GRAM, Unit.CARTON_20Pcs)) + " " +
                    UnitConverter.abbreviate(String.valueOf(Unit.CARTON_20Pcs)));
            case "SEL" -> conversion.setText(String.format("%.2f", UnitConverter.
                    convert(ingredientDTO.getStockQuantity(), Unit.GRAM, Unit.SACHET)) + " " +
                    UnitConverter.abbreviate(String.valueOf(Unit.SACHET)));
            case "CARBURANT" -> conversion.setText(String.format("%.2f", UnitConverter.
                    convert(ingredientDTO.getStockQuantity(), Unit.LITER, Unit.BIDON)) + " " +
                    UnitConverter.abbreviate(String.valueOf(Unit.BIDON)));
            default -> conversion.setText(storage.getText());
        }
    }
}
