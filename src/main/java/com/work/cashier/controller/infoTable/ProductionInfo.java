package com.work.cashier.controller.infoTable;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProductionInfo {

    @FXML
    private Label article;

    @FXML
    private Label nbProduction;

    public void setData(String nameArticle,String production){
        article.setText(nameArticle.toUpperCase());
        nbProduction.setText(production);
    }
}
