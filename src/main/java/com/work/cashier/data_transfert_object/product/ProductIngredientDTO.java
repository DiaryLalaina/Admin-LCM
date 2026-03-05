package com.work.cashier.data_transfert_object.product;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductIngredientDTO {

    private Long id,idProduct,idIngredient;

    private String ingredientName;

    private Double quantityRequired;

}
