package com.work.cashier.controller.infoTable;

import animatefx.animation.Pulse;
import com.jfoenix.controls.JFXButton;
import com.work.cashier.constants.ActionDatabase;
import com.work.cashier.constants.Constants;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
import com.work.cashier.data_transfert_object.employee.EmployeeDTO;
import com.work.cashier.data_transfert_object.user.UserRoleType;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;

public class UserInfo implements Initializable {

    @FXML
    private VBox container;

    @FXML
    private ImageView imageView;

    @FXML
    private Label name,phone;

    @FXML
    private JFXButton userBtn,paymentBtn,updateBtn,deleteBtn,addImageBtn;

    private final ControlsOption controlsOption = new ControlsOption();

    @Setter
    private CustomerDTO customerDTO;

    @Setter
    private EmployeeDTO employeeDTO;

    @Getter @Setter
    private static CustomerDTO customerDTOClicked;

    @Getter @Setter
    private static EmployeeDTO employeeDTOClicked;

    @Setter
    private String userType;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controlsOption.jfxButtonOption(userBtn,"fa-user", Color.BLUE);
        controlsOption.jfxButtonOption(updateBtn,"fa-pencil",Color.GREEN);
        controlsOption.jfxButtonOption(deleteBtn,"fa-trash-o",Color.TOMATO);
        controlsOption.jfxButtonOption(paymentBtn,"fa-credit-card-alt",Color.DARKVIOLET);
        controlsOption.jfxButtonOption(addImageBtn,"fa-file-image-o",Color.web("#00AC73"));
    }

    public void setDataCustomer(){
        imageView.setImage(new Image(customerDTO.getImage() == null?"user.jpg":"circle1.jpg"));
        name.setText(customerDTO.getFirstName());
        phone.setText(customerDTO.getPhoneNumber());
    }

    public void setDataEmployee(){
        if(employeeDTO.getImage() != null) {
            byte[] imageBytes = Base64.getDecoder().decode(employeeDTO.getImage());
            Image img = new Image(new ByteArrayInputStream(imageBytes));
            imageView.setImage(img);
        }
        //imageView.setImage(new Image(employeeDTO.getImage() == null?"user.jpg":employeeDTO.getImage()));
        name.setText(employeeDTO.getLastName().toUpperCase()+" "+employeeDTO.getFirstName());
        phone.setText(employeeDTO.getPhoneNumber());
    }

    @FXML
    void addImage(){

    }

    @FXML
    void clientFile() {
        if(customerDTO != null) {
            setCustomerDTOClicked(customerDTO);
            new SwitchScene().showFloatNode("userFile", 0);
        }
    }

    @FXML
    void delete() {

    }

    @FXML
    void onMouseEntered() {
        new Pulse(container).play();
    }

    @FXML
    void onMouseExited() {
       // new ZoomOut(container).play();
    }

    @FXML
    void payment() {
        if(userType.equals("customer")) {
            setCustomerDTOClicked(customerDTO);
            new SwitchScene().showFloatNode("payment",0);
        }else {
            setEmployeeDTOClicked(employeeDTO);
            new SwitchScene().showFloatNode("paymentEmployee",0);
        }
    }

    @FXML
    void update() {
        if(userType.equals("employee")) {
            setEmployeeDTOClicked(employeeDTO);
            Constants.userRole = UserRoleType.ROLE_EMPLOYEE;
            Constants.action = ActionDatabase.UPDATE;
            new SwitchScene().showFloatNode("user",0);
        }
    }
}
