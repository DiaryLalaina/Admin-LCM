package com.work.cashier.controller.infoTable;

import animatefx.animation.ZoomInRight;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.work.cashier.Application;
import com.work.cashier.controller.page.Stock;
import com.work.cashier.data_transfert_object.product.ProductDTO;
import com.work.cashier.data_transfert_object.product.ProductIngredientDTO;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ArticleInfo implements Initializable {

    @FXML
    private Label article,price;

    @FXML
    private JFXButton jfxButton;


    private final ControlsOption controlsOption = new ControlsOption();
    private final SwitchScene switchScene = new SwitchScene();

    @Setter
    private ProductDTO productDTO;
    @Getter
    private static ProductDTO productDTOClicked ;
    @Getter
    private static String idProductClicked;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controlsOption.jfxButtonOption(jfxButton,"fa-ellipsis-v", Color.SADDLEBROWN);
    }

    @FXML
    void menu() {
        setStaticInfo();
        if(productDTO.getProductIngredients() != null){
            fillTableRecipe();
        }
        JFXTextField nbProduct = Stock.getNbProductStatic();
        nbProduct.setText(productDTO.getProvidedProducts() != null ?
                productDTO.getProvidedProducts().getQuantity()+" "+productDTO.getName() : 0 +" "+ productDTO.getName());
        String promptText = nbProduct.getText(nbProduct.getText().split(" ").length, nbProduct.getLength());
        nbProduct.setPromptText(promptText);
        nbProduct.setLabelFloat(false);
    }

    public void setData(){
        article.setId(productDTO.getId()+"");
        article.setText(productDTO.getName());
        price.setText(productDTO.getPrice()+" Ar");
    }

    public void fillTableRecipe(){
        Stock.getContainerListRecipe().getChildren().clear();
        List<ProductIngredientDTO> list = productDTO.getProductIngredients();

        double delay = 0.0;

        for(ProductIngredientDTO ingredient : list){
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/ingredientRecipeInfo.fxml"));
            try {
                HBox hbox = fxmlLoader.load();
                IngredientRecipeInfo info = fxmlLoader.getController();
                info.setProductIngredientDTO(ingredient);
                info.setData();
                Stock.getContainerListRecipe().getChildren().add(hbox);

                new NodeAnimation().animate(hbox,delay,new ZoomInRight());

                delay += 0.1;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void setStaticInfo(){
        setProductDTOClicked(productDTO);
        setIdProductClicked(String.valueOf(productDTO.getId()));
    }

    public static void setProductDTOClicked(ProductDTO productDTOClicked) {
        ArticleInfo.productDTOClicked = productDTOClicked;
    }

    public static void setIdProductClicked(String idProductClicked) {
        ArticleInfo.idProductClicked = idProductClicked;
    }
}
