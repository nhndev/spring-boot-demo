package com.nhn.demo.dto.response.product;

import com.nhn.demo.dto.response.category.CategoryDetailResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailResponse {
    private Integer id;

    private String name;

    private Integer price;

    private Integer discount;

    private String imageUrl;

    private String cloudinaryImageId;

    private CategoryDetailResponse category;
}
