package com.thabith.authservice.client;

import com.thabith.authservice.dto.AuthResponse;
import com.thabith.authservice.dto.UserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient("USER-SERVICE")
public interface AuthClient {
    @PostMapping("/api/v1/user/auth")
    AuthResponse credentials(@RequestParam("email") String email);


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, value = "/api/v1/user/register")
    AuthResponse saveUser(@RequestPart("user") UserRequest user, @RequestPart(value = "profile", required = false) MultipartFile multipartFile);

}
