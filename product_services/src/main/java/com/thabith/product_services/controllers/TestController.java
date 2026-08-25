package com.thabith.product_services.controllers;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public String greeting(){
        return "Hello test world";
    }
}
