package dealer.task3.service;

import dealer.task3.dto.request.CreateDealerRequest;
import dealer.task3.dto.request.UpdateDealerRequest;
import dealer.task3.dto.response.DealerResponse;
import dealer.task3.entity.SubscriptionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.UUID;

public interface DealerService {

    DealerResponse create(CreateDealerRequest request, String tenantId);

    DealerResponse getById(UUID id, String tenantId);

    Page<DealerResponse> getAll(String tenantId, Pageable pageable);

    DealerResponse update(UUID id, UpdateDealerRequest request, String tenantId);

    void delete(UUID id, String tenantId);

    /**
     * Returns count of dealers grouped by SubscriptionType.
     * When callerRole == GLOBAL_ADMIN  → counts across ALL tenants.
     * Otherwise                        → counts within the caller's tenant only.
     */
    Map<SubscriptionType, Long> countBySubscription(String tenantId, String callerRole);
}
