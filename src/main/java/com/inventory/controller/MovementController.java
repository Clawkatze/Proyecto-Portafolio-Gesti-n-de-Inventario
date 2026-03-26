package com.inventory.controller;

import com.inventory.dto.request.MovementRequest;
import com.inventory.dto.response.MovementResponse;
import com.inventory.service.MovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
public class MovementController {

    private final MovementService movementService;

    @GetMapping
    public ResponseEntity<List<MovementResponse>> getAll() {
        return ResponseEntity.ok(movementService.getAll());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<MovementResponse>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(movementService.getByProduct(productId));
    }

    @PostMapping
    public ResponseEntity<MovementResponse> register(@Valid @RequestBody MovementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movementService.register(request));
    }
}
