package com.work.cashier.controller.floatNode;

import com.jfoenix.controls.JFXButton;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.controller.infoTable.CashOutInfo;
import com.work.cashier.data_transfert_object.customer.CashOutDTO;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.print.Print;
import com.work.cashier.service.ControlsOption;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.ResourceBundle;

public class PrintingUnpaid implements Initializable {

    @FXML
    private VBox containerUnpaid,containerPrintA4;

    @FXML
    private JFXButton hideBtn,printBtn;

    @FXML
    private Label date,totalAmount,totalAdvance,totalUnpaid;

    private final ControlsOption controlsOption = new ControlsOption();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        date.setText("Sortie le "+ LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
        controlsOption.jfxButtonOption(hideBtn,"fa-window-close", Color.WHITE);
        controlsOption.jfxButtonOption(printBtn,"fa-print",Color.DARKGREEN);
        Platform.runLater(this::fillTable);
    }

    @FXML
    void close() {
        new SwitchScene().closeFloatScene(1);
    }

    @FXML
    void print() throws IOException {
        Print print = new Print();
        print.setPrintPage(containerPrintA4);
        print.toPdfFile("Les impayés");
        close();
    }

    private void fillTable() {
        long total = 0, advance = 0, remainAmount = 0;
        String url = "http://192.168.7.2:8080/customer/getAllUnpaid";
        containerUnpaid.getChildren().clear();

        List<CashOutDTO> list = ApiClient.getAll(url, CashOutDTO.class);

        for (CashOutDTO dto : list) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/cashoutInfo.fxml"));
            try {
                HBox hbox = fxmlLoader.load();
                CashOutInfo info = fxmlLoader.getController();
                info.setCashOutDTO(dto);
                info.setWindow("printingUnpaid");
                info.setData();
                containerUnpaid.getChildren().add(hbox);

                long eachRemain = dto.getTotal() - dto.getPaid();
                remainAmount += eachRemain;
                total += dto.getTotal();
                advance += dto.getPaid();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        totalAmount.setText(controlsOption.thousandSeparator(total)+" Ar");
        totalAdvance.setText(controlsOption.thousandSeparator(advance)+" Ar");
        totalUnpaid.setText(controlsOption.thousandSeparator(remainAmount)+" Ar");
    }

}
