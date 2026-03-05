package com.work.cashier.controller.floatNode;

import com.jfoenix.controls.JFXButton;
import com.work.cashier.api.ApiClient;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.order.OrderLineDTO;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.Chart;
import com.work.cashier.service.ControlsOption;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import lombok.Setter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class IngredientProportionForProduction implements Initializable {

    @FXML
    private JFXButton hideBtn,leftBtn,rightBtn;

    @FXML
    private Label infoArticle;

    @FXML
    private PieChart pieChart;

    @Setter
    private OrderDTO orderDTOSelected;

    private final ControlsOption controlsOption = new ControlsOption();

    private final List<OrderLineDTO> lineDTO = new ArrayList<>();

    private int count = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        Platform.runLater(() ->{
            if(!orderDTOSelected.getOrderLines().isEmpty()){
                lineDTO.addAll(orderDTOSelected.getOrderLines());
                showPieChart();
            }
        });
        controlsOption.jfxButtonOption(hideBtn,"fa-times", Color.WHITE);
        controlsOption.jfxButtonOption(leftBtn,"fa-chevron-left",Color.BLACK);
        controlsOption.jfxButtonOption(rightBtn,"fa-chevron-right",Color.BLACK);
    }

    @FXML
    void close() {
        new SwitchScene().closeFloatScene(1);
    }

    @FXML
    void toLeft() {
        count = (count == 0) ? lineDTO.size() - 1 : (count-1);
        showPieChart();
    }

    @FXML
    void toRight() {
        count  = (count == (lineDTO.size()-1))? 0 : (count+1);
        showPieChart();
    }

    private void showPieChart(){
        setInfoArticle(lineDTO.get(count));
        Chart chart = new Chart();
        chart.setPieChart(pieChart);
        chart.setNameProduct(lineDTO.get(count).getNameProduct());
        chart.setQuantity(lineDTO.get(count).getQuantity());
        chart.configure(false);
    }

    private void setInfoArticle(OrderLineDTO lineDTO){
        String nameProduct = ApiClient.getString("http://192.168.7.2:8080/product/getName/"+lineDTO.getIdProduct());
        lineDTO.setNameProduct(nameProduct);
        infoArticle.setText("Article : "+nameProduct+" -- Quantité : "+lineDTO.getQuantity());
    }

}
