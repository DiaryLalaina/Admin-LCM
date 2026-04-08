package com.work.cashier.controller.infoTable;

import com.work.cashier.data_transfert_object.ingredient.IngredientDTO;
import com.work.cashier.data_transfert_object.unitOption.Unit;
import com.work.cashier.data_transfert_object.unitOption.UnitConverter;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StockInfo {

    @FXML
    private Label nameIngredient;

    @FXML
    private Label weight;

    private final ControlsOption controlsOption = new ControlsOption();

    public void setData(IngredientDTO dto){
        nameIngredient.setText(dto.getName());
        switch (dto.getName()) {
            case "FARINE" -> weight.setText(String.format("%.2f", UnitConverter.
                    convert(dto.getStockQuantity(), Unit.KILOGRAM, Unit.SAC_50Kg)) + " " +
                    UnitConverter.abbreviate(String.valueOf(Unit.SAC_50Kg)));
            case "AMELIORANT", "LEVURE" -> weight.setText(String.format("%.2f", UnitConverter.
                    convert(dto.getStockQuantity(), Unit.GRAM, Unit.CARTON_20Pcs)) + " " +
                    UnitConverter.abbreviate(String.valueOf(Unit.CARTON_20Pcs)));
            case "SEL" -> weight.setText(String.format("%.2f", UnitConverter.
                    convert(dto.getStockQuantity(), Unit.GRAM, Unit.SACHET)) + " " +
                    UnitConverter.abbreviate(String.valueOf(Unit.SACHET)));
            case "CARBURANT" -> weight.setText(String.format("%.2f", UnitConverter.
                    convert(dto.getStockQuantity(), Unit.LITER, Unit.BIDON)) + " " +
                    UnitConverter.abbreviate(String.valueOf(Unit.BIDON)));
            default -> weight.setText(dto.getStockQuantity()+" "+
                    UnitConverter.abbreviate(String.valueOf(dto.getUnit())));
        }

    }
}
