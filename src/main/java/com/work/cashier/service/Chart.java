package com.work.cashier.service;

import animatefx.animation.Pulse;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.controller.infoTable.IngredientRecipeInfo;
import com.work.cashier.data_transfert_object.ingredient.IngredientDTO;
import com.work.cashier.data_transfert_object.product.ProductDTO;
import com.work.cashier.data_transfert_object.product.ProductIngredientDTO;
import com.work.cashier.data_transfert_object.stock.StockTransactionDTO;
import com.work.cashier.data_transfert_object.unitOption.Unit;
import com.work.cashier.data_transfert_object.unitOption.UnitConverter;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;

@Setter
@Getter
public class Chart {

    private PieChart pieChart;
    private BarChart<String,Number> barChart;
    private Long idProduct;
    private int quantity;
    private String nameProduct;
    private VBox containerListRecipe;

    public void configure(boolean statusList){
        if (!pieChart.getData().isEmpty()) {
            pieChart.getData().clear();
        }
        if(containerListRecipe != null) containerListRecipe.getChildren().clear();
        ProductDTO productDTO = ApiClient.getOneEntity("http://192.168.7.2:8080/product/getByName?name="+nameProduct,
                ProductDTO.class);
        assert productDTO != null;
        double delay = 0.0;
        for(ProductIngredientDTO productIngredientDTO : productDTO.getProductIngredients()){

            String urlIngredient = "http://192.168.7.2:8080/ingredient/getById/"+productIngredientDTO.getIdIngredient();
            IngredientDTO ingredientDTO = ApiClient.getOneEntity(urlIngredient, IngredientDTO.class);
            assert ingredientDTO != null;
            double weight = productIngredientDTO.getQuantityRequired();
            double normalWeight = weight;
            if(!ingredientDTO.getUnit().equals(Unit.GRAM)){
                weight = UnitConverter.convert(weight, ingredientDTO.getUnit(),Unit.GRAM);
            }
            int productProvided = productDTO.getProvidedProducts().getQuantity();
            double quantityNeeded = quantity * weight / productProvided;
            double normalQuantityNeeded = quantity * normalWeight / productProvided;
            pieChart.getData().add(new PieChart.Data(productIngredientDTO.getIngredientName()+" : "+
                    String.format("%.2f", normalQuantityNeeded) + " "+ingredientDTO.getUnit(),
                    Double.parseDouble(String.format("%.2f",quantityNeeded).replace(",","."))));
            if(statusList){
                displayInTable(ingredientDTO.getName(),String.format("%.2f", normalQuantityNeeded) +" "+
                        ingredientDTO.getUnit(),delay);
            }
            delay += 0.25;
        }

    }
    public void displayInTable(String nameProduct,String infoQuantity,double delay){
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Application.class.getResource("info_table/ingredientRecipeInfo.fxml"));
        try {
            HBox hBox = fxmlLoader.load();
            IngredientRecipeInfo info = fxmlLoader.getController();
            info.setData(nameProduct,infoQuantity);
            containerListRecipe.getChildren().add(hBox);
            new NodeAnimation().animate(hBox, delay, new Pulse());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void buildGroupedBarChart(List<StockTransactionDTO> dailyStockSummary) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Ingrédients");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Quantité");

        //barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Résumé Journalier des Stocks par Ingrédient");

        XYChart.Series<String, Number> beforeSeries = new XYChart.Series<>();
        beforeSeries.setName("Avant");

        XYChart.Series<String, Number> usedSeries = new XYChart.Series<>();
        usedSeries.setName("Utilisée");

        XYChart.Series<String, Number> afterSeries = new XYChart.Series<>();
        afterSeries.setName("Après");

        for (StockTransactionDTO dto : dailyStockSummary) {
            String ingredient = dto.getIngredientName();

            beforeSeries.getData().add(new XYChart.Data<>(ingredient, dto.getQuantityBefore()));
            usedSeries.getData().add(new XYChart.Data<>(ingredient, dto.getQuantityUsed()));
            afterSeries.getData().add(new XYChart.Data<>(ingredient, dto.getQuantityAfter()));
        }
        barChart.getData().addAll(beforeSeries,usedSeries,afterSeries);
        /*barChart.getData().add(beforeSeries);
        barChart.getData().add(usedSeries);
        barChart.getData().add(afterSeries);

         */

    }

}
