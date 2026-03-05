package com.work.cashier.controller.infoTable;

import com.work.cashier.data_transfert_object.employee.EmployeeSalaryDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class PaymentEmployeeInfo {

    @FXML
    private Label date,payment,amount;

    @Setter
    private EmployeeSalaryDTO dto;

    public void setData(){
        date.setText(LocalDate.parse(dto.getPayAt()).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        payment.setText(dto.getDescription()+" : "+dto.getType());
        amount.setText(dto.getSalary()+" Ar");
    }

}
