package controller;

import com.agrosolutions.AgroVia.dto.*;
import service.NegotiationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/negotiations")
@CrossOrigin(origins = "*")
public class NegotiationController {

    @Autowired
    private NegotiationService negotiationService;

    // Start a new negotiation
    @PostMapping
    public ResponseEntity<ApiResponse<NegotiationResponse>> createNegotiation(
            @Valid @RequestBody NegotiationRequest request) {
        NegotiationResponse response = negotiationService.createNegotiation(request);
        return new ResponseEntity<>(ApiResponse.success("Negotiation started successfully", response), HttpStatus.CREATED);
    }

    // Get my negotiations (as buyer)
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<NegotiationResponse>>> getMyNegotiations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<NegotiationResponse> negotiations = negotiationService.getMyNegotiations(pageable);
        return ResponseEntity.ok(ApiResponse.success(negotiations));
    }

    // Get negotiations for my products (as seller)
    @GetMapping("/seller")
    public ResponseEntity<ApiResponse<Page<NegotiationResponse>>> getNegotiationsForMyProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<NegotiationResponse> negotiations = negotiationService.getNegotiationsForMyProducts(pageable);
        return ResponseEntity.ok(ApiResponse.success(negotiations));
    }

    // Get negotiation by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NegotiationResponse>> getNegotiationById(@PathVariable Long id) {
        NegotiationResponse response = negotiationService.getNegotiationById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Respond to negotiation (accept/reject)
    @PostMapping("/{id}/respond")
    public ResponseEntity<ApiResponse<NegotiationResponse>> respondToNegotiation(
            @PathVariable Long id,
            @RequestParam String response,
            @RequestParam String status) {
        NegotiationResponse negotiationResponse = negotiationService.respondToNegotiation(id, response, status);
        return ResponseEntity.ok(ApiResponse.success("Negotiation response sent successfully", negotiationResponse));
    }
}