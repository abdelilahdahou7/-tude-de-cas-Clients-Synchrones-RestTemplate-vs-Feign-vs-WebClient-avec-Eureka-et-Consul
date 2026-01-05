# Case Study: Synchronous Clients (Eureka & Consul)

## Overview
This case study compares three synchronous client implementations in a Spring Boot microservices architecture:
1.  **RestTemplate**: Traditional synchronous client (Maintenance mode).
2.  **OpenFeign**: Declarative REST client, simplifies code.
3.  **WebClient**: Non-blocking, reactive client (Modern approach).

The architecture uses **Eureka** (initially) and **Consul** (migration) for Service Discovery.

## Architecture
-   **inventory-service** (Provider):
    -   Port: `8081`
    -   Exposes: `/api/clients`
    -   Database: H2 (In-memory)
-   **service-client** (Consumer):
    -   Port: `8082`
    -   Consumes: `inventory-service` using the 3 clients.
    -   Test Endpoints:
        -   `/api/client-test/rest-template/clients`
        -   `/api/client-test/feign/clients`
        -   `/api/client-test/web-client/clients`

## 1. Implementation Details

### RestTemplate
Configured with `@LoadBalanced` to resolve service names from Eureka/Consul.
```java
@Bean
@LoadBalanced
public RestTemplate restTemplate() {
    return new RestTemplate();
}
// Usage: restTemplate.getForObject("http://inventory-service/api/clients", ...);
```

### OpenFeign
Declarative interface approach.
```java
@FeignClient(name = "inventory-service")
public interface InventoryFeignClient {
    @GetMapping("/api/clients")
    List<Client> findAll();
}
```

### WebClient
Reactive stack, configured with `@LoadBalanced`.
```java
@Bean
@LoadBalanced
public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
}
// Usage: webClient.get().uri("http://inventory-service/api/clients")...
```

## 2. Service Discovery Migration (Eureka -> Consul)

### Eureka Configuration
-   Dependency: `spring-cloud-starter-netflix-eureka-client`
-   Config: `eureka.client.service-url.defaultZone=http://localhost:8761/eureka`

### Consul Configuration
-   Dependency: `spring-cloud-starter-consul-discovery`
-   Container: `hashicorp/consul`
-   Config:
    ```yaml
    spring:
      cloud:
        consul:
          host: localhost
          port: 8500
          discovery:
            service-name: inventory-service
    ```

## 3. Testing & Verification

### Verification
Endpoints were verified successfully via Browser/Curl during the implementation phase, returning JSON data for all 3 clients.

### Performance Testing Strategy
Scripts are provided in `tests/`:
-   `benchmark.py`: Simple Python script to measure latency.
-   `test_plan.jmx`: JMeter Test Plan for concurrent load testing.

**Expected Observations:**
-   **RestTemplate**: Blocking, simple, good for low concurrency.
-   **Feign**: Slight overhead due to abstraction, but very clean code.
-   **WebClient**: Best performance under high load due to non-blocking nature (though used synchronously here, it scales better).

## 4. Resilience
To test resilience:
1.  Stop `inventory-service`.
2.  Call endpoints.
3.  Observe behavior:
    -   RestTemplate/Feign: Throws Exception (500).
    -   Resilience4j (if added) would handle fallbacks.

## Conclusion
Migrating from Eureka to Consul is straightforward with Spring Cloud. 
-   **Feign** offers the best developer experience.
-   **WebClient** offers the best performance potential.
-   **Consul** provides a robust alternative to Eureka with additional features like Key/Value store.
