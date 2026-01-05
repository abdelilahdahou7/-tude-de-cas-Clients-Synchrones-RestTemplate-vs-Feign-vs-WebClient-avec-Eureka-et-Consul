# TP21: Microservices Architecture with Spring WebClient

This project demonstrates a microservices architecture using Spring Boot, Spring Cloud Netflix Eureka, and Spring WebFlux (WebClient) for inter-service communication.

## Architecture Overview

The system consists of three main components:

1.  **Eureka Server** (`eureka-server`): Acts as the service registry where all microservices register themselves.
2.  **Service Client** (`service-client`): A microservice that manages `Client` entities.
3.  **Service Car** (`service-car`): A microservice that manages `Car` entities and fetches associated client details from `service-client` using `WebClient`.

## Technologies Used

-   **Spring Boot 3.2.3**
-   **Spring Cloud 2023.0.0**
-   **Netflix Eureka Server/Client**
-   **Spring WebFlux (WebClient)**
-   **Spring Data JPA**
-   **H2 Database**
-   **Lombok**

## Project Structure

The repository is organized into three separate modules (available as branches):

-   `eureka-server`: Configuration for the Eureka naming server.
-   `service-client`: Implementation of the Client service.
-   `service-car`: Implementation of the Car service (Consumer).

## Getting Started

### Prerequisites

-   Java 17
-   Maven

### Running the Services

1.  **Start Eureka Server**:
    ```bash
    cd eureka-server
    mvn spring-boot:run
    ```
    Access Dashboard: [http://localhost:8761](http://localhost:8761)

2.  **Start Service Client**:
    ```bash
    cd service-client
    mvn spring-boot:run
    ```
    API: [http://localhost:8081/api/clients](http://localhost:8081/api/clients)

3.  **Start Service Car**:
    ```bash
    cd service-car
    mvn spring-boot:run
    ```
    API: [http://localhost:8082/api/cars](http://localhost:8082/api/cars)

## Verification

To verify the inter-service communication, make a GET request to `service-car`:

```bash
curl http://localhost:8082/api/cars
```

**Expected JSON Output:**

```json
[
  {
    "id": 1,
    "marque": "Toyota",
    "model": "Corolla",
    "client": {
      "id": 1,
      "nom": "Amine",
      "age": 23.0
    }
  },
  ...
]
```

## Key Implementations

### WebClient Configuration (Service Car)

The `WebClient` is configured with `@LoadBalanced` to resolve service names (e.g., `http://service-client`).

```java
@Bean
@LoadBalanced
public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
}
```

### Fetching Data

```java
Client client = webClient.get()
        .uri("http://service-client/api/clients/" + car.getClient_id())
        .retrieve()
        .bodyToMono(Client.class)
        .block();
```

## Author
Abdelilah Dahou
