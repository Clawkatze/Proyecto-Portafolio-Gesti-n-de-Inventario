package com.inventory.service;

import com.inventory.dto.request.MovementRequest;
import com.inventory.dto.response.MovementResponse;
import com.inventory.model.MovementType;
import com.inventory.model.StockMovement;
import com.inventory.model.User;
import com.inventory.repository.MovementRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovementService {

    private final MovementRepository movementRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<MovementResponse> getAll() {
        return movementRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<MovementResponse> getByProduct(Long productId) {
        return movementRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public MovementResponse register(MovementRequest request) {
        var product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        if (request.getType() == MovementType.ENTRY) {
            product.setStock(product.getStock() + request.getQuantity());
        } else {
            if (product.getStock() < request.getQuantity()) {
                throw new IllegalStateException(
                        "Stock insuficiente. Disponible: " + product.getStock()
                );
            }
            product.setStock(product.getStock() - request.getQuantity());
        }
        productRepository.save(product);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email).orElse(null);

        var movement = StockMovement.builder()
                .product(product)
                .type(request.getType())
                .quantity(request.getQuantity())
                .reason(request.getReason())
                .createdBy(currentUser)
                .build();

        return toResponse(movementRepository.save(movement));
    }

    private MovementResponse toResponse(StockMovement m) {
        var r = new MovementResponse();
        r.setId(m.getId());
        r.setProductName(m.getProduct().getName());
        r.setProductSku(m.getProduct().getSku());
        r.setType(m.getType());
        r.setQuantity(m.getQuantity());
        r.setReason(m.getReason());
        r.setCreatedAt(m.getCreatedAt());
        if (m.getCreatedBy() != null) {
            r.setCreatedBy(m.getCreatedBy().getName());
        }
        return r;
    }
}
