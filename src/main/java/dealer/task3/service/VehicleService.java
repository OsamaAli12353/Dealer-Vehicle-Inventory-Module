package dealer.task3.service;

import dealer.task3.dto.request.CreateVehicleRequest;
import dealer.task3.dto.request.UpdateVehicleRequest;
import dealer.task3.dto.response.VehicleResponse;
import dealer.task3.entity.SubscriptionType;
import dealer.task3.entity.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface VehicleService {

    VehicleResponse create(CreateVehicleRequest request, String tenantId);

    VehicleResponse getById(UUID id, String tenantId);

    /**
     * List vehicles with optional filters.
     * If subscription == PREMIUM, only vehicles whose dealer has PREMIUM subscription
     * are returned — still scoped to the caller's tenant.
     */
    Page<VehicleResponse> getAll(
            String tenantId,
            String model,
            VehicleStatus status,
            BigDecimal priceMin,
            BigDecimal priceMax,
            SubscriptionType subscription,
            Pageable pageable);

    VehicleResponse update(UUID id, UpdateVehicleRequest request, String tenantId);

    void delete(UUID id, String tenantId);
}
