package com.thabith.product_services.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductImage {

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "public_id")
    private String publicId;
}
