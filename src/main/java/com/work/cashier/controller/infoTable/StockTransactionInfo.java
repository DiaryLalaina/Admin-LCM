package com.work.cashier.controller.infoTable;

import com.work.cashier.data_transfert_object.stock.StockTransactionDTO;
import com.work.cashier.data_transfert_object.unitOption.UnitConverter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Getter;
import lombok.Setter;

public class StockTransactionInfo {

    @FXML
    private Label ingredient,quantityAfter,quantityUsed,quantityBefore;

    @Setter
    @Getter
    private StockTransactionDTO dto;

    public void setData(){
        ingredient.setText(dto.getIngredientName());
        quantityAfter.setText(String.format("%.2f",dto.getQuantityAfter())+" "+
                UnitConverter.abbreviate(dto.getUnitType()));
        quantityUsed.setText(String.format("%.2f",dto.getQuantityUsed())+" "+
                UnitConverter.abbreviate(dto.getUnitType()));
        quantityBefore.setText(String.format("%.2f",dto.getQuantityBefore())+" "+
                UnitConverter.abbreviate(dto.getUnitType()));
    }

    public void setDataPrint(){
        ingredient.setText(dto.getIngredientName());
        quantityAfter.setText(String.format("%.2f",dto.getQuantityAfter())+" "+
                UnitConverter.abbreviate(dto.getUnitType()));
        quantityUsed.setText(String.format("%.2f",dto.getQuantityUsed())+" "+
                UnitConverter.abbreviate(dto.getUnitType()));
        quantityBefore.setText(String.format("%.2f",dto.getQuantityBefore())+" "+
                UnitConverter.abbreviate(dto.getUnitType()));
        prefSize();
        ingredient.getParent().setStyle("-fx-background-color:WHITE");
    }
    private void prefSize(){
        ingredient.setPrefWidth(257.6);
        Label[] labels = {quantityAfter,quantityUsed,quantityBefore};
        for(Label label : labels){
            label.setMinWidth(180);
            label.setMaxWidth(180);
        }
    }

}
