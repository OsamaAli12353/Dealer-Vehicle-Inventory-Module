package dealer.task.service.impl;

import dealer.task.dto.request.CreateDealerRequest;
import dealer.task.dto.request.UpdateDealerRequest;
import dealer.task.dto.response.DealerResponse;
import dealer.task.entity.Dealer;
import dealer.task.entity.SubscriptionType;
import dealer.task.exception.AccessDeniedException;
import dealer.task.exception.ResourceNotFoundException;
import dealer.task.repository.DealerRepository;
import dealer.task.service.DealerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DealerServiceImpl implements DealerService {

    private final DealerRepository dealerRepository;

    @Override
    @Transactional
    public DealerResponse create(CreateDealerRequest request, String tenantId) {
        if (dealerRepository.existsByEmailAndTenantId(request.getEmail(), tenantId)) {
            throw new IllegalArgumentException(
                    "A dealer with email '" + request.getEmail() + "' already exists in this tenant.");
        }

        Dealer dealer = Dealer.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .email(request.getEmail())
                .subscriptionType(request.getSubscriptionType())
                .build();

        return toResponse(dealerRepository.save(dealer));
    }

    @Override
    @Transactional(readOnly = true)
    public DealerResponse getById(UUID id, String tenantId) {
        Dealer dealer = findByIdAndTenant(id, tenantId);
        return toResponse(dealer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DealerResponse> getAll(String tenantId, Pageable pageable) {
        return dealerRepository.findAllByTenantId(tenantId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public DealerResponse update(UUID id, UpdateDealerRequest request, String tenantId) {
        Dealer dealer = findByIdAndTenant(id, tenantId);

        if (request.getName() != null && !request.getName().isBlank()) {
            dealer.setName(request.getName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            // Ensure new email isn't already taken by another dealer in this tenant
            if (!request.getEmail().equals(dealer.getEmail())
                    && dealerRepository.existsByEmailAndTenantId(request.getEmail(), tenantId)) {
                throw new IllegalArgumentException(
                        "A dealer with email '" + request.getEmail() + "' already exists in this tenant.");
            }
            dealer.setEmail(request.getEmail());
        }
        if (request.getSubscriptionType() != null) {
            dealer.setSubscriptionType(request.getSubscriptionType());
        }

        return toResponse(dealerRepository.save(dealer));
    }

    @Override
    @Transactional
    public void delete(UUID id, String tenantId) {
        Dealer dealer = findByIdAndTenant(id, tenantId);
        dealerRepository.delete(dealer);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<SubscriptionType, Long> countBySubscription(String tenantId, String callerRole) {
        boolean isGlobalAdmin = "GLOBAL_ADMIN".equalsIgnoreCase(callerRole);

        Map<SubscriptionType, Long> result = new LinkedHashMap<>();
        for (SubscriptionType type : SubscriptionType.values()) {
            long count = isGlobalAdmin
                    ? dealerRepository.countBySubscriptionType(type)
                    : dealerRepository.countByTenantIdAndSubscriptionType(tenantId, type);
            result.put(type, count);
        }
        return result;
    }

    // ─── helpers ────────────────────────────────────────────────────────────────

    private Dealer findByIdAndTenant(UUID id, String tenantId) {
        // First check the dealer exists at all
        Dealer dealer = dealerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + id));

        // Then enforce tenant isolation
        if (!dealer.getTenantId().equals(tenantId)) {
            throw new AccessDeniedException("Access denied: dealer does not belong to your tenant.");
        }
        return dealer;
    }

    private DealerResponse toResponse(Dealer dealer) {
        return DealerResponse.builder()
                .id(dealer.getId())
                .tenantId(dealer.getTenantId())
                .name(dealer.getName())
                .email(dealer.getEmail())
                .subscriptionType(dealer.getSubscriptionType())
                .build();
    }
}
