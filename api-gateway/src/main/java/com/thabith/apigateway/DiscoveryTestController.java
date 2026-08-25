package com.thabith.apigateway;


import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DiscoveryTestController {
    private final DiscoveryClient discoveryClient;

    public DiscoveryTestController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @GetMapping("/debug/services")
    public List<String> getServices() {
        return discoveryClient.getServices();
    }
}
