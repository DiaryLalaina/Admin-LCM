package com.work.cashier.controller.floatNode;

import animatefx.animation.FadeInUp;
import com.jfoenix.controls.JFXButton;
import com.work.cashier.Application;
import com.work.cashier.alert.AlertMessage;
import com.work.cashier.api.ApiClient;
import com.work.cashier.controller.infoTable.FileUserOrderInfo;
import com.work.cashier.controller.infoTable.FileUserOrderLineInfo;
import com.work.cashier.controller.infoTable.ReductionInfo;
import com.work.cashier.controller.infoTable.UserInfo;
import com.work.cashier.controller.login.Login;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
import com.work.cashier.data_transfert_object.customer.CustomerReductionDTO;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.order.OrderLineDTO;
import com.work.cashier.data_transfert_object.user.UserRoleType;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ClientFile implements Initializable {

    @FXML
    private JFXButton hideBtn,reduceBtn;

    @FXML
    private Label userInfo;

    @FXML
    private ImageView picture;

    @FXML
    private VBox containerData,containerReduction;

    @FXML
    private DatePicker startPicker,endPicker;

    private static VBox containerReductionBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        containerReductionBox = containerReduction;
        CustomerDTO customerDTO = UserInfo.getCustomerDTOClicked();
        userInfo.setText(customerDTO.getLastName().toUpperCase()+" "+customerDTO.getFirstName()+"\n"+
                "Télephone : "+customerDTO.getPhoneNumber()+"\nAdresse : "+customerDTO.getAddress()+"\n"+
                "CIN : "+customerDTO.getCin());
        new ControlsOption().jfxButtonOption(hideBtn,"fa-times", Color.WHITE);
        new ControlsOption().jfxButtonOption(reduceBtn,"fa-minus", Color.BLACK);
        startPicker.setValue(LocalDate.now());
        endPicker.setValue(LocalDate.now());
        Platform.runLater(()->{
            refreshTable();
            filReductionInfo();
        });
    }

    @FXML
    void close() {
        new SwitchScene().closeFloatScene(1);
    }

    @FXML
    void reducePrice() {
        if(Login.getConnected().getRole() == UserRoleType.ROLE_ADMIN) {
            SwitchScene switchScene = new SwitchScene();
            switchScene.setCustomerDTO(UserInfo.getCustomerDTOClicked());
            switchScene.showFloatNode("reductionPrice", 1);
        }else{
            new AlertMessage("ACCES REFUSE").information();
        }
    }

    @FXML
    void start(){
        refreshTable();
    }

    @FXML
    void end(){
        refreshTable();
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

    private void refreshTable(){
        if(startPicker.getValue().isBefore(endPicker.getValue()) ||
                startPicker.getValue().isEqual(endPicker.getValue())){
            fillPrintInfo();
        }
    }

    private void fillPrintInfo() {
        String url = "http://192.168.7.2:8080/order/byCustomerAndDates?customerId="+
                UserInfo.getCustomerDTOClicked().getId()+"&startDate="+startPicker.getValue()+"&endDate="+
                endPicker.getValue();
        containerData.getChildren().clear();
        List<OrderDTO> list = ApiClient.getAll(url, OrderDTO.class);

        double delay = 0.0;

        for (OrderDTO orderDTO : list) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/printOrderInfo.fxml"));
            try {
                HBox hBox = fxmlLoader.load();

                FileUserOrderInfo info = fxmlLoader.getController();
                info.setDto(orderDTO);

                setOrderLinePrintInfo(info.getContainerOrderLines(),orderDTO.getOrderLines(),orderDTO);

                info.setData();

                containerData.getChildren().add(hBox);

                new NodeAnimation().animate(hBox,delay,new FadeInUp());
                delay += 0.1;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void filReductionInfo() {
        String url = "http://192.168.7.2:8080/reduction/getListReductions?idCustomer="+
                UserInfo.getCustomerDTOClicked().getId();
        containerReductionBox.getChildren().clear();
        List<CustomerReductionDTO> list = ApiClient.getAll(url,CustomerReductionDTO.class);

        double delay = 0.0;

        for (CustomerReductionDTO dto : list) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/reductionInfo.fxml"));
            try {
                HBox hBox = fxmlLoader.load();

                ReductionInfo info = fxmlLoader.getController();
                info.setData(dto);

                containerReductionBox.getChildren().add(hBox);

                new NodeAnimation().animate(hBox,delay,new FadeInUp());
                delay += 0.1;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void setOrderLinePrintInfo(VBox contentPrinting, List<OrderLineDTO> list,OrderDTO dto){
        int amount = 0;
        for (OrderLineDTO orderLineDTO : list) {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/printOrderLineInfo.fxml"));

            try {
                String nameProduct = ApiClient.getString("http://192.168.7.2:8080/product/getName/"+orderLineDTO.getIdProduct());
                orderLineDTO.setNameProduct(nameProduct);
                HBox hBox = fxmlLoader.load();
                FileUserOrderLineInfo info = fxmlLoader.getController();
                info.setData(orderLineDTO);

                amount += Integer.parseInt(info.getSubTotal().getText());
                contentPrinting.getChildren().add(hBox);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        dto.setTotalPrice(amount);
    }

}
