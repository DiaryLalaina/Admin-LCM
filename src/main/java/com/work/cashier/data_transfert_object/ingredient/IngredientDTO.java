package com.work.cashier.data_transfert_object.ingredient;

import com.work.cashier.data_transfert_object.unitOption.Unit;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IngredientDTO {

    private Long id;

    private String provider,name;

    private String createdAt;

    private Unit unit;

    private double stockQuantity;

    public IngredientDTO(){}

}
