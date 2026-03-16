package com.agrosolutions.AgroVia.controller;
import com.agrosolutions.AgroVia.dto.*;
import com.agrosolutions.AgroVia.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/resources")
@CrossOrigin(origins = "*")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    // Create new resource listing
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResourceResponse>> createResource(
            @Valid @RequestBody ResourceRequest request) {
        ResourceResponse response = resourceService.createResource(request);
        return new ResponseEntity<>(
                ApiResponse.success("Resource listed successfully", response),
                HttpStatus.CREATED
        );
    }

    // Get all available resources (public)
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<Page<ResourceSummary>>> getAllAvailableResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ResourceSummary> resources = resourceService.getAllAvailableResources(pageable);
        return ResponseEntity.ok(ApiResponse.success(resources));
    }

    // Get resource by ID (public)
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<ResourceResponse>> getResourceById(@PathVariable Long id) {
        ResourceResponse response = resourceService.getResourceById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Update resource
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResourceResponse>> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody ResourceRequest request) {
        ResourceResponse response = resourceService.updateResource(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Resource updated successfully", response)
        );
    }

    // Delete resource
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok(
                ApiResponse.success("Resource deleted successfully", null)
        );
    }

    // Update resource status
    @PatchMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResourceResponse>> updateResourceStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        ResourceResponse response = resourceService.updateResourceStatus(id, status);
        return ResponseEntity.ok(
                ApiResponse.success("Resource status updated successfully", response)
        );
    }

    // Get current user's resources
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ResourceSummary>>> getMyResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ResourceSummary> resources = resourceService.getMyResources(pageable);
        return ResponseEntity.ok(ApiResponse.success(resources));
    }

    // Search resources with filters (public)
    @PostMapping("/search")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<Page<ResourceSummary>>> searchResources(
            @RequestBody ResourceSearchRequest request) {
        Page<ResourceSummary> resources = resourceService.searchResources(request);
        return ResponseEntity.ok(ApiResponse.success(resources));
    }

    // Add image to resource
    @PostMapping("/{resourceId}/images")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResourceImageResponse>> addImage(
            @PathVariable Long resourceId,
            @RequestParam String imageUrl,
            @RequestParam(defaultValue = "false") boolean isPrimary) {
        ResourceImageResponse response = resourceService.addImage(resourceId, imageUrl, isPrimary);
        return new ResponseEntity<>(
                ApiResponse.success("Image added successfully", response),
                HttpStatus.CREATED
        );
    }

    // Remove image
    @DeleteMapping("/{resourceId}/images/{imageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> removeImage(
            @PathVariable Long resourceId,
            @PathVariable Long imageId) {
        resourceService.removeImage(resourceId, imageId);
        return ResponseEntity.ok(
                ApiResponse.success("Image removed successfully", null)
        );
    }

    // Set primary image
    @PutMapping("/{resourceId}/images/{imageId}/primary")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> setPrimaryImage(
            @PathVariable Long resourceId,
            @PathVariable Long imageId) {
        resourceService.setPrimaryImage(resourceId, imageId);
        return ResponseEntity.ok(
                ApiResponse.success("Primary image updated successfully", null)
        );
    }
}
