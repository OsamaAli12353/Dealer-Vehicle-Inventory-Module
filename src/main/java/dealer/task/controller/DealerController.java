package dealer.task.controller;

import dealer.task.dto.request.CreateDealerRequest;
import dealer.task.dto.request.UpdateDealerRequest;
import dealer.task.dto.response.DealerResponse;
import dealer.task.security.TenantContext;
import dealer.task.service.DealerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/dealers")
@RequiredArgsConstructor
public class DealerController {

    private final DealerService dealerService;

    // POST /dealers
    @PostMapping
    public ResponseEntity<DealerResponse> create(@Valid @RequestBody CreateDealerRequest request) {
        DealerResponse response = dealerService.create(request, TenantContext.getTenantId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /dealers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<DealerResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(dealerService.getById(id, TenantContext.getTenantId()));
    }

    // GET /dealers?page=0&size=10&sort=name,asc
    @GetMapping
    public ResponseEntity<Page<DealerResponse>> getAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, buildSort(sort));
        return ResponseEntity.ok(dealerService.getAll(TenantContext.getTenantId(), pageable));
    }

    // PATCH /dealers/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<DealerResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDealerRequest request) {

        return ResponseEntity.ok(
                dealerService.update(id, request, TenantContext.getTenantId()));
    }

    // DELETE /dealers/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        dealerService.delete(id, TenantContext.getTenantId());
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
