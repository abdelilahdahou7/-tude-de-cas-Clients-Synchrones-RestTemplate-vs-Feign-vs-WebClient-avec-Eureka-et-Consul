package com.example.servicecar;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
@RequestMapping("/api/client-test")
public class ClientCommunicationController {

    private final InventoryFeignClient feignClient;
    private final RestTemplate restTemplate;
    private final WebClient webClient;

    public ClientCommunicationController(InventoryFeignClient feignClient, RestTemplate restTemplate,
            WebClient.Builder webClientBuilder) {
        this.feignClient = feignClient;
        this.restTemplate = restTemplate;
        this.webClient = webClientBuilder.baseUrl("http://inventory-service").build();
    }

    @GetMapping("/rest-template/clients")
    public List<Client> findAllRestTemplate() {
        Client[] clients = restTemplate.getForObject("http://inventory-service/api/clients", Client[].class);
        return List.of(clients);
    }

    @GetMapping("/rest-template/clients/{id}")
    public Client findByIdRestTemplate(@PathVariable Long id) {
        return restTemplate.getForObject("http://inventory-service/api/clients/" + id, Client.class);
    }

    @GetMapping("/feign/clients")
    public List<Client> findAllFeign() {
        return feignClient.findAll();
    }

    @GetMapping("/feign/clients/{id}")
    public Client findByIdFeign(@PathVariable Long id) {
        return feignClient.findById(id);
    }

    @GetMapping("/web-client/clients")
    public List<Client> findAllWebClient() {
        return webClient.get()
                .uri("/api/clients")
                .retrieve()
                .bodyToFlux(Client.class)
                .collectList()
                .block();
    }

    @GetMapping("/web-client/clients/{id}")
    public Client findByIdWebClient(@PathVariable Long id) {
        return webClient.get()
                .uri("/api/clients/" + id)
                .retrieve()
                .bodyToMono(Client.class)
                .block();
    }
}
