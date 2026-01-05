package com.example.servicecar;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableDiscoveryClient
public class CarApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarApplication.class, args);
    }

    @Bean
    @org.springframework.cloud.client.loadbalancer.LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    CommandLineRunner commandLineRunner(CarRepository carRepository) {
        return args -> {
            carRepository.save(new Car(null, "Toyota", "Corolla", 1L));
            carRepository.save(new Car(null, "Renault", "Clio", 2L));
            carRepository.save(new Car(null, "Peugeot", "208", 1L));
        };
    }
}

@Entity
@Data
@jakarta.persistence.Table(name = "cars")
class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String marque;
    private String model;
    private Long client_id;

    public Car() {
    }

    public Car(Long id, String marque, String model, Long client_id) {
        this.id = id;
        this.marque = marque;
        this.model = model;
        this.client_id = client_id;
    }

    public Long getId() {
        return id;
    }

    public String getMarque() {
        return marque;
    }

    public String getModel() {
        return model;
    }

    public Long getClient_id() {
        return client_id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setClient_id(Long client_id) {
        this.client_id = client_id;
    }
}

interface CarRepository extends JpaRepository<Car, Long> {
}

@NoArgsConstructor
class Client {
    private Long id;
    private String nom;
    private Float age;

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public Float getAge() {
        return age;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setAge(Float age) {
        this.age = age;
    }
}

@Data
@NoArgsConstructor
class CarResponse {
    private Long id;
    private String marque;
    private String model;
    private Client client;

    public CarResponse(Long id, String marque, String model, Client client) {
        this.id = id;
        this.marque = marque;
        this.model = model;
        this.client = client;
    }

    public Long getId() {
        return id;
    }

    public String getMarque() {
        return marque;
    }

    public String getModel() {
        return model;
    }

    public Client getClient() {
        return client;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}

@RestController
@RequestMapping("/api/cars")
class CarController {

    private final CarRepository carRepository;
    private final WebClient webClient;

    public CarController(CarRepository carRepository, WebClient.Builder webClientBuilder) {
        this.carRepository = carRepository;
        this.webClient = webClientBuilder.baseUrl("http://service-client").build();
    }

    @GetMapping
    public List<CarResponse> findAll() {
        List<Car> cars = carRepository.findAll();
        return cars.stream()
                .map(this::mapToCarResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CarResponse findById(@PathVariable Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found"));
        return mapToCarResponse(car);
    }

    private CarResponse mapToCarResponse(Car car) {
        Client client = webClient.get()
                .uri("/api/clients/" + car.getClient_id())
                .retrieve()
                .bodyToMono(Client.class)
                .block();

        return new CarResponse(car.getId(), car.getMarque(), car.getModel(), client);
    }
}
