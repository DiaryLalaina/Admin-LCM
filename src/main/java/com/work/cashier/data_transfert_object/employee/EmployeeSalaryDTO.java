package com.work.cashier.data_transfert_object.employee;

import lombok.Getter;
import lombok.Setter;


@Setter @Getter
public class EmployeeSalaryDTO
{
    private Long id, idEmployee;

    private String payAt;

    private String description;

    private int salary;

    private EmployeeSalaryType type;
}