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
        if(dto.getUnit().equals(Unit.GRAM)){
            if(dto.getStockQuantity() >= 1000){
                double stockConvert = UnitConverter.convert(dto.getStockQuantity(),dto.getUnit(),Unit.KILOGRAM);
                stockConvert = Math.round(stockConvert * 100.0) / 100.0;
                weight.setText(stockConvert+" KG");
            }
        }else  {
            weight.setText(controlsOption.thousandSeparator(dto.getStockQuantity())+" "+
                    UnitConverter.abbreviate(String.valueOf(dto.getUnit())));
        }
    }

}
