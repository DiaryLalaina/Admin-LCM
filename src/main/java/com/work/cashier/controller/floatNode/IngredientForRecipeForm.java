package com.work.cashier.controller.floatNode;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.ActionDatabase;
import com.work.cashier.constants.Constants;
import com.work.cashier.controller.infoTable.ArticleInfo;
import com.work.cashier.controller.infoTable.IngredientRecipeInfo;
import com.work.cashier.data_transfert_object.ingredient.IngredientDTO;
import com.work.cashier.data_transfert_object.product.ProductDTO;
import com.work.cashier.data_transfert_object.product.ProductIngredientDTO;
import com.work.cashier.data_transfert_object.unitOption.Unit;
import com.work.cashier.data_transfert_object.unitOption.UnitConverter;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.mask.TextFieldMask;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class IngredientForRecipeForm implements Initializable {

    @FXML
    private Label title;

    @FXML
    private JFXComboBox<String> ingredient;

    @FXML
    private JFXTextField quantity;

    @FXML
    private JFXComboBox<Unit> unit;

    @FXML
    private JFXButton button,hideBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        TextFieldMask.onlyNumbers(quantity);
        title.setText("RECETTE : " + ArticleInfo.getProductDTOClicked().getName().toUpperCase());
        ControlsOption controlsOption = new ControlsOption();
        controlsOption.jfxButtonOption(button,"fa-check", Color.GREEN);
        controlsOption.jfxButtonOption(hideBtn,"fa-window-close", Color.RED);
        unit.getItems().addAll(Unit.values());
        ingredient.getItems().addAll(ApiClient.getListString("http://192.168.7.2:8080/ingredient/getNames"));
        if(Constants.action == ActionDatabase.INSERT){
            button.setId(ArticleInfo.getIdProductClicked());
        } else if (Constants.action == ActionDatabase.UPDATE) {
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
            clearForm();
        } else if (Constants.action == ActionDatabase.UPDATE) {
            update();
            closeOnAction();
        }
    }
    private void insert(){
        ProductIngredientDTO productIngredientDTO = new ProductIngredientDTO();
        productIngredientDTO.setIngredientName(ingredient.getValue());
        productIngredientDTO.setIdProduct(Long.valueOf(button.getId()));

        double quantityIngredient = Double.parseDouble(quantity.getText());

        String urlIngredient = "http://192.168.7.2:8080/ingredient/getByName/"+ingredient.getValue();
        IngredientDTO ingredientDTO = ApiClient.getOneEntity(urlIngredient, IngredientDTO.class);

        assert ingredientDTO != null;
        if(!ingredientDTO.getUnit().equals(unit.getValue())){
            quantityIngredient = UnitConverter.convert(Double.parseDouble(quantity.getText()),
                    unit.getValue(),ingredientDTO.getUnit());
        }

        productIngredientDTO.setQuantityRequired(quantityIngredient);
        String url = "http://192.168.7.2:8080/productIngredient/save";
        ApiClient.insert(url, productIngredientDTO);
        //ArticleInfo.getProductDTOClicked().getProductIngredients().add(productIngredientDTO);
        refresh();
    }

    private void update(){
        ProductIngredientDTO productIngredientDTO = IngredientRecipeInfo.getProductIngredientDTOClicked();
        productIngredientDTO.setIngredientName(ingredient.getValue());

        double quantityIngredient = Double.parseDouble(quantity.getText());

        String urlIngredient = "http://192.168.7.2:8080/ingredient/getByName/"+ingredient.getValue();
        IngredientDTO ingredientDTO = ApiClient.getOneEntity(urlIngredient, IngredientDTO.class);

        assert ingredientDTO != null;
        if(!ingredientDTO.getUnit().equals(unit.getValue())){
            String nb = quantity.getText().contains(".")?quantity.getText() : quantity.getText().split("\\.")[0];
            quantityIngredient = UnitConverter.convert(Long.parseLong(nb),
                    unit.getValue(),ingredientDTO.getUnit());
        }

        productIngredientDTO.setQuantityRequired(quantityIngredient);
        String url = "http://192.168.7.2:8080/productIngredient/update/"+productIngredientDTO.getId();
        ApiClient.update(url, productIngredientDTO);

        refresh();
    }

    private void setDataUpdate(){
        ProductIngredientDTO productIngredientDTO = IngredientRecipeInfo.getProductIngredientDTOClicked();
        ingredient.setValue(productIngredientDTO.getIngredientName());
        quantity.setText(String.valueOf(productIngredientDTO.getQuantityRequired()));

        String urlIngredient = "http://192.168.7.2:8080/ingredient/getByName/"+ingredient.getValue();
        IngredientDTO ingredientDTO = ApiClient.getOneEntity(urlIngredient, IngredientDTO.class);
        assert ingredientDTO != null;
        unit.setValue(ingredientDTO.getUnit());
    }
    private void refresh(){
        String urlProduct = "http://192.168.7.2:8080/product/"+ArticleInfo.getProductDTOClicked().getId();
        ProductDTO productDTO = ApiClient.getOneEntity(urlProduct, ProductDTO.class);

        ArticleInfo articleInfo = new ArticleInfo();
        articleInfo.setProductDTO(productDTO);
        articleInfo.fillTableRecipe();

        Constants.action = ActionDatabase.INSERT;
    }
    private void clearForm(){
        quantity.clear();
        ingredient.setValue(ingredient.getItems().getFirst());
        unit.setValue(unit.getItems().getFirst());
    }
}
