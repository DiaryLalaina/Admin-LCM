package com.work.cashier.controller.floatNode;

import animatefx.animation.FadeInUp;
import com.jfoenix.controls.JFXButton;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.VARIABLE_STATIC;
import com.work.cashier.controller.infoTable.UserFileInfo;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class MonthlyClientFile implements Initializable {

    @FXML
    private Label infoCustomer;

    @FXML
    private JFXButton hideBtn;

    @FXML
    private VBox containerData;

    @FXML
    private Label bread,total_amount,nbPayments,total_advanced,total_expense,total_remain;

    @Getter @Setter
    private String idCustomer,nameCustomer,month;

    private final ControlsOption controlsOption = new ControlsOption();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controlsOption.jfxButtonOption(hideBtn,"fa-times", Color.WHITE);
        Platform.runLater(()->{
            infoCustomer.setText("RAPPORT DE "+nameCustomer+" DU MOIS "+month);
            fillInfo();
        });
    }

    @FXML
    void hide() {
        new SwitchScene().closeFloatScene(1);
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

    private void animateNode(Node node, double delay) {
        new NodeAnimation().animate(node, delay, new FadeInUp());
    }

    private void renderOrder(OrderDTO orderDTO, double delay) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Application.class.getResource("info_table/fileUserInfo.fxml"));

        try {
            HBox hBox = loader.load();

            UserFileInfo info = loader.getController();
            info.setDto(orderDTO);

            info.setData();

            containerData.getChildren().add(hBox);
            animateNode(hBox, delay);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillInfo() {
        int year = LocalDate.now().getYear();

        LocalDate start = LocalDate.of(year, VARIABLE_STATIC.month, 1);

        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        VARIABLE_STATIC.fileUserOrderInfoList.clear();
        VARIABLE_STATIC.sum_amount = 0; VARIABLE_STATIC.sum_remain = 0;
        VARIABLE_STATIC.sum_paid = 0; VARIABLE_STATIC.sum_expense = 0;

        int sum_bread = 0;int sum_payment = 0;
        String url = "http://192.168.7.2:8080/order/byCustomerAndDates?customerId=" +
                idCustomer +
                "&startDate=" + start +
                "&endDate=" + end;

        containerData.getChildren().clear();

        List<OrderDTO> list = ApiClient.getAll(url, OrderDTO.class);

        Collections.reverse(list);

        double delay = 0.0;
        for (OrderDTO orderDTO : list) {
            renderOrder(orderDTO, delay);
            sum_bread += orderDTO.getOrderLines().getFirst().getQuantity();
            sum_payment += orderDTO.getPayments().size();
            delay += 0.1;
        }
        bread.setText(controlsOption.thousandSeparator(sum_bread));
        total_amount.setText(controlsOption.thousandSeparator(VARIABLE_STATIC.sum_amount));
        nbPayments.setText(controlsOption.thousandSeparator(sum_payment));
        total_advanced.setText(controlsOption.thousandSeparator(VARIABLE_STATIC.sum_paid));
        total_expense.setText(controlsOption.thousandSeparator(VARIABLE_STATIC.sum_expense));
        total_remain.setText(controlsOption.thousandSeparator(VARIABLE_STATIC.sum_remain));

    }
}
