package com.work.cashier.data_transfert_object.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public  abstract class UserDTO {
    protected Long id;

    protected String firstName, lastName, phoneNumber, cin, address, password, image;

    protected UserRoleType role;

}
