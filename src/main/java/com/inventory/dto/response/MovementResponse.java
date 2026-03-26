package com.inventory.dto.response;

import com.inventory.model.MovementType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovementResponse {
    private Long id;
    private String productName;
    private String productSku;
    private MovementType type;
    private Integer quantity;
    private String reason;
    private String createdBy;
    private LocalDateTime createdAt;
}
