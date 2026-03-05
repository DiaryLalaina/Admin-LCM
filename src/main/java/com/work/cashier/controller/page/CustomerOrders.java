package com.work.cashier.controller.page;

import animatefx.animation.AnimationFX;
import animatefx.animation.ZoomInUp;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.controller.infoTable.OrderInformation;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.service.Chart;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import com.work.cashier.webSocket.WebSocketFX;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerOrders implements Initializable {

    @FXML
    private JFXDatePicker date;

    @FXML
    private Label totalArticle,subTotalArticle;

    @FXML
    private VBox containerOrderList,contentRawMaterialsUsed;

    @FXML
    private PieChart pieChart;

    @FXML
    private JFXComboBox<String> nameProductChoice;

    @Getter
    private static VBox containerOrders;

    @Getter
    private static String dateOrders;

    private WebSocketFX ws;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        pieChart.setLabelsVisible(false);
        date.setValue(LocalDate.now());
        dateOrders = String.valueOf(date.getValue());
        setContainerOrders(containerOrderList);
        datePickerOnAction();
        //initWebSocket();
    }

    @FXML
    void datePickerOnAction() {
        dateOrders = String.valueOf(date.getValue());
        String url = "http://192.168.7.2:8080/orderLine/getSumBread/"+dateOrders;
        String totalBread = ApiClient.getString(url);
        totalArticle.setText(totalBread.isEmpty() ? (0+" pains") : new ControlsOption().thousandSeparator(Double.parseDouble(totalBread))+" pains");
        showOrderList();
        disposeNameProductChoice();
    }
    @FXML
    void onChoiceProduct(){
        showPieChart();
    }


    private void initWebSocket() {
        ws = new WebSocketFX();
        ws.connect(() -> {
            System.out.println("🔁 Rafraîchissement des ventes");
            showOrderList();
        }, "/topic/order");
    }

    private void disposeNameProductChoice(){
        nameProductChoice.getItems().clear();
        String urlNameProductList = "http://192.168.7.2:8080/orderLine/getProductTotalList/"+dateOrders;
        nameProductChoice.getItems().addAll(ApiClient.getListString(urlNameProductList));
    }

    private void showPieChart(){
        Chart chart = new Chart();
        chart.setContainerListRecipe(contentRawMaterialsUsed);
        chart.setPieChart(pieChart);
        chart.setNameProduct(nameProductChoice.getValue());
        String quantity = ApiClient.getString("http://192.168.7.2:8080/orderLine/getSumBreadByDateAndProduct?nameProduct="+
                nameProductChoice.getValue()+"&date="+dateOrders);
        chart.setQuantity(Integer.parseInt(quantity));
        chart.configure(true);
        subTotalArticle.setText("Production : "+quantity+" "+nameProductChoice.getValue());
    }

    public static void showOrderList(){
        String url = "http://192.168.7.2:8080/order/getByDate/"+dateOrders;
        CustomerOrders.getContainerOrders().getChildren().clear();
        List<OrderDTO> orderDTOList = ApiClient.getAll(url,OrderDTO.class);

        double delay = 0.0;
        for(OrderDTO dto : orderDTOList) {
            CustomerDTO customerDTO = ApiClient.getOneEntity(
                    "http://192.168.7.2:8080/customer/" + dto.getIdCustomer(),
                    CustomerDTO.class
            );

            if (customerDTO == null || dto.getOrderLines().isEmpty()) {
                return;
            }
            dto.setNameCustomer(customerDTO.getFirstName());
        }


        orderDTOList.sort(Comparator.comparing(
                OrderDTO::getNameCustomer,
                String.CASE_INSENSITIVE_ORDER
        ));

        for (OrderDTO dto : orderDTOList) {
            addOrderInTable(dto,delay,new ZoomInUp());
            delay += 0.15;
        }
    }

    private static void addOrderInTable(OrderDTO dto,double delay ,AnimationFX animationFX){
        FXMLLoader fxmlLoader = new FXMLLoader();
        //fxmlLoader.setLocation(Application.class.getResource("info_table/orderInfo.fxml"));
        fxmlLoader.setLocation(Application.class.getResource("info_table/orderInformation.fxml"));
        try {
            HBox hBox = fxmlLoader.load();
            OrderInformation info = fxmlLoader.getController();
            info.setDto(dto);
            info.setData();
            CustomerOrders.getContainerOrders().getChildren().add(hBox);
            new NodeAnimation().animate(hBox, delay, animationFX);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setContainerOrders(VBox containerOrders) {
        CustomerOrders.containerOrders = containerOrders;
    }
}

