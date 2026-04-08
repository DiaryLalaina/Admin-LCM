package com.work.cashier.controller.floatNode;

import animatefx.animation.Pulse;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.work.cashier.Application;
import com.work.cashier.alert.AlertMessage;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.ActionDatabase;
import com.work.cashier.constants.Constants;
import com.work.cashier.controller.infoTable.OrderLineInfo;
import com.work.cashier.controller.page.CustomerOrders;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.order.OrderLineDTO;
import com.work.cashier.data_transfert_object.product.ProductDTO;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class OrderCustomerForm implements Initializable{

    @FXML
    private JFXButton hideBtn;

    @FXML
    private JFXComboBox<String> clientChoice;

    @FXML
    private Label totalPriceOrder,refOrder;

    @FXML
    private VBox containerOrderLine;

    @FXML
    private JFXTextField article,price,quantity,subTotal;

    @Setter
    private OrderDTO orderDTOSelected;

    private final ControlsOption controlsOption = new ControlsOption();

    private final ObservableList<String> nameProductList = FXCollections.observableArrayList();

    private final ContextMenu suggestionsPopup = new ContextMenu();

    @Setter
    private CustomerDTO customerDTO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controlsOption.jfxButtonOption(hideBtn,"fa-window-close",Color.RED);

        Platform.runLater(()->{
            autoCompletion();
            quantity.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    if(new AlertMessage("Voulez-vous insérer ?").confirmMessage()) {
                        System.out.println("INSERTION AVEC succées");
                    }
                }
            });
            clientChoice.getItems().add(customerDTO.getFirstName().toUpperCase());
            clientChoice.setValue(customerDTO.getFirstName().toUpperCase());
            clientChoice.setMouseTransparent(true);
        });
        /*
        *Platform.runLater(() -> {
            showListOrderLine();
            clientChoice.getItems().add(orderDTOSelected.getNameCustomer());
            clientChoice.setValue(orderDTOSelected.getNameCustomer());
            refOrder.setText("Ref : "+ orderDTOSelected.getReference());
            totalPriceOrder.setText(controlsOption.thousandSeparator(orderDTOSelected.getTotalPrice())+" Ar");
            clientChoice.setMouseTransparent(true);
        });

         */
    }

    @FXML
    void close() {
        Constants.action = ActionDatabase.INSERT;
        new SwitchScene().closeFloatScene(1);
    }

    @FXML
    void articleChoice_OnAction(){
        if(customerDTO != null) {
            String url = "http://192.168.7.2:8080/product/getByNameWithReduction?name=" + article.getText()
                    + "&idCustomer=" + customerDTO.getId();
            ProductDTO productDTO = ApiClient.getOneEntity(url, ProductDTO.class);
            assert productDTO != null;
            price.setText(productDTO.getPrice()+"");
            quantity.requestFocus();
        }else new AlertMessage("Client introuvable").information();
    }

    private List<String> products(){
        String urlProduct = "http://192.168.7.2:8080/product/getNames";
        return ApiClient.getListString(urlProduct);
    }

    private void autoCompletion(){

        nameProductList.addAll(products());
        article.textProperty().addListener((_, _, newText) -> {
            if (newText == null || newText.isEmpty()) {
                suggestionsPopup.hide();
                return;
            }

            List<String> matches = nameProductList.stream()
                    .filter(name -> name.toLowerCase().startsWith(newText.toLowerCase()))
                    .toList();

            if (!matches.isEmpty()) {
                suggestionsPopup.requestFocus();
                suggestionsPopup.getItems().clear();
                for (String match : matches) {
                    MenuItem item = new MenuItem(match);
                    item.setOnAction(_ -> {
                        article.setText(match);
                        article.positionCaret(match.length());
                        suggestionsPopup.hide();
                    });
                    suggestionsPopup.getItems().add(item);
                    suggestionsPopup.requestFocus();
                }

                if (!suggestionsPopup.isShowing()) {
                    suggestionsPopup.requestFocus();
                    suggestionsPopup.show(article, Side.BOTTOM, 0, 0);
                } else {
                    suggestionsPopup.hide();
                    suggestionsPopup.show(article, Side.BOTTOM, 0, 0);
                }

            } else {
                suggestionsPopup.hide();
            }
        });

        article.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (!suggestionsPopup.getItems().isEmpty()) {
                    article.setText(suggestionsPopup.getItems().getFirst().getText());
                }
                suggestionsPopup.hide();
            }
        });

        article.focusedProperty().addListener((_, _, newVal) -> {
            if (!newVal) suggestionsPopup.hide();
        });
    }

    private void showListOrderLine(){
        containerOrderLine.getChildren().clear();
        double delay = 0.0;
        if(!orderDTOSelected.getOrderLines().isEmpty()) {
            for (OrderLineDTO lineDTO : orderDTOSelected.getOrderLines()) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(Application.class.getResource("info_table/orderLineInfo.fxml"));
                try {
                    HBox hbox = fxmlLoader.load();
                    OrderLineInfo info = fxmlLoader.getController();

                    hbox.getProperties().put("controller", info);

                    String nameProduct = ApiClient.getString("http://192.168.7.2:8080/product/getName/"+lineDTO.getIdProduct());
                    lineDTO.setNameProduct(nameProduct);

                    info.setOrderLineDTO(lineDTO);
                    info.setData();
                    containerOrderLine.getChildren().add(hbox);

                    new NodeAnimation().animate(hbox, delay, new Pulse());

                    delay += 0.1;

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


}
