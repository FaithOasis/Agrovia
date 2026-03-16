package com.agrosolutions.AgroVia.service;

import com.agrosolutions.AgroVia.dto.*;
import com.agrosolutions.AgroVia.entity.*;
import com.agrosolutions.AgroVia.exception.BadRequestException;
import com.agrosolutions.AgroVia.exception.ResourceNotFoundException;
import com.agrosolutions.AgroVia.exception.UnauthorizedException;

import com.agrosolutions.AgroVia.repository.ResourceCategoryRepository;
import com.agrosolutions.AgroVia.repository.ResourceImageRepository;
import com.agrosolutions.AgroVia.repository.ResourceRepository;
import com.agrosolutions.AgroVia.repository.UserRepository;
import com.agrosolutions.AgroVia.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceCategoryRepository categoryRepository;

    @Autowired
    private ResourceImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    // Get current logged in user
    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // Create new resource listing
    @Transactional
    public ResourceResponse createResource(ResourceRequest request) {
        User currentUser = getCurrentUser();

        // Validate request
        validateResourceRequest(request);

        Resource resource = new Resource();
        resource.setTitle(request.getTitle());
        resource.setDescription(request.getDescription());
        resource.setOwner(currentUser);
        resource.setPricePerHour(request.getPricePerHour());
        resource.setPricePerDay(request.getPricePerDay());
        resource.setPricePerWeek(request.getPricePerWeek());
        resource.setDepositAmount(request.getDepositAmount());
        resource.setLocation(request.getLocation());
        resource.setLatitude(request.getLatitude());
        resource.setLongitude(request.getLongitude());
        resource.setDeliveryRadius(request.getDeliveryRadius());
        resource.setBrand(request.getBrand());
        resource.setModel(request.getModel());
        resource.setYear(request.getYear());
        resource.setCondition(request.getCondition());
        resource.setQuantityAvailable(request.getQuantityAvailable() != null ?
                request.getQuantityAvailable() : 1);
        resource.setMinRentalPeriod(request.getMinRentalPeriod());
        resource.setMaxRentalPeriod(request.getMaxRentalPeriod());
        resource.setAdvanceNotice(request.getAdvanceNotice());
        resource.setStatus(Resource.ResourceStatus.AVAILABLE);

        // Set category if provided
        if (request.getCategoryId() != null) {
            ResourceCategory category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            resource.setCategory(category);
        }

        Resource savedResource = resourceRepository.save(resource);

        // Send notification
        String message = "Your resource \"" + resource.getTitle() + "\" has been listed successfully.";
        notificationService.createSystemAnnouncement(message, currentUser);

        return mapToResourceResponse(savedResource);
    }

    // Validate resource request
    private void validateResourceRequest(ResourceRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Title is required");
        }

        boolean hasPrice = request.getPricePerHour() != null ||
                request.getPricePerDay() != null ||
                request.getPricePerWeek() != null;

        if (!hasPrice) {
            throw new BadRequestException("At least one price (hourly, daily, or weekly) must be provided");
        }

        if (request.getPricePerHour() != null && request.getPricePerHour().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Hourly price must be greater than zero");
        }

        if (request.getPricePerDay() != null && request.getPricePerDay().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Daily price must be greater than zero");
        }

        if (request.getPricePerWeek() != null && request.getPricePerWeek().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Weekly price must be greater than zero");
        }

        if (request.getDepositAmount() != null && request.getDepositAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Deposit amount cannot be negative");
        }
    }

    // Get all available resources
    public Page<ResourceSummary> getAllAvailableResources(Pageable pageable) {
        return resourceRepository.findByStatus(Resource.ResourceStatus.AVAILABLE, pageable)
                .map(this::mapToResourceSummary);
    }

    // Get resource by ID
    public ResourceResponse getResourceById(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));

        // Increment view count
        resource.setViewsCount(resource.getViewsCount() + 1);
        resourceRepository.save(resource);

        return mapToResourceResponse(resource);
    }

    // Update resource
    @Transactional
    public ResourceResponse updateResource(Long id, ResourceRequest request) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));

        User currentUser = getCurrentUser();
        if (!resource.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only update your own resources");
        }

        // Update fields
        resource.setTitle(request.getTitle());
        resource.setDescription(request.getDescription());
        resource.setPricePerHour(request.getPricePerHour());
        resource.setPricePerDay(request.getPricePerDay());
        resource.setPricePerWeek(request.getPricePerWeek());
        resource.setDepositAmount(request.getDepositAmount());
        resource.setLocation(request.getLocation());
        resource.setLatitude(request.getLatitude());
        resource.setLongitude(request.getLongitude());
        resource.setDeliveryRadius(request.getDeliveryRadius());
        resource.setBrand(request.getBrand());
        resource.setModel(request.getModel());
        resource.setYear(request.getYear());
        resource.setCondition(request.getCondition());
        resource.setQuantityAvailable(request.getQuantityAvailable());
        resource.setMinRentalPeriod(request.getMinRentalPeriod());
        resource.setMaxRentalPeriod(request.getMaxRentalPeriod());
        resource.setAdvanceNotice(request.getAdvanceNotice());

        // Update category if provided
        if (request.getCategoryId() != null) {
            ResourceCategory category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            resource.setCategory(category);
        }

        Resource updatedResource = resourceRepository.save(resource);
        return mapToResourceResponse(updatedResource);
    }

    // Delete resource
    @Transactional
    public void deleteResource(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));

        User currentUser = getCurrentUser();
        if (!resource.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only delete your own resources");
        }

        // Check for active bookings
        long activeBookings = resource.getBookings().stream()
                .filter(b -> b.getStatus() == ResourceBooking.BookingStatus.CONFIRMED ||
                        b.getStatus() == ResourceBooking.BookingStatus.ACTIVE)
                .count();

        if (activeBookings > 0) {
            throw new BadRequestException("Cannot delete resource with active bookings");
        }

        resource.setStatus(Resource.ResourceStatus.DELETED);
        resourceRepository.save(resource);
    }

    // Update resource status
    @Transactional
    public ResourceResponse updateResourceStatus(Long id, String status) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));

        User currentUser = getCurrentUser();
        if (!resource.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only update your own resources");
        }

        Resource.ResourceStatus newStatus = Resource.ResourceStatus.valueOf(status);
        resource.setStatus(newStatus);

        Resource updatedResource = resourceRepository.save(resource);
        return mapToResourceResponse(updatedResource);
    }

    // Get current user's resources
    public Page<ResourceSummary> getMyResources(Pageable pageable) {
        User currentUser = getCurrentUser();
        return resourceRepository.findByOwner(currentUser, pageable)
                .map(this::mapToResourceSummary);
    }

    // Search resources with filters
    public Page<ResourceSummary> searchResources(ResourceSearchRequest request) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                request.getPage(),
                request.getSize(),
                request.getSortDir().equalsIgnoreCase("desc") ?
                        org.springframework.data.domain.Sort.by(request.getSortBy()).descending() :
                        org.springframework.data.domain.Sort.by(request.getSortBy()).ascending()
        );

        // If location-based search
        if (request.getLatitude() != null && request.getLongitude() != null && request.getRadius() != null) {
            List<Resource> nearbyResources = resourceRepository.findResourcesWithinRadius(
                    request.getLatitude(), request.getLongitude(), request.getRadius());

            // Convert to page (simplified - in production, you'd want paginated radius search)
            List<ResourceSummary> summaries = nearbyResources.stream()
                    .map(this::mapToResourceSummary)
                    .collect(Collectors.toList());

            // This is a simplification - you'd need a custom paginated radius query
            return new org.springframework.data.domain.PageImpl<>(summaries, pageable, summaries.size());
        }

        // Regular search with filters
        Resource.ResourceStatus status = request.getStartDateTime() != null ?
                Resource.ResourceStatus.AVAILABLE : null;

        return resourceRepository.searchResources(
                request.getCategoryId(),
                request.getMinPrice(),
                request.getMaxPrice(),
                status,
                request.getLocation(),
                request.getSearchTerm(),
                pageable
        ).map(this::mapToResourceSummary);
    }

    // Add image to resource
    @Transactional
    public ResourceImageResponse addImage(Long resourceId, String imageUrl, boolean isPrimary) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        User currentUser = getCurrentUser();
        if (!resource.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only add images to your own resources");
        }

        int sortOrder = resource.getImages().size();

        ResourceImage image = new ResourceImage(resource, imageUrl, isPrimary, sortOrder);

        if (isPrimary) {
            imageRepository.resetPrimaryForResource(resource);
        }

        ResourceImage savedImage = imageRepository.save(image);

        return new ResourceImageResponse(
                savedImage.getId(),
                savedImage.getImageUrl(),
                savedImage.isPrimary(),
                savedImage.getSortOrder()
        );
    }

    // Remove image
    @Transactional
    public void removeImage(Long resourceId, Long imageId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        User currentUser = getCurrentUser();
        if (!resource.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only remove images from your own resources");
        }

        ResourceImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

        if (!image.getResource().getId().equals(resourceId)) {
            throw new BadRequestException("Image does not belong to this resource");
        }

        imageRepository.delete(image);
    }

    // Set primary image
    @Transactional
    public void setPrimaryImage(Long resourceId, Long imageId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        User currentUser = getCurrentUser();
        if (!resource.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only update images on your own resources");
        }

        ResourceImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

        if (!image.getResource().getId().equals(resourceId)) {
            throw new BadRequestException("Image does not belong to this resource");
        }

        imageRepository.resetPrimaryForResource(resource);
        image.setPrimary(true);
        imageRepository.save(image);
    }

    // Map Resource to ResourceResponse
    private ResourceResponse mapToResourceResponse(Resource resource) {
        ResourceResponse response = new ResourceResponse();
        response.setId(resource.getId());
        response.setTitle(resource.getTitle());
        response.setDescription(resource.getDescription());
        response.setPricePerHour(resource.getPricePerHour());
        response.setPricePerDay(resource.getPricePerDay());
        response.setPricePerWeek(resource.getPricePerWeek());
        response.setDepositAmount(resource.getDepositAmount());
        response.setLocation(resource.getLocation());
        response.setLatitude(resource.getLatitude());
        response.setLongitude(resource.getLongitude());
        response.setDeliveryRadius(resource.getDeliveryRadius());
        response.setBrand(resource.getBrand());
        response.setModel(resource.getModel());
        response.setYear(resource.getYear());
        response.setCondition(resource.getCondition());
        response.setQuantityAvailable(resource.getQuantityAvailable());
        response.setMinRentalPeriod(resource.getMinRentalPeriod());
        response.setMaxRentalPeriod(resource.getMaxRentalPeriod());
        response.setAdvanceNotice(resource.getAdvanceNotice());
        response.setStatus(resource.getStatus().toString());
        response.setViewsCount(resource.getViewsCount());
        response.setBookingsCount(resource.getBookingsCount());
        response.setAverageRating(resource.getAverageRating());
        response.setReviewCount(resource.getReviewCount());
        response.setCreatedAt(resource.getCreatedAt());
        response.setUpdatedAt(resource.getUpdatedAt());

        // Map owner
        if (resource.getOwner() != null) {
            UserSummary ownerSummary = new UserSummary(
                    resource.getOwner().getId(),
                    resource.getOwner().getUsername(),
                    resource.getOwner().getEmail(),
                    resource.getOwner().getFullName()
            );
            response.setOwner(ownerSummary);
        }

        // Map category
        if (resource.getCategory() != null) {
            CategorySummary categorySummary = new CategorySummary(
                    resource.getCategory().getId(),
                    resource.getCategory().getName(),
                    resource.getCategory().getIcon(),
                    (long) resource.getCategory().getResources().size()
            );
            response.setCategory(categorySummary);
        }

        // Map images
        if (resource.getImages() != null && !resource.getImages().isEmpty()) {
            List<ResourceImageResponse> imageResponses = resource.getImages().stream()
                    .map(img -> new ResourceImageResponse(
                            img.getId(),
                            img.getImageUrl(),
                            img.isPrimary(),
                            img.getSortOrder()
                    ))
                    .collect(Collectors.toList());
            response.setImages(imageResponses);
        }

        return response;
    }

    // Map Resource to ResourceSummary
    private ResourceSummary mapToResourceSummary(Resource resource) {
        UserSummary ownerSummary = null;
        if (resource.getOwner() != null) {
            ownerSummary = new UserSummary(
                    resource.getOwner().getId(),
                    resource.getOwner().getUsername(),
                    resource.getOwner().getEmail(),
                    resource.getOwner().getFullName()
            );
        }

        CategorySummary categorySummary = null;
        if (resource.getCategory() != null) {
            categorySummary = new CategorySummary(
                    resource.getCategory().getId(),
                    resource.getCategory().getName(),
                    resource.getCategory().getIcon(),
                    null
            );
        }

        String primaryImageUrl = null;
        if (resource.getImages() != null && !resource.getImages().isEmpty()) {
            ResourceImage primary = resource.getImages().stream()
                    .filter(ResourceImage::isPrimary)
                    .findFirst()
                    .orElse(resource.getImages().get(0));
            primaryImageUrl = primary.getImageUrl();
        }

        return new ResourceSummary(
                resource.getId(),
                resource.getTitle(),
                ownerSummary,
                categorySummary,
                resource.getPricePerDay(),
                resource.getLocation(),
                resource.getStatus().toString(),
                primaryImageUrl,
                resource.getAverageRating(),
                resource.getReviewCount()
        );
    }
}
