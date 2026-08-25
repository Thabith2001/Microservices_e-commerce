package com.thabith.userservice.service;

import com.thabith.userservice.Exceptions.UserExistException;
import com.thabith.userservice.dto.AuthResponse;
import com.thabith.userservice.dto.CloudinaryImage;
import com.thabith.userservice.dto.UserRequest;
import com.thabith.userservice.dto.UserResponse;
import com.thabith.userservice.enums.Role;
import com.thabith.userservice.model.Users;
import com.thabith.userservice.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UsersService {

    private final UserRepo userRepo;
    private final CloudinaryService cloudinaryService;


    public UserResponse addUser(UserRequest request, MultipartFile multipartFile) {

        Optional<Users> us = userRepo.findByEmail(request.getEmail());
        if (us.isPresent()) {
            throw new UserExistException("THIS USER EMAIL ID IS ALREADY EXIST.");
        }

        CloudinaryImage uri = cloudinaryService.uploadImage(multipartFile);
        Users saveUser = setUser(request, uri, new Users());
        Users user = userRepo.save(saveUser);
        return mapToResponse(user);
    }

    public UserResponse updateProfile(long id, MultipartFile multipartFile) {

        Users user = userRepo.findById(id)
                .orElseThrow(() ->
                        new UserExistException("USER NOT FOUND.")
                );

        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("Profile image is required.");
        }

        if (user.getProfileId() != null && !user.getProfileId().isBlank()) {
            cloudinaryService.deleteImage(user.getProfileId());
        }

        CloudinaryImage image =
                cloudinaryService.uploadImages(multipartFile);

        if (image == null || image.url() == null) {
            throw new RuntimeException("Failed to upload profile image.");
        }

        user.setProfileUri(image.url());
        user.setProfileId(image.publicId());

        Users updatedUser = userRepo.save(user);

        return mapToResponse(updatedUser);
    }

    public Page<UserResponse> findAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Users> us = userRepo.findAll(pageable);
        return us.map(this::mapToResponse);

    }

    public UserResponse findById(long id) {
        Users user = userRepo.findById(id).orElseThrow(() -> new UserExistException("USER NOT FOUND."));
        return mapToResponse(user);
    }


    public UserResponse findByText(String text) {
        Users user = userRepo.searchUsers(text).orElseThrow(() -> new UserExistException("USER NOT FOUND."));
        return mapToResponse(user);
    }

    public String delete(long id) {
        Users user = userRepo.findById(id).orElseThrow(() -> new UserExistException("USER NOT FOUND"));
        userRepo.deleteById(user.getId());
        return "Successfully deleted";
    }

    public AuthResponse getUserCredentials(String email) {

        Users user = userRepo.findByEmail(email).orElseThrow(
                () -> new UserExistException("USER NOT FOUND")
        );


        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }


    private Users setUser(UserRequest request, CloudinaryImage uri, Users user) {
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setContact(request.getContact());
        user.setPassword(request.getPassword());
        user.setProfileUri(uri.url());
        user.setProfileId(uri.publicId());
        user.setRole(request.getRole());
        user.setRole(request.getRole() != null ? request.getRole() : Role.USER);

        return user;
    }


    private UserResponse mapToResponse(Users user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setFirstname(user.getFirstname());
        response.setLastname(user.getLastname());
        response.setEmail(user.getEmail());
        response.setContact(user.getContact());
        response.setProfileUri(user.getProfileUri());
        response.setRole(user.getRole());
        return response;
    }
}
