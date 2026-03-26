package com.inventory.repository;

import com.inventory.model.MovementType;
import com.inventory.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByProductIdOrderByCreatedAtDesc(Long productId);
    List<StockMovement> findByTypeOrderByCreatedAtDesc(MovementType type);
}
