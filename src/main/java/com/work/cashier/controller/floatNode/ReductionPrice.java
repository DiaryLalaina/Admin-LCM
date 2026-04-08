package com.work.cashier.controller.floatNode;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.work.cashier.api.ApiClient;
import com.work.cashier.data_transfert_object.customer.CustomerDTO;
import com.work.cashier.data_transfert_object.customer.CustomerReductionDTO;
import com.work.cashier.data_transfert_object.product.ProductDTO;
import com.work.cashier.graphics.SwitchScene;
import com.work.cashier.mask.TextFieldMask;
import com.work.cashier.service.ControlsOption;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import lombok.Setter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ReductionPrice implements Initializable {

    @FXML
    private JFXButton hideBtn;

    @FXML
    private Label nameCustomer;

    @FXML
    private JFXComboBox<String> articleChoice;

    @FXML
    private JFXTextField price,reductionPrice;

    @FXML
    private JFXButton saveBtn;

    private final ControlsOption controlsOption = new ControlsOption();

    @Setter
    private CustomerDTO customerDTO;

    private ProductDTO productDTOClicked;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        TextFieldMask.onlyNumbers(reductionPrice);
        controlsOption.jfxButtonOption(hideBtn,"fa-times", Color.WHITE);
        controlsOption.jfxButtonOption(saveBtn,"fa-check",Color.GREEN);
        String urlProduct = "http://192.168.7.2:8080/product/getNames";
        List<String> products = ApiClient.getListString(urlProduct);
        articleChoice.getItems().addAll(products);
        articleChoice.setValue(products.getFirst());

        Platform.runLater(()->{
            nameCustomer.setText(customerDTO.getLastName().toUpperCase()+" "+customerDTO.getFirstName());
            articleSelected();
        });
    }

    @FXML
    void articleSelected() {
        String url = "http://192.168.7.2:8080/product/getByNameWithReduction?name=" + articleChoice.getValue()
                    + "&idCustomer=" + customerDTO.getId();
        System.out.println(url);
        productDTOClicked = ApiClient.getOneEntity(url, ProductDTO.class);
        assert productDTOClicked != null;
        price.setText(productDTOClicked.getPrice()+" Ar");
    }

    @FXML
    void close() {
        ClientFile.filReductionInfo();
        new SwitchScene().closeFloatScene(2);
    }

    @FXML
    void save() {
        if(!reductionPrice.getText().isBlank()) {
            CustomerReductionDTO dto = new CustomerReductionDTO();
            dto.setIdProduct(productDTOClicked.getId());
            dto.setIdCustomer(customerDTO.getId());
            dto.setPrice(Integer.parseInt(reductionPrice.getText()));
            ApiClient.insert("http://192.168.7.2:8080/reduction/save",dto);
            close();
        }
    }
}
