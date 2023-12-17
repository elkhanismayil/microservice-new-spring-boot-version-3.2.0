package com.programmintechie.order.external.feign;

import com.programmintechie.order.external.response.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@FeignClient(name = "inventory-service", path = "/api/inventory")
public interface InventoryService {

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    List<InventoryResponse> isInStock(@RequestParam List<String> skuCodes);
}
