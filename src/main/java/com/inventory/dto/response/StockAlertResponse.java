package com.inventory.dto.response;

import lombok.Data;

@Data
public class StockAlertResponse {
    private Long productId;
    private String productName;
    private String sku;
    private Integer currentStock;
    private Integer threshold;
}
