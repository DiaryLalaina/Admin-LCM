package com.work.cashier.data_transfert_object.employee;

import com.work.cashier.data_transfert_object.user.UserDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmployeeDTO extends UserDTO {
    private Integer monthly,daily;
    private JobEmployee job;
}
