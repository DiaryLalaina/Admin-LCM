package com.work.cashier.data_transfert_object.responsible;

import com.work.cashier.data_transfert_object.user.UserDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponsibleDTO extends UserDTO {

    private Boolean authorisation;

}
