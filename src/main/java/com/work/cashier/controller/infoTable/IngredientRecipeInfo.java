package com.work.cashier.controller.infoTable;

import com.work.cashier.api.ApiClient;
import com.work.cashier.data_transfert_object.ingredient.IngredientDTO;
import com.work.cashier.data_transfert_object.product.ProductIngredientDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;

public class IngredientRecipeInfo{

    @FXML
    private HBox container;

    @FXML
    private Label nameIngredient,weight;

    @Setter
    private ProductIngredientDTO productIngredientDTO;
    @Getter
    private static ProductIngredientDTO productIngredientDTOClicked;


    public void setData(String nameProduct,String infoQuantity){
        nameIngredient.setText(nameProduct);
        weight.setText(infoQuantity);
        container.setStyle("-fx-background-color: E7EFE0;");
    }

    public void setData(){
        String urlIngredient = "http://192.168.7.2:8080/ingredient/getById/"+productIngredientDTO.getIdIngredient();
        IngredientDTO ingredientDTO = ApiClient.getOneEntity(urlIngredient, IngredientDTO.class);
        nameIngredient.setText(productIngredientDTO.getIngredientName());
        assert ingredientDTO != null;
        weight.setText(productIngredientDTO.getQuantityRequired()+" "+ ingredientDTO.getUnit());
    }

}
