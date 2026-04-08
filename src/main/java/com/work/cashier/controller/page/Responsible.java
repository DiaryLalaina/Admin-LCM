package com.work.cashier.controller.page;

import animatefx.animation.ZoomInUp;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.work.cashier.Application;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.ActionDatabase;
import com.work.cashier.constants.Constants;
import com.work.cashier.controller.infoTable.ResponsibleInfo;
import com.work.cashier.controller.listener.ResponsibleListener;
import com.work.cashier.data_transfert_object.responsible.ResponsibleDTO;
import com.work.cashier.data_transfert_object.user.UserRoleType;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.ControlsOption;
import com.work.cashier.service.NodeAnimation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Responsible implements Initializable, ResponsibleListener {

    @FXML
    private VBox containerData;

    @FXML
    private JFXButton addBtn,timekeepingBtn,cancelBtn;

    @FXML
    private JFXTextField firstName, lastName, phone, cin, address, password;

    @FXML
    private JFXComboBox<UserRoleType> roleChoice;

    private final ControlsOption controlsOption = new ControlsOption();

    @Setter @Getter
    private static VBox container;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        setContainer(containerData);
        Constants.action = ActionDatabase.INSERT;
        controlsOption.jfxButtonOption(addBtn,"fa-plus", Color.DARKGREEN);
        controlsOption.jfxButtonOption(timekeepingBtn,"fa-clock-o", Color.WHITE);
        controlsOption.jfxButtonOption(cancelBtn,"fa-times", Color.WHITE);
        roleChoice.getItems().addAll(UserRoleType.values());
        roleChoice.setValue(UserRoleType.ROLE_ADMIN);
        Platform.runLater(this::fillTable);
    }

    @FXML
    void onMouseEntered(MouseEvent event) {
        ScrollPane scrollPane = (ScrollPane) event.getSource();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    }

    @FXML
    void add_OnAction() {
        if(Constants.action == ActionDatabase.INSERT) {
            ApiClient.insert("http://192.168.7.2:8080/responsible/save", responsibleDTO());
        }else ApiClient.update("http://192.168.7.2:8080/responsible/update?id="+
                lastName.getId()+"&withPassword="+true,responsibleDTO());
        fillTable();
        refresh();
        Constants.action = ActionDatabase.INSERT;
    }

    @FXML
    void timekeeping_OnAction() {
        new SwitchScene().showFloatNode("timeKeeping",0);
    }

    @FXML
    void cancel_OnAction(){
        refresh();
        Constants.action = ActionDatabase.INSERT;
    }

    @FXML
    void onMouseExited(MouseEvent event) {
        ScrollPane scrollPane = (ScrollPane) event.getSource();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    public void fillTable(){
        String url = "http://192.168.7.2:8080/responsible/getList";
        getContainer().getChildren().clear();

        List<ResponsibleDTO> list = ApiClient.getAll(url, ResponsibleDTO.class);
        double delay = 0.0;

        for(ResponsibleDTO dto : list){
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Application.class.getResource("info_table/responsibleInfo.fxml"));
            try {
                HBox hbox = fxmlLoader.load();
                ResponsibleInfo info = fxmlLoader.getController();
                info.setDto(dto);
                info.setListener(this);
                info.setData();
                getContainer().getChildren().add(hbox);

                new NodeAnimation().animate(hbox,delay,new ZoomInUp());

                delay += 0.1;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ResponsibleDTO responsibleDTO(){
        ResponsibleDTO dto = new ResponsibleDTO();
        dto.setLastName(lastName.getText().isEmpty()?"":lastName.getText());
        dto.setFirstName(firstName.getText().isEmpty()?"":firstName.getText());
        dto.setPhoneNumber(phone.getText().isEmpty()?"":phone.getText());
        dto.setCin(cin.getText().isEmpty()?"":cin.getText());
        dto.setAddress(address.getText().isEmpty()?"":address.getText());
        dto.setPassword(password.getText().isEmpty()?"":password.getText());
        dto.setAuthorisation(false);
        dto.setRole(roleChoice.getValue());
        return dto;
    }

    private void refresh(){
        JFXTextField[] textFields = {firstName, lastName, phone, cin, address, password};
        for(JFXTextField jfxTextField : textFields){
            jfxTextField.clear();
        }
        Constants.action = ActionDatabase.INSERT;
    }

    @Override
    public void setInformationUpdate(String id, String lastNameData, String firstNameData,String phoneData,
                                     String cinData, String addressData, UserRoleType role) {
        lastName.setId(id);
        lastName.setText(lastNameData);
        firstName.setText(firstNameData);
        phone.setText(phoneData);
        cin.setText(cinData);
        address.setText(addressData);
        roleChoice.setValue(role);
        Constants.action = ActionDatabase.UPDATE;
    }

    @Override
    public void refreshTable(){
        fillTable();
    }
}
