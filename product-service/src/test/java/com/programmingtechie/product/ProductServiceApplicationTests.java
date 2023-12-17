package com.programmingtechie.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmingtechie.product.dto.ProductRequest;
import com.programmingtechie.product.dto.ProductResponse;
import com.programmingtechie.product.model.Product;
import com.programmingtechie.product.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Jackson2ObjectMapperBuilder builder;

    @Autowired
    private ProductRepository productRepository;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest request = getProductRequest();
        ObjectMapper mapper = builder.build();
        String json = mapper.writeValueAsString(request);
        mockMvc.perform(post("/api/product")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated());

        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/product"))
                .andExpect(status().isOk());

        productRepository.findAll().forEach(product -> {
            assertNotNull(product.getId());
            assertNotNull(product.getName());
            assertNotNull(product.getDescription());
            assertNotNull(product.getPrice());
        });
    }

    @Test
    void shouldGetProductById() throws Exception {
//        Product product = productRepository.findById("6570795b4a9cc51eb5acff3e").orElse(new Product());
//
//        mockMvc.perform(get("/api/product/" + product.getId())
//                        .contentType("application/json"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(product.getId()))
//                .andExpect(jsonPath("$.name").value(product.getName()))
//                .andExpect(jsonPath("$.description").value(product.getDescription()))
//                .andExpect(jsonPath("$.price").value(product.getPrice()));

//        productRepository.findById(response.getId())
//                .ifPresent(product -> {
//                    assertEquals(response.getId(), product.getId());
//                    assertEquals(response.getName(), product.getName());
//                    assertEquals(response.getDescription(), product.getDescription());
//                    assertEquals(response.getPrice(), product.getPrice());
//                });
    }

    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                .name("iPhone 14")
                .description("Apple iPhone")
                .price(BigDecimal.valueOf(1200))
                .build();
    }

}
