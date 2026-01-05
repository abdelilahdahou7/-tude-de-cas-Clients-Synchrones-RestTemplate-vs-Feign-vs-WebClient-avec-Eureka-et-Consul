package com.example.servicecar;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "inventory-service")
public interface InventoryFeignClient {

    @GetMapping("/api/clients")
    List<Client> findAll();

    @GetMapping("/api/clients/{id}")
    Client findById(@PathVariable("id") Long id);
}
