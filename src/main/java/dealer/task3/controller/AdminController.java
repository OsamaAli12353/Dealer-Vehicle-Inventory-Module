package dealer.task3.controller;

import dealer.task3.entity.SubscriptionType;
import dealer.task3.exception.AccessDeniedException;
import dealer.task3.security.TenantContext;
import dealer.task3.service.DealerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Admin-only endpoints.
 *
 * GET /admin/dealers/countBySubscription
 * ─────────────────────────────────────
 * Requires role GLOBAL_ADMIN (supplied via X-Role header).
 *
 * Count scope:
 *   - GLOBAL_ADMIN  → counts across ALL tenants (global aggregate).
 *   - Any other role → 403 Forbidden.
 *
 * The result is therefore a GLOBAL count, not per-tenant.
 * If a per-tenant count is needed, a regular authenticated user
 * can derive it from the /dealers listing; or a future
 * per-tenant admin endpoint could be added.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DealerService dealerService;

    @GetMapping("/dealers/countBySubscription")
    public ResponseEntity<Map<SubscriptionType, Long>> countBySubscription() {
        String role = TenantContext.getRole();

        if (!"GLOBAL_ADMIN".equalsIgnoreCase(role)) {
            throw new AccessDeniedException(
                    "Access denied: GLOBAL_ADMIN role required for this endpoint.");
        }

        // tenantId is still passed but service ignores it for GLOBAL_ADMIN
        Map<SubscriptionType, Long> counts =
                dealerService.countBySubscription(TenantContext.getTenantId(), role);

        return ResponseEntity.ok(counts);
    }
}
