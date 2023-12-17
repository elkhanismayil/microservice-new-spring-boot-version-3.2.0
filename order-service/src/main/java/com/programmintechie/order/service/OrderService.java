package com.programmintechie.order.service;

import com.programmintechie.order.dto.OrderLineItemsDto;
import com.programmintechie.order.dto.OrderRequest;
import com.programmintechie.order.external.feign.InventoryService;
import com.programmintechie.order.model.Order;
import com.programmintechie.order.model.OrderLineItems;
import com.programmintechie.order.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.Operations;
import org.apache.hc.core5.concurrent.CompletedFuture;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final Tracer tracer;

    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsListDto().stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItems);
        List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();
        log.info("Before calling inventory service");
        Span span = tracer.nextSpan().name("InventoryServiceLookup");
        try (Tracer.SpanInScope ws = tracer.withSpan(span.start())) {
            inventoryService.isInStock(skuCodes).forEach(inventoryResponse -> {
                if (!inventoryResponse.isInStock()) {
                    throw new RuntimeException("Out of stock for : " + inventoryResponse.getSkuCode());
                }
                orderRepository.save(order);
            });
        } finally {
            span.end();
        }

        return "Order placed successfully";
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        return OrderLineItems.builder()
                .skuCode(orderLineItemsDto.getSkuCode())
                .price(orderLineItemsDto.getPrice())
                .quantity(orderLineItemsDto.getQuantity())
                .build();
    }

    private String fallbackMethod(OrderRequest orderRequest, RuntimeException e) {
        return "Oops! Something went wrong while placing order. Please try again later.";
    }
}
