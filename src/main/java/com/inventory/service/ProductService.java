package com.inventory.service;

import com.inventory.dto.request.ProductRequest;
import com.inventory.dto.response.ProductResponse;
import com.inventory.dto.response.StockAlertResponse;
import com.inventory.model.Product;
import com.inventory.repository.CategoryRepository;
import com.inventory.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public ProductResponse getBySku(String sku) {
        return productRepository.findBySku(sku)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Producto con SKU " + sku + " no encontrado"));
    }

    public List<ProductResponse> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductResponse> getByCategory(Long categoryId) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("El SKU ya existe: " + request.getSku());
        }
        var product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .sku(request.getSku())
                .price(request.getPrice())
                .stock(request.getStock())
                .lowStockThreshold(request.getLowStockThreshold())
                .active(true)
                .build();

        if (request.getCategoryId() != null) {
            var category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));
            product.setCategory(category);
        }
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        var product = findOrThrow(id);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setLowStockThreshold(request.getLowStockThreshold());

        if (request.getCategoryId() != null) {
            var category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));
            product.setCategory(category);
        }
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        var product = findOrThrow(id);
        product.setActive(false); // Soft delete
        productRepository.save(product);
    }

    public List<StockAlertResponse> getLowStockAlerts() {
        return productRepository.findLowStockProducts().stream()
                .map(p -> {
                    var alert = new StockAlertResponse();
                    alert.setProductId(p.getId());
                    alert.setProductName(p.getName());
                    alert.setSku(p.getSku());
                    alert.setCurrentStock(p.getStock());
                    alert.setThreshold(p.getLowStockThreshold());
                    return alert;
                })
                .toList();
    }

    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + id));
    }

    private ProductResponse toResponse(Product p) {
        var r = new ProductResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setSku(p.getSku());
        r.setPrice(p.getPrice());
        r.setStock(p.getStock());
        r.setLowStockThreshold(p.getLowStockThreshold());
        r.setLowStock(p.isLowStock());
        r.setActive(p.getActive());
        r.setCreatedAt(p.getCreatedAt());
        r.setUpdatedAt(p.getUpdatedAt());
        if (p.getCategory() != null) {
            r.setCategoryName(p.getCategory().getName());
        }
        return r;
    }
}
