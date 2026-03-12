package controller;

import com.agrosolutions.AgroVia.dto.*;
import service.AcquisitionPaymentService;
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
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class AcquisitionPaymentController {

    @Autowired
    private AcquisitionPaymentService paymentService;

    // Make a payment
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaymentResponse>> makePayment(
            @Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.makePayment(request);
        return new ResponseEntity<>(
                ApiResponse.success("Payment processed successfully", response),
                HttpStatus.CREATED
        );
    }

    // Get payments for an acquisition (creator only)
    @GetMapping("/acquisition/{acquisitionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getAcquisitionPayments(
            @PathVariable Long acquisitionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PaymentResponse> payments = paymentService.getAcquisitionPayments(acquisitionId, pageable);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    // Get payment by ID
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable Long id) {
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Get payment milestones for an acquisition
    @GetMapping("/milestones/acquisition/{acquisitionId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<java.util.List<PaymentMilestoneResponse>>> getPaymentMilestones(
            @PathVariable Long acquisitionId) {
        java.util.List<PaymentMilestoneResponse> milestones = paymentService.getPaymentMilestones(acquisitionId);
        return ResponseEntity.ok(ApiResponse.success(milestones));
    }
}
