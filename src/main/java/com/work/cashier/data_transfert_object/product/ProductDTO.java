package com.work.cashier.data_transfert_object.product;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ProductDTO {
    private Long id;

    private int price;

    private String name,createdAt;

    private List<ProductIngredientDTO> productIngredients;

    private ProductProvidedDTO providedProducts;

}
