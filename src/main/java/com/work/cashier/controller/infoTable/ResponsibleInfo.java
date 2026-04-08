package com.work.cashier.controller.infoTable;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.work.cashier.alert.AlertMessage;
import com.work.cashier.api.ApiClient;
import com.work.cashier.constants.ActionDatabase;
import com.work.cashier.constants.Constants;
import com.work.cashier.controller.listener.ResponsibleListener;
import com.work.cashier.controller.login.Login;
import com.work.cashier.data_transfert_object.responsible.ResponsibleDTO;
import com.work.cashier.data_transfert_object.user.UserRoleType;
import com.work.cashier.encryption.PasswordVerifier;
import com.work.cashier.service.ControlsOption;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import lombok.Setter;

import java.util.Optional;

public class ResponsibleInfo {

    @FXML
    private Label name, phone, cin, address, role;

    @FXML
    private JFXRadioButton authorisation;

    @FXML
    private JFXButton deleteBtn,updateBtn;

    @Setter
    private ResponsibleDTO dto;

    private final PasswordVerifier passwordVerifier = new PasswordVerifier();

    private final ControlsOption controlsOption = new ControlsOption();

    @Setter
    private ResponsibleListener listener;

    public void setData() {
        name.setId(String.valueOf(dto.getId()));
        name.setText(dto.getFirstName());
        phone.setText(dto.getPhoneNumber());
        cin.setText(dto.getCin());
        address.setText(dto.getAddress());
        authorisation.setSelected(dto.getAuthorisation());
        role.setText(String.valueOf(dto.getRole()));
        controlsOption.jfxButtonOption(deleteBtn,"fa-trash", Color.WHITE);
        controlsOption.jfxButtonOption(updateBtn,"fa-edit", Color.DARKGREEN);
    }

    @FXML
    void delete(){
        if(new AlertMessage("Êtes-vous sûr de supprimer ?").confirmMessage()) {
            ApiClient.delete("http://192.168.7.2:8080/responsible/delete?id=" + dto.getId());
            refreshList();
        }
    }

    @FXML
    void update(){
        Constants.action = ActionDatabase.UPDATE;
        notifyDataUpdate(String.valueOf(dto.getId()),dto.getLastName(),dto.getFirstName(),
                dto.getPhoneNumber(),dto.getCin(),dto.getAddress(),dto.getRole());
    }

    @FXML
    void authorisation_OnAction(){
        boolean statusUser = authorisation.isSelected();
        String enableMessage = "Voulez-vous autoriser l'accées à cet utilisateur ?";
        String disableMessage = "Voulez-vous retirer l'accées de cet utilisateur ?";
        if (new AlertMessage(!authorisation.isSelected() ? disableMessage : enableMessage).confirmMessage()) {

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Vérification");
            dialog.setHeaderText("Veuillez entrer le mot de passe pour confirmer");
            dialog.setContentText("Mot de passe :");

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()
                    && passwordVerifier.verifyPassword(result.get(), Login.getConnected().getPassword())) {
                dto.setAuthorisation(statusUser);
                ApiClient.update("http://192.168.7.2:8080/responsible/update?id=" + dto.getId()+"&withPassword="+false, dto);
                new AlertMessage("Autorisation mise à jour avec succès.").information();
            } else {
                authorisation.setSelected(!statusUser);
                new AlertMessage("Mot de passe incorrect ou annulé.").error();
            }
        } else
            authorisation.setSelected(!statusUser);
    }

    public void notifyDataUpdate(String id, String lastNameData, String firstNameData,String phoneData,
                                 String cinData, String addressData, UserRoleType role) {
        if (listener != null) {
            listener.setInformationUpdate(id, lastNameData, firstNameData,phoneData,
                    cinData, addressData, role);
        }
    }

    public void refreshList(){
        if (listener != null) {
            listener.refreshTable();
        }
    }
}
