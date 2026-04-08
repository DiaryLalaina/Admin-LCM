package com.work.cashier.controller.infoTable;

import com.work.cashier.api.ApiClient;
import com.work.cashier.data_transfert_object.responsible.ResponsibleDTO;
import com.work.cashier.data_transfert_object.responsible.TimeKeepingDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TimeKeepingInfo {

    @FXML
    private Label hour, user,role;

    public void setData(TimeKeepingDTO dto){
        ResponsibleDTO responsibleDTO = ApiClient.getOneEntity(
                "http://192.168.7.2:8080/responsible/by-id?id="+dto.getIdResponsible(),
                ResponsibleDTO.class);
        hour.setText(dto.getTime().split("\\.")[0]);
        assert responsibleDTO != null;
        user.setText(responsibleDTO.getFirstName().toUpperCase()+"  "+responsibleDTO.getPhoneNumber());
        role.setText(String.valueOf(responsibleDTO.getRole()));
    }

}
