package com.work.cashier.data_transfert_object.customer;

import com.work.cashier.data_transfert_object.order.OrderDTO;
import com.work.cashier.data_transfert_object.user.UserDTO;

import java.util.List;

public class CustomerDTO extends UserDTO {
    private List<OrderDTO> orders;
}
