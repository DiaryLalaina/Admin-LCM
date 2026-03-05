package com.work.cashier.controller.floatNode;

import animatefx.animation.FadeInLeft;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.api.ApiResult;
import com.work.cashier.controller.infoTable.PaymentEmployeeInfo;
import com.work.cashier.controller.infoTable.UserInfo;
import com.work.cashier.data_transfert_object.employee.EmployeeSalaryDTO;
import com.work.cashier.data_transfert_object.employee.EmployeeSalaryType;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.mask.TextFieldMask;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SalaryForm implements Initializable {

    @FXML
    private Label infoEmployee;

    @FXML
    private JFXTextField description,amount;

    @FXML
    private Label totalAmount;

    @FXML
    private JFXComboBox<EmployeeSalaryType> salaryType;

    @Setter
    @FXML
    private JFXButton saveBtn,hideBtn;

    @FXML
    private JFXDatePicker startPicker,endPicker;

    @FXML
    private VBox containerPayment;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        TextFieldMask.onlyNumbers(amount);
        startPicker.setValue(LocalDate.now());
        endPicker.setValue(LocalDate.now());
        salaryType.getItems().addAll(EmployeeSalaryType.values());
        new ControlsOption().jfxButtonOption(saveBtn,"fa-check", Color.GREEN);
        new ControlsOption().jfxButtonOption(hideBtn,"fa-window-close", Color.WHITE);
        Platform.runLater(this::fillSalaries);
    }

    @FXML
    void save() {
        if(!description.getText().isBlank() && !amount.getText().isBlank()) {
            String url = "http://192.168.7.2:8080/salary/save";
            EmployeeSalaryDTO employeeSalaryDTO = new EmployeeSalaryDTO();
            ApiClient.insert(url, dto(employeeSalaryDTO));
            refresh();
            refreshSalaries();
        }
    }

    @FXML
    void selectEnd() {
        refreshSalaries();
    }

    @FXML
    void selectStart() {
        refreshSalaries();
    }

    @FXML
    void close(){
        new SwitchScene().closeFloatScene(1);
    }

    private void refreshSalaries(){
        if (startPicker.getValue().isBefore(endPicker.getValue())
                || startPicker.getValue().isEqual(endPicker.getValue())) {
            fillSalaries();
        }
    }

    private void fillSalaries(){
        String url = "http://192.168.7.2:8080/salary/salariesByIdEmployee?idEmployee="+UserInfo.getEmployeeDTOClicked().getId()+
                "&start="+startPicker.getValue()+"&end="+endPicker.getValue();
        containerPayment.getChildren().clear();
        ApiResult<EmployeeSalaryDTO> list = ApiClient.getSalaries(url, EmployeeSalaryDTO.class);

        totalAmount.setText(list.getTotal()+" Ar");
        double delay = 0.0;

        for (EmployeeSalaryDTO dto : list.getList()) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/paymentEmployeeInfo.fxml"));
            try {
                HBox hBox = fxmlLoader.load();
                PaymentEmployeeInfo info = fxmlLoader.getController();
                info.setDto(dto);
                info.setData();
                containerPayment.getChildren().add(hBox);

                new NodeAnimation().animate(hBox,delay,new FadeInLeft());

                delay += 0.1;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void refresh(){
        description.clear();
        amount.clear();
    }

    private EmployeeSalaryDTO dto(EmployeeSalaryDTO dto){
        dto.setType(salaryType.getValue());
        dto.setDescription(description.getText());
        dto.setSalary(Integer.parseInt(amount.getText()));
        dto.setIdEmployee(UserInfo.getEmployeeDTOClicked().getId());
        return dto;
    }

}
