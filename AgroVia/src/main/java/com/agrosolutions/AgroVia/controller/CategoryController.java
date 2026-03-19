package com.agrosolutions.AgroVia.controller;
import com.agrosolutions.AgroVia.dto.ApiResponse;
import com.agrosolutions.AgroVia.dto.CategorySummary;

import com.agrosolutions.AgroVia.entity.Resource;
import com.agrosolutions.AgroVia.entity.ResourceCategory;
import com.agrosolutions.AgroVia.repository.ResourceCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {
    @Autowired
    private ResourceCategoryRepository categoryRepository;

    // Get all categories (public)
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<CategorySummary>>> getAllCategories() {
        List<ResourceCategory> categories = categoryRepository.findAllByOrderByNameAsc();

        List<CategorySummary> summaries = categories.stream()
                .map(cat -> new CategorySummary(
                        cat.getId(),
                        cat.getName(),
                        cat.getIcon(),
                        (long) cat.getResources().size()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(summaries));
    }

    // Get categories with available resources (public)
    @GetMapping("/available")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<CategorySummary>>> getCategoriesWithAvailableResources() {
        List<ResourceCategory> categories = categoryRepository.findCategoriesWithAvailableResources();

        List<CategorySummary> summaries = categories.stream()
                .map(cat -> new CategorySummary(
                        cat.getId(),
                        cat.getName(),
                        cat.getIcon(),
                        cat.getResources().stream()
                                .filter(r -> r.getStatus() == Resource.ResourceStatus.AVAILABLE)
                                .count()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(summaries));
    }
}
