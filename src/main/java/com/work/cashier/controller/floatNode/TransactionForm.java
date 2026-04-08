package com.work.cashier.controller.floatNode;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.work.cashier.api.ApiClient;
import com.work.cashier.data_transfert_object.stock.StockTransactionDTO;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.ControlsOption;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

public class TransactionForm implements Initializable {

    @FXML
    private JFXButton hideBtn;

    @FXML
    private JFXTextField ingredient,base,transaction,reason;

    @FXML
    private JFXComboBox<String> manageType,transactionChoice;

    @FXML
    private JFXButton saveBtn;

    @Setter
    private StockTransactionDTO dto;

    private final ControlsOption controlsOption = new ControlsOption();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        controlsOption.jfxButtonOption(hideBtn,"fa-close", Color.WHITE);
        manageType.getItems().addAll("ENTREE","SORTIE");
        transactionChoice.getItems().addAll(
                "","ACHAT","VENTE","EMPRUNT","PRET","REMBOURSEMENT","MAPOESA","PRODUCTION"
        );
        Platform.runLater(this::setData);
    }

    @FXML
    void close() {
        new SwitchScene().closeFloatScene(1);
    }

    @FXML
    void save() {
        String url = "http://192.168.7.2:8080/stockTransaction/update?id="+dto.getId();
        String space = transactionChoice.getValue().isBlank()?"":" ";
        dto.setReason(transactionChoice.getValue()+space+reason.getText());
        ApiClient.update(url,dto);
        close();
    }

    private void setData(){
        ingredient.setText(dto.getIngredientName());
        transactionChoice.setValue(dto.getReason().contains(" ") ? dto.getReason().split(" ")[0]:"");
        reason.setText(dto.getReason().contains(" ") ? dto.getReason().split(" ")[1]:dto.getReason());
        setType();
        transaction.setText(controlsOption.thousandSeparator(dto.getQuantityUsed()));
        base.setText(controlsOption.thousandSeparator(dto.getQuantityAfter()));
    }

    private void setType(){
        double result = dto.getQuantityAfter() - dto.getQuantityBefore();
        manageType.setValue(result < 0 ? "SORTIE":"ENTREE");
    }
}
