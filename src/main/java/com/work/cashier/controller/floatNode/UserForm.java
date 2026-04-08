package com.work.cashier.controller.floatNode;

import com.jfoenix.controls.*;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.ActionDatabase;
import com.work.cashier.constants.Constants;
import com.work.cashier.controller.infoTable.UserInfo;
import com.work.cashier.controller.page.User;
import com.work.cashier.data_transfert_object.employee.EmployeeDTO;
import com.work.cashier.data_transfert_object.employee.JobEmployee;
import com.work.cashier.data_transfert_object.user.UserRoleType;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class UserForm implements Initializable {

    @FXML
    private JFXTextField lastName,firstName,address,cin,contact,monthlySalary,dailySalary;

    @FXML
    private JFXComboBox<UserRoleType> role;

    @FXML
    private JFXButton button,hideBtn,pictureBtn;

    @FXML
    private Label pictureFile;

    @FXML
    private VBox contentSalary;

    @FXML
    private JFXComboBox<JobEmployee> jobChoice;

    private final ControlsOption controlsOption = new ControlsOption();

    private EmployeeDTO employeeDTO;

    private File picture;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controlsOption.jfxButtonOption(button,"fa-check", Color.GREEN);
        controlsOption.jfxButtonOption(hideBtn,"fa-window-close",Color.WHITE);
        controlsOption.jfxButtonOption(pictureBtn,"fa-file-image-o",Color.WHITE);
        role.getItems().addAll(UserRoleType.values());
        jobChoice.getItems().addAll(JobEmployee.values());
        jobChoice.setValue(JobEmployee.GERANT);

        if(Constants.userRole == UserRoleType.ROLE_CUSTOMER){
            role.setValue(UserRoleType.ROLE_CUSTOMER);
            role.setMouseTransparent(true);
            pictureBtn.getParent().setVisible(false);
            pictureBtn.getParent().setManaged(false);
            contentSalary.setVisible(false);
            contentSalary.setManaged(false);
            if(Constants.action == ActionDatabase.UPDATE){
                setDataEmployeeUpdate();
            }
        }else if (Constants.userRole == UserRoleType.ROLE_EMPLOYEE){
            role.setValue(UserRoleType.ROLE_EMPLOYEE);
            role.setMouseTransparent(true);
            contentSalary.setVisible(true);
            contentSalary.setManaged(true);
            if(Constants.action == ActionDatabase.UPDATE){
                setDataEmployeeUpdate();
            }
        }
    }

    @FXML
    void close() {
        Constants.action = ActionDatabase.INSERT;
        new SwitchScene().closeFloatScene(1);
    }

    @FXML
    void manage() {
        if(Constants.action == ActionDatabase.INSERT) {
            insertEmployee();
        } else if (Constants.action == ActionDatabase.UPDATE) {
            updateEmployee();
        }
    }

    @FXML
    void selectPicture(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(pictureFile.getParent().getScene().getWindow());

        if (selectedFile != null) {
            picture = selectedFile;
            pictureFile.setText(selectedFile.getAbsolutePath());
        }
    }

    private void insertEmployee(){
        EmployeeDTO dto = new EmployeeDTO();
        String url = "http://192.168.7.2:8080/employee/save";
        ApiClient.insertMultipart(url, setEmployeeDTO(dto),picture);

        new SwitchScene().closeFloatScene(1);
        User.fillEmployees("");
    }

    private void updateEmployee(){
        String url = "http://192.168.7.2:8080/employee/update/"+ employeeDTO.getId();
        EmployeeDTO dto = new EmployeeDTO();
        ApiClient.update(url, setEmployeeDTO(dto));

        new SwitchScene().closeFloatScene(1);
        User.fillEmployees("");

        Constants.action = ActionDatabase.UPDATE;
    }

    private void setDataEmployeeUpdate(){
        employeeDTO = UserInfo.getEmployeeDTOClicked();
        lastName.setText(employeeDTO.getLastName());
        firstName.setText(employeeDTO.getFirstName());
        contact.setText(employeeDTO.getPhoneNumber());
        address.setText(employeeDTO.getAddress());
        cin.setText(employeeDTO.getCin());
        dailySalary.setText(employeeDTO.getDaily()+"");
        monthlySalary.setText(employeeDTO.getMonthly()+"");
        jobChoice.setValue(employeeDTO.getJob());
    }

    private EmployeeDTO setEmployeeDTO(EmployeeDTO employeeDTO){
        employeeDTO.setLastName(lastName.getText());
        employeeDTO.setFirstName(firstName.getText());
        employeeDTO.setAddress(address.getText());
        employeeDTO.setCin(cin.getText());
        employeeDTO.setRole(UserRoleType.ROLE_EMPLOYEE);
        employeeDTO.setPhoneNumber(contact.getText());
        employeeDTO.setPassword(firstName.getText());
        employeeDTO.setDaily(Integer.parseInt(dailySalary.getText()));
        employeeDTO.setMonthly(Integer.parseInt(monthlySalary.getText()));
        employeeDTO.setJob(jobChoice.getValue());
        return employeeDTO;
    }

}