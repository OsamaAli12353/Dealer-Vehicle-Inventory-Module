package dealer.task3.controller;

import dealer.task3.dto.request.CreateVehicleRequest;
import dealer.task3.dto.request.UpdateVehicleRequest;
import dealer.task3.dto.response.VehicleResponse;
import dealer.task3.entity.SubscriptionType;
import dealer.task3.entity.VehicleStatus;
import dealer.task3.security.TenantContext;
import dealer.task3.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    // POST /vehicles
    @PostMapping
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody CreateVehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehicleService.create(request, TenantContext.getTenantId()));
    }

    // GET /vehicles/{id}
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleService.getById(id, TenantContext.getTenantId()));
    }

    // GET /vehicles?model=&status=&priceMin=&priceMax=&subscription=PREMIUM&page=0&size=10&sort=price,asc
    @GetMapping
    public ResponseEntity<Page<VehicleResponse>> getAll(
            @RequestParam(required = false) String model,
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) SubscriptionType subscription,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "model,asc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, buildSort(sort));
        return ResponseEntity.ok(vehicleService.getAll(
                TenantContext.getTenantId(),
                model, status, priceMin, priceMax, subscription,
                pageable));
    }

    // PATCH /vehicles/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<VehicleResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateVehicleRequest request) {

        return ResponseEntity.ok(
                vehicleService.update(id, request, TenantContext.getTenantId()));
    }

    // DELETE /vehicles/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        vehicleService.delete(id, TenantContext.getTenantId());
        return ResponseEntity.noContent().build();
    }

    // ─── helpers ────────────────────────────────────────────────────────────────

    private Sort buildSort(String[] sortParams) {
        if (sortParams.length == 2) {
            Sort.Direction dir = sortParams[1].equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            return Sort.by(dir, sortParams[0]);
        }
        return Sort.by(Sort.Direction.ASC, sortParams[0]);
    }
}
