package com.work.cashier.controller.page;

import animatefx.animation.*;
import com.jfoenix.controls.JFXComboBox;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.controller.infoTable.StockInfo;
import com.work.cashier.controller.infoTable.TopCustomerInfo;
import com.work.cashier.data_transfert_object.customer.TopCustomerDTO;
import com.work.cashier.data_transfert_object.ingredient.IngredientDTO;
import com.work.cashier.data_transfert_object.rapport.MonthlyBreadTotalDTO;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

public class Home implements Initializable {

    @FXML
    private Label employeeAtWork,employeeOnRest;

    @FXML
    private VBox stockContainer,containerTopCustomer,containerMonthlyBreadTotals;

    @FXML
    private HBox  bottomContainer;

    @FXML
    private JFXComboBox<String> monthChoice,yearChoice;

    @FXML
    private Label breadJanuary,breadFebruary,breadMarch,breadApril,breadMay,breadJune,breadJuly,
            breadAugust,breadSeptember,breadOctober,breadNovember,breadDecember;

    @FXML
    private Label mondayBread,tuesdayBread,wednesdayBread,thursdayBread,fridayBread,saturdayBread,sundayBread;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        int firstYear = LocalDate.now().getYear() - 3;
        for(int count = 0;count <= 8;count++){
            int value = firstYear+count;
            yearChoice.getItems().add(String.valueOf(value));
        }
        yearChoice.setValue(String.valueOf(LocalDate.now().getYear()));
        Month[] months = Month.values();
        for (Month month : months) {
            monthChoice.getItems().add(month.getDisplayName(TextStyle.FULL, Locale.FRENCH));
        }
        Platform.runLater(() ->{
            showTopCustomer(LocalDate.now().getMonthValue());
            showStock();
            showMonthlyBreadTotals(yearChoice.getValue());
            showWeeklyBreadTotals();
        });
    }

    @FXML
    private void monthChoiceOnAction(){
        String selectedMonth = monthChoice.getValue();
        int monthNumber = Arrays.stream(Month.values())
                .filter(m -> m.getDisplayName(TextStyle.FULL, Locale.FRENCH).equals(selectedMonth))
                .findFirst()
                .map(Month::getValue)
                .orElse(0);
        showTopCustomer(monthNumber);
    }

    @FXML
    private void yearChoiceOnAction(){
        FadeOut fadeOut = new FadeOut(containerMonthlyBreadTotals);
        fadeOut.setOnFinished(_-> {
            new FadeIn(containerMonthlyBreadTotals).play();
            showMonthlyBreadTotals(yearChoice.getValue());
        });
        fadeOut.play();

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

    private void showMonthlyBreadTotals(String year){
        Label[] monthLabels = {breadJanuary,breadFebruary,breadMarch,breadApril,breadMay,breadJune,breadJuly,
                breadAugust,breadSeptember,breadOctober,breadNovember,breadDecember};
        String url = "http://192.168.7.2:8080/orderLine/getMonthlyBread?year="+year;
        List<MonthlyBreadTotalDTO> list = ApiClient.getAll(url, MonthlyBreadTotalDTO.class);
        int month = 1;
        for(Label label : monthLabels){
            label.setText("0 Pains");
            for(MonthlyBreadTotalDTO dto : list){
                if(dto.getMonth() == month) {
                    label.setText(dto.getTotalQuantity() + " Pains");
                }
            }
            month++;
        }
    }

    private void showTopCustomer(int month){
        String url = "http://192.168.7.2:8080/customer/top?month="+month+"&year="+ LocalDate.now().getYear();
        containerTopCustomer.getChildren().clear();
        List<TopCustomerDTO> list = ApiClient.getAll(url, TopCustomerDTO.class);

        int number = 1;
        double delay = 0.0;

        for (TopCustomerDTO customerDTO : list) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/topCustomerInfo.fxml"));
            try {
                HBox hBox = fxmlLoader.load();
                TopCustomerInfo info = fxmlLoader.getController();
                info.setData(number,customerDTO);
                containerTopCustomer.getChildren().add(hBox);

                new NodeAnimation().animate(hBox,delay,new FadeInUp());

                delay += 0.1;
                number++;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void showStock(){
        String url = "http://192.168.7.2:8080/ingredient/getList";
        stockContainer.getChildren().clear();
        List<IngredientDTO> list = ApiClient.getAll(url, IngredientDTO.class);

        double delay = 0.0;

        for (IngredientDTO dto : list) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/stockInfo.fxml"));
            try {
                HBox hBox = fxmlLoader.load();
                StockInfo info = fxmlLoader.getController();
                info.setData(dto);
                stockContainer.getChildren().add(hBox);

                new NodeAnimation().animate(hBox,delay,new ZoomInDown());

                delay += 0.1;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void showWeeklyBreadTotals(){
        ControlsOption controlsOption = new ControlsOption();
        Label[] labels = {mondayBread,tuesdayBread,wednesdayBread,thursdayBread,fridayBread,saturdayBread,sundayBread};
        int count = 0;
        List<LocalDate> list = getCurrentWeekDates();
        for (Label label : labels){

            String url = "http://192.168.7.2:8080/orderLine/getSumBread/"+list.get(count);
            String sumBreads = ApiClient.getString(url);
            label.setText(!sumBreads.isBlank()?controlsOption.thousandSeparator(Double.parseDouble(sumBreads))+"\nPains":"0 Pains");

            FadeInDown fade = new FadeInDown(label.getParent()) {
                @Override
                public void play() {
                    label.getParent().setVisible(true);
                    super.play();
                }
            };
            fade.play();

            count++;
        }
    }
    private List<LocalDate> getCurrentWeekDates() {
        LocalDate today = LocalDate.now();

        LocalDate monday = today.with(DayOfWeek.MONDAY);

        List<LocalDate> weekDates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDates.add(monday.plusDays(i));
        }

        return weekDates;
    }
}
