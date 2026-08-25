package com.thabith.product_services.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.thabith.product_services.dto.CloudinaryImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;


    public CloudinaryImage uploadImage(MultipartFile file) {

        try {

            Map<?, ?> uploadResult =
                    cloudinary.uploader().upload(
                            file.getBytes(),
                            ObjectUtils.asMap(
                                    "asset_folder", "products",
                                    "resource_type", "image"
                            )
                    );

            String imageUrl =
                    uploadResult.get("secure_url").toString();

            String publicId =
                    uploadResult.get("public_id").toString();

            return new CloudinaryImage(
                    imageUrl,
                    publicId
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to upload image: "
                            + file.getOriginalFilename(),
                    e
            );
        }
    }


    public List<CloudinaryImage> uploadImages(
            List<MultipartFile> images
    ) {

        List<CloudinaryImage> uploadedImages =
                new ArrayList<>();

        for (MultipartFile image : images) {

            if (!image.isEmpty()) {

                uploadedImages.add(
                        uploadImage(image)
                );
            }
        }

        return uploadedImages;
    }


    public void deleteImage(String publicId) {

        try {

            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "invalidate", true
                    )
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to delete Cloudinary image: "
                            + publicId,
                    e
            );
        }
    }
}