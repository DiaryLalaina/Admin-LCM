package com.work.cashier.controller.infoTable;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXButton;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.controller.floatNode.PaymentForm;
import com.work.cashier.controller.listener.PaymentListener;
import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.payment.PaymentDTO;
import com.work.cashier.data_transfert_object.payment.UnpaidDTO;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.ResourceBundle;

public class OrderPaymentInfo implements Initializable {

    @FXML
    private Label reference,quantity,total,advance,remain;

    @FXML
    private JFXButton paymentBtn;

    @Setter
    private UnpaidDTO dto;

    @Setter
    private PaymentListener paymentListener;

    @Setter @Getter
    private static UnpaidDTO unpaidDTO;

    private final ControlsOption controlsOption = new ControlsOption();

    @Override
    public void initialize(URL url , ResourceBundle resourceBundle){
        controlsOption.jfxButtonOption(paymentBtn,"fa-ellipsis-v", Color.BLUEVIOLET);
    }

    public void setData(){
        OrderDTO orderDTO = ApiClient.getOneEntity(
                "http://192.168.7.2:8080/order/"+dto.getIdOrder(), OrderDTO.class);
        reference.setId(String.valueOf(dto.getIdOrder()));
        assert orderDTO != null;
        quantity.setText(orderDTO.getOrderLines().getFirst().getQuantity()+"");
        reference.setText(LocalDate.parse(dto.getCreatedAt()).
                format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        total.setText(controlsOption.thousandSeparator(dto.getTotal())+" Ar");
        advance.setText(controlsOption.thousandSeparator(dto.getAdvance())+" Ar");
        remain.setText(controlsOption.thousandSeparator(dto.getTotal()-dto.getAdvance())+" Ar");
    }

    @FXML
    void payment() {
        PaymentForm.setIdOrder(dto.getIdOrder());
        notifyReference(dto.getReference());
        showPaymentByOrder(dto.getIdOrder());
        setUnpaidDTO(dto);
    }

    public void notifyReference(String reference) {
        if (paymentListener != null) {
            paymentListener.setReferenceOrder(reference);
        }
    }

    public static void showPaymentByOrder(Long idOrder) {
        String url = "http://192.168.7.2:8080/payment/getList?idOrder="+idOrder;
        List<PaymentDTO> list = ApiClient.getAll(url, PaymentDTO.class);
        PaymentForm.getContainerPayment().getChildren().clear();

        double delay = 0.0;
        for (PaymentDTO dto : list) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/paymentInfo.fxml"));
            try {
                HBox hbox = fxmlLoader.load();
                PaymentInfo info = fxmlLoader.getController();
                info.setDto(dto);
                info.setData();
                PaymentForm.getContainerPayment().getChildren().add(hbox);

                new NodeAnimation().animate(hbox, delay, new FadeIn());

                delay += 0.1;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
