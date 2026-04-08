package com.work.cashier.controller.listener;

import com.work.cashier.data_transfert_object.user.UserRoleType;

public interface ResponsibleListener {
    void setInformationUpdate(String id, String lastNameData, String firstNameData,String phoneData,
                              String cinData, String addressData, UserRoleType role);
    void refreshTable();
}
