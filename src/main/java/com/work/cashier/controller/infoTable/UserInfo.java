package com.work.cashier.controller.infoTable;

import animatefx.animation.Pulse;
import com.jfoenix.controls.JFXButton;
import com.work.cashier.alert.AlertMessage;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.ActionDatabase;
import com.work.cashier.constants.Constants;
import com.work.cashier.controller.page.User;
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
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.io.File;
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
        if(customerDTO.getImage() != null) {
            byte[] imageBytes = Base64.getDecoder().decode(customerDTO.getImage());
            Image img = new Image(new ByteArrayInputStream(imageBytes));
            imageView.setImage(img);
        }else imageView.setImage(new Image("user.jpg"));
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
        File picture;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(container.getScene().getWindow());

        if (selectedFile == null) {
            return;
        }
        picture = selectedFile;

        ApiClient.addImage("http://192.168.7.2:8080/customer/addImage/"+customerDTO.getId(),
                String.valueOf(customerDTO.getId()),picture);
        System.out.println("id customer "+customerDTO.getId());
        Image image = new Image(picture.toURI().toString());
        imageView.setImage(image);
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
        String firstName = userType.equals("employee")? employeeDTO.getFirstName() : customerDTO.getFirstName();
        if(new AlertMessage("Voulez-vous supprimer "+ firstName.toUpperCase()+" ?").confirmMessage()) {
            if (userType.equals("employee")) {
                String url = "http://192.168.7.2:8080/employee/delete/" + employeeDTO.getId();
                ApiClient.delete(url);
                User.fillEmployees("");
            } else if (userType.equals("customer")) {
                String url = "http://192.168.7.2:8080/customer/delete/" + customerDTO.getId();
                ApiClient.delete(url);
                User.fillCustomers("");
            }
        }
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
