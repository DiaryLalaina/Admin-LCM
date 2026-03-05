package com.work.cashier.controller.infoTable;

import com.work.cashier.alert.AlertMessage;
import com.work.cashier.api.ApiClient;
import com.work.cashier.controller.floatNode.ClientFile;
import com.work.cashier.data_transfert_object.customer.CustomerReductionDTO;
import com.work.cashier.data_transfert_object.product.ProductDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ReductionInfo {

    @FXML
    private Label article,price;

    public void setData(CustomerReductionDTO dto){
        String url = "http://192.168.7.2:8080/product/"+ dto.getIdProduct();
        ProductDTO productDTO = ApiClient.getOneEntity(url, ProductDTO.class);
        assert productDTO != null;
        article.setId(dto.getId()+"");
        article.setText(productDTO.getName());
        price.setText(dto.getPrice()+" Ar");
    }

    @FXML
    void delete(){
        if(new AlertMessage("Êtes-vous sûr de supprimer ?").confirmMessage()){
            String url = "http://192.168.7.2:8080/reduction/delete?id="+article.getId();
            ApiClient.delete(url);
            ClientFile.filReductionInfo();
        }
    }

}
