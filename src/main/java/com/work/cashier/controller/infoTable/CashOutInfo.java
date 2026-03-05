package com.work.cashier.controller.infoTable;

import com.jfoenix.controls.JFXButton;
import com.work.cashier.api.ApiClient;
import com.work.cashier.data_transfert_object.customer.CashOutDTO;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CashOutInfo {

    @FXML
    private Label customer,total,remain,paid;

    @FXML
    private JFXButton paymentBtn;

    @Setter
    private CashOutDTO cashOutDTO;

    @Setter
    private LocalDate startDate,endDate;

    private final ControlsOption controlsOption = new ControlsOption();

    @Getter
    private static List<String> data = new ArrayList<>();

    @Setter
    private String window = "paymentView" ;

    @FXML
    void payment() {
        data.clear();
        String[] listData = {String.valueOf(cashOutDTO.getIdCustomer()),customer.getText(),
                String.valueOf(startDate),String.valueOf(endDate),
                total.getText(),paid.getText(),remain.getText()};
        data.addAll(List.of(listData));
        System.out.println(window);
        new SwitchScene().showFloatNode(window,0);
    }

    public void setData(){
        controlsOption.jfxButtonOption(paymentBtn,"fa-ellipsis-h", Color.DARKGREEN);
        paymentBtn.setId(String.valueOf(cashOutDTO.getIdCustomer()));
        customer.setText(cashOutDTO.getFirstName());
        total.setText(controlsOption.thousandSeparator(cashOutDTO.getTotal())+" Ar");
        paid.setText(controlsOption.thousandSeparator(cashOutDTO.getPaid())+" Ar");
        remain.setText(controlsOption.thousandSeparator(cashOutDTO.getTotal()- cashOutDTO.getPaid())+ " Ar");
        if(!window.equals("paymentView")) {
            CustomerDTO customerDTO = ApiClient.getOneEntity("http://192.168.7.2:8080/customer/" + cashOutDTO.getIdCustomer(),
                    CustomerDTO.class);
            assert customerDTO != null;
            customer.setText(customer.getText() + " : " + customerDTO.getPhoneNumber());
            setWidth(window.equals("unpaidDetailsView")?200:150);
            if (window.equals("printingUnpaid")) {
                HBox hBox = (HBox) paymentBtn.getParent();
                hBox.setVisible(false);
                hBox.setManaged(false);
                hBox.getParent().setStyle("-fx-background-color:WHITE;" +
                        "-fx-border-color:WHITE WHITE BLACK WHITE;" +
                        "-fx-border-width:0.5;");
                setLabelStyle();
            }
        }
    }

    private void setWidth(int width){
        for( Label label : new Label[]{total,paid,remain}){
            label.setMaxWidth(width);
            label.setMinWidth(width);
        }
    }

    private void setLabelStyle(){
        for( Label label : new Label[]{customer,total,paid,remain}){
            label.setTextFill(Color.BLACK);
            label.setPadding(new Insets(5,0,5,0));
        }
    }
}
