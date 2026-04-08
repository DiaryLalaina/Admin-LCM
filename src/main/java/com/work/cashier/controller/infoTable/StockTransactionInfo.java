package com.work.cashier.controller.infoTable;

import com.jfoenix.controls.JFXButton;
import com.work.cashier.constants.VARIABLE_STATIC;
import com.work.cashier.data_transfert_object.stock.StockTransactionDTO;
import com.work.cashier.data_transfert_object.unitOption.Unit;
import com.work.cashier.data_transfert_object.unitOption.UnitConverter;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;

public class StockTransactionInfo implements Initializable {

    @FXML
    private Label date,time,reason,ingredient,entered,sorted,stock,conversion;

    @FXML
    private HBox hBox;

    @FXML
    private JFXButton updateBtn;

    @Getter @Setter
    private String dateData,timeData,reasonData,enteredData,sortedData,stockData,conversionData;

    @Setter
    @Getter
    private StockTransactionDTO dto;

    private final ControlsOption controlsOption = new ControlsOption();

    public void setData(){
        date.setText(LocalDate.parse(dto.getTransactionDate()).
                format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
        time.setText(dto.getTransactionTime().split("\\.")[0]);
        reason.setText(dto.getReason());
        ingredient.setText(dto.getIngredientName());
        double transaction = dto.getQuantityAfter() - dto.getQuantityBefore();
        if(transaction < 0){
            sorted.setText(controlsOption.thousandSeparator(dto.getQuantityUsed())+" "+
                    UnitConverter.abbreviate(dto.getUnitType()));
        }else {
            entered.setText(controlsOption.thousandSeparator(dto.getQuantityUsed())+" "+
                    UnitConverter.abbreviate(dto.getUnitType()));
        }
        stock.setText(controlsOption.thousandSeparator(dto.getQuantityAfter())+" "+
                UnitConverter.abbreviate(dto.getUnitType()));
        switch (dto.getIngredientName()) {
            case "FARINE" -> conversion.setText(String.format("%.2f", UnitConverter.
                    convert(dto.getQuantityAfter(), Unit.KILOGRAM, Unit.SAC_50Kg)) + " " +
                    UnitConverter.abbreviate(String.valueOf(Unit.SAC_50Kg)));
            case "AMELIORANT", "LEVURE" -> conversion.setText(String.format("%.2f", UnitConverter.
                    convert(dto.getQuantityAfter(), Unit.GRAM, Unit.CARTON_20Pcs)) + " " +
                    UnitConverter.abbreviate(String.valueOf(Unit.CARTON_20Pcs)));
            case "SEL" -> conversion.setText(String.format("%.2f", UnitConverter.
                    convert(dto.getQuantityAfter(), Unit.GRAM, Unit.SACHET)) + " " +
                    UnitConverter.abbreviate(String.valueOf(Unit.SACHET)));
            case "CARBURANT" -> conversion.setText(String.format("%.2f", UnitConverter.
                    convert(dto.getQuantityAfter(), Unit.LITER, Unit.BIDON)) + " " +
                    UnitConverter.abbreviate(String.valueOf(Unit.BIDON)));
            default -> conversion.setText(stock.getText());
        }
        if(reason.getText().contains("PRET")){
            hBox.setStyle("-fx-background-color : #F4C5C0");
        }else if (reason.getText().contains("EMPRUNT")){
            hBox.setStyle("-fx-background-color : #E7EFE0");
        }else if (reason.getText().contains("REMBOURSEMENT")){
            hBox.setStyle("-fx-background-color : #F4D9FF");
        }else if (reason.getText().contains("MAPOESA")){
            hBox.setStyle("-fx-background-color : #F1ECE4");
        }else hBox.setStyle("-fx-background-color : WHITE");
        setDataPrint();
        VARIABLE_STATIC.stockTransactionInfos.add(this);
    }

    public void setDataPrint(){
        setDateData(date.getText());
        setTimeData(time.getText());
        setReasonData(reason.getText());
        double transaction = dto.getQuantityAfter() - dto.getQuantityBefore();
        if(transaction < 0){
            setSortedData(String.valueOf(dto.getQuantityUsed()));
            setEnteredData("");
        }else {
            setEnteredData(String.valueOf(dto.getQuantityUsed()));
            setSortedData("");
        }
        setStockData(controlsOption.thousandSeparator(dto.getQuantityAfter()));
        setConversionData(conversion.getText().split(" ")[0]);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        new ControlsOption().jfxButtonOption(updateBtn,"fa-edit", Color.DARKGREEN);
    }

    @FXML
    void update(){
        dto.setIngredientName(ingredient.getText());
        SwitchScene switchScene = new SwitchScene();
        switchScene.setTransactionDTO(dto);
        switchScene.showFloatNode("transaction",0);
    }
}