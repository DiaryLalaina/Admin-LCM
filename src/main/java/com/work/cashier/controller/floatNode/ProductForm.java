package com.work.cashier.controller.floatNode;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.ActionDatabase;
import com.work.cashier.constants.Constants;
import com.work.cashier.controller.infoTable.ArticleInfo;
import com.work.cashier.controller.page.Stock;
import com.work.cashier.data_transfert_object.product.ProductDTO;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class ProductForm implements Initializable {

    @FXML
    private JFXTextField product;

    @FXML
    private JFXTextField sellingPrice;

    @FXML
    private JFXButton button,hideBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        ControlsOption controlsOption = new ControlsOption();
        controlsOption.jfxButtonOption(button,"fa-check",Color.GREEN);
        controlsOption.jfxButtonOption(hideBtn,"fa-window-close",Color.RED);
        if(Constants.action == ActionDatabase.UPDATE){
            setDataUpdate();
        }
    }

    @FXML
    void close() {
        Constants.action = ActionDatabase.INSERT;
        new SwitchScene().closeFloatScene(1);
    }

    @FXML
    void save() {
        if(Constants.action == ActionDatabase.INSERT){
            insert();
        } else if (Constants.action == ActionDatabase.UPDATE) {
            update();
        }
    }

    private void setDataUpdate(){
        ProductDTO productUpdate = ArticleInfo.getProductDTOClicked();
        product.setText(productUpdate.getName());
        sellingPrice.setText(String.valueOf(productUpdate.getPrice()));
        button.setId(String.valueOf(productUpdate.getId()));
    }

    private void insert(){
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(product.getText());
        productDTO.setPrice(Integer.parseInt(sellingPrice.getText()));
        String url = "http://192.168.7.2:8080/product/save";
        ApiClient.insert(url, productDTO);

        new SwitchScene().closeFloatScene(1);

        Stock.fillTableArticle();
    }

    private void update(){
        ProductDTO productDTO = ArticleInfo.getProductDTOClicked();
        productDTO.setName(product.getText());
        productDTO.setPrice(Integer.parseInt(sellingPrice.getText()));

        String url = "http://192.168.7.2:8080/product/update/"+button.getId();
        ApiClient.update(url,productDTO);

        new SwitchScene().closeFloatScene(1);

        Stock.fillTableArticle();
        Constants.action = ActionDatabase.INSERT;
    }

}
