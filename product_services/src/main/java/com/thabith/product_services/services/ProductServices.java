package com.thabith.product_services.services;

import com.thabith.product_services.dto.CloudinaryImage;
import com.thabith.product_services.dto.ProductRequest;
import com.thabith.product_services.dto.ProductResponse;

import com.thabith.product_services.model.Brand;
import com.thabith.product_services.model.Category;
import com.thabith.product_services.model.ProductImage;
import com.thabith.product_services.model.Products;

import com.thabith.product_services.repo.BrandRepo;
import com.thabith.product_services.repo.CategoryRepo;
import com.thabith.product_services.repo.ProductsRepo;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductServices {

    private final ProductsRepo productsRepo;
    private final CategoryRepo categoryRepo;
    private final BrandRepo brandRepo;
    private final CloudinaryService cloudinaryService;


    public Page<ProductResponse> allProducts(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return productsRepo.allProducts(pageable).map(this::mapToResponse);
    }


    public List<ProductResponse> findByText(String text) {

        return productsRepo.findByText(text).stream().map(this::mapToResponse).toList();
    }

    public ProductResponse findById(long id) {

        Products product = productsRepo.findById(id).orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        return mapToResponse(product);
    }

    public ProductResponse addProducts(ProductRequest request, List<MultipartFile> images) {

        Products existingProduct = productsRepo.findByModelNumber(request.getModelNumber());

        if (existingProduct != null) {

            existingProduct.setQty(existingProduct.getQty() + request.getQty());

            Products savedProduct = productsRepo.save(existingProduct);

            return mapToResponse(savedProduct);
        }

        if (images == null || images.isEmpty()) {
            throw new RuntimeException("At least one product image is required");
        }

        List<ProductImage> productImages = uploadProductImages(images);

        Category category = getCategory(request.getCategory());
        Brand brand = getBrand(request.getBrand());


        Products product = Products.builder()

                .productName(request.getProductName())

                .images(productImages)

                .category_id(category)

                .brand_id(brand)

                .qty(request.getQty())

                .price(request.getPrice())

                .modelNumber(request.getModelNumber())

                .build();


        Products savedProduct = productsRepo.save(product);


        return mapToResponse(savedProduct);
    }

    public ProductResponse update(ProductRequest request, long id, List<MultipartFile> images) {

        Products product = productsRepo.findById(id).orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setProductName(request.getProductName());
        product.setPrice(request.getPrice());

        product.setCategory_id(getCategory(request.getCategory()));

        product.setBrand_id(getBrand(request.getBrand()));

        product.setQty(request.getQty());

        if (images != null && !images.isEmpty()) {

            List<ProductImage> oldImages = new ArrayList<>(product.getImages());

            List<ProductImage> newImages = uploadProductImages(images);

            product.setImages(new ArrayList<>(newImages));

            Products updatedProduct = productsRepo.save(product);

            for (ProductImage oldImage : oldImages) {

                if (oldImage.getPublicId() != null && !oldImage.getPublicId().isBlank()) {

                    cloudinaryService.deleteImage(oldImage.getPublicId());
                }
            }

            return mapToResponse(updatedProduct);
        }

        Products updatedProduct = productsRepo.save(product);

        return mapToResponse(updatedProduct);
    }

    public boolean delete(long id) {

        Products product = productsRepo.findById(id).orElse(null);


        if (product == null) {
            return false;
        }

        if (product.getQty() > 1) {

            product.setQty(product.getQty() - 1);

            productsRepo.save(product);

            return true;
        }

        List<ProductImage> oldImages = List.copyOf(product.getImages());

        productsRepo.delete(product);

        deleteCloudinaryImages(oldImages);
        return true;
    }


    private List<ProductImage> uploadProductImages(List<MultipartFile> images) {

        List<CloudinaryImage> uploadedImages = cloudinaryService.uploadImages(images);


        return uploadedImages.stream().map(image -> new ProductImage(image.url(), image.publicId())).toList();
    }


    private void deleteCloudinaryImages(List<ProductImage> images) {

        if (images == null || images.isEmpty()) {
            return;
        }


        for (ProductImage image : images) {

            if (image.getPublicId() != null && !image.getPublicId().isBlank()) {

                cloudinaryService.deleteImage(image.getPublicId());
            }
        }
    }

    private Brand getBrand(String brandName) {

        Brand brand = brandRepo.findByName(brandName);

        if (brand != null) {
            return brand;
        }

        Brand newBrand = new Brand();

        newBrand.setName(brandName);

        return brandRepo.save(newBrand);
    }

    private Category getCategory(String categoryName) {

        Category category = categoryRepo.findByName(categoryName);


        if (category != null) {
            return category;
        }


        Category newCategory = new Category();

        newCategory.setName(categoryName);


        return categoryRepo.save(newCategory);
    }

    private ProductResponse mapToResponse(Products product) {

        List<String> imageUrls = product.getImages().stream().map(ProductImage::getImageUrl).toList();


        return ProductResponse.builder()

                .id(product.getId())

                .productName(product.getProductName())

                .imageUris(imageUrls)

                .category(product.getCategory_id().getName())

                .brand(product.getBrand_id().getName())

                .price(product.getPrice())

                .qty(product.getQty())

                .inStock(product.getQty() > 0)

                .modelNumber(product.getModelNumber())

                .build();
    }
}