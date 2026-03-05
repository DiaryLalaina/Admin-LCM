package com.work.cashier.controller.page;

import animatefx.animation.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.controller.infoTable.ArticleInfo;
import com.work.cashier.controller.infoTable.IngredientInfo;
import com.work.cashier.controller.infoTable.StockTransactionInfo;
import com.work.cashier.data_transfert_object.ingredient.IngredientDTO;
import com.work.cashier.data_transfert_object.product.ProductDTO;
import com.work.cashier.data_transfert_object.stock.StockTransactionDTO;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.Chart;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.Getter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class Stock implements Initializable {

    @FXML
    private JFXDatePicker dateTransaction;

    @FXML
    private VBox containerRecipe,containerIngredient,containerArticle,containerListTransactions;

    @FXML
    private JFXTextField nbProduct;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private JFXButton printBtn;

    @Getter
    private static JFXTextField nbProductStatic;

    @Getter
    private static VBox containerListIngredient,containerListArticle,containerListRecipe;

    @Getter
    private static LocalDate dateStock;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        new ControlsOption().jfxButtonOption(printBtn,"fa-print", Color.BLACK);
        dateTransaction.setValue(LocalDate.now());
        setNbProductStatic(nbProduct);
        setContainerListIngredient(containerIngredient);
        setContainerListArticle(containerArticle);
        setContainerListRecipe(containerRecipe);
        Platform.runLater(()->{
            fillTableIngredient();
            fillTableArticle();
            dateTransactionsOnAction();
        });
    }

    @FXML
    void onMouseClicked(){
        nbProduct.setLabelFloat(true);
        String nb = nbProduct.getText().split(" ")[0];
        nbProduct.setText(nb);
    }

    @FXML
    void print(){
        dateStock = dateTransaction.getValue();
        new SwitchScene().showFloatNode("printing",0);
    }

    @FXML
    void dateTransactionsOnAction() {
        fillTableTransaction();
    }

    @FXML
    void onMouseEntered(MouseEvent event) {
        ScrollPane scrollPane = (ScrollPane) event.getSource();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    }

    @FXML
    void onMouseExited(MouseEvent event) {
        ScrollPane scrollPane = (ScrollPane) event.getSource();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    public static void fillTableIngredient(){
        String url = "http://192.168.7.2:8080/ingredient/getList";
        containerListIngredient.getChildren().clear();
        List<IngredientDTO> list = ApiClient.getAll(url, IngredientDTO.class);

        double delay = 0.0;

        for(IngredientDTO ingredientDTO : list){
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/ingredientInfo.fxml"));
            try {
                HBox hbox = fxmlLoader.load();
                IngredientInfo info = fxmlLoader.getController();
                info.setIngredientDTO(ingredientDTO);
                info.setData();
                containerListIngredient.getChildren().add(hbox);

                new NodeAnimation().animate(hbox,delay,new ZoomInLeft());

                delay += 0.1;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void fillTableTransaction(){
        String url = "http://192.168.7.2:8080/stockTransaction/getDailyStockSummary?transactionDate="+dateTransaction.getValue();
        containerListTransactions.getChildren().clear();
        List<StockTransactionDTO> list = ApiClient.getAll(url, StockTransactionDTO.class);

        double delay = 0.0;

        for(StockTransactionDTO dto : list){
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/stockTransactionInfo.fxml"));
            try {
                HBox hbox = fxmlLoader.load();
                StockTransactionInfo info = fxmlLoader.getController();
                info.setDto(dto);
                info.setData();
                containerListTransactions.getChildren().add(hbox);

                new NodeAnimation().animate(hbox,delay,new Pulse());

                delay += 0.1;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        barChart.getData().clear();
        Chart chart = new Chart();
        chart.setBarChart(barChart);
        chart.buildGroupedBarChart(list);
    }

    public static void fillTableArticle(){
        String url = "http://192.168.7.2:8080/product/getList";
        containerListArticle.getChildren().clear();
        List<ProductDTO> list = ApiClient.getAll(url, ProductDTO.class);

        double delay = 0.0;

        for(ProductDTO productDTO : list){
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/articleInfo.fxml"));
            try {
                HBox hbox = fxmlLoader.load();
                ArticleInfo info = fxmlLoader.getController();
                info.setProductDTO(productDTO);
                info.setData();
                containerListArticle.getChildren().add(hbox);

                new NodeAnimation().animate(hbox,delay,new ZoomInUp());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void setContainerListIngredient(VBox containerListIngredient) {
        Stock.containerListIngredient = containerListIngredient;
    }

    public static void setContainerListArticle(VBox containerListArticle) {
        Stock.containerListArticle = containerListArticle;
    }

    public static void setContainerListRecipe(VBox containerListRecipe) {
        Stock.containerListRecipe = containerListRecipe;
    }

    public static void setNbProductStatic(JFXTextField nbProductStatic) {
        Stock.nbProductStatic = nbProductStatic;
    }
}
