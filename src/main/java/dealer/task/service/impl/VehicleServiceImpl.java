package dealer.task.service.impl;

import dealer.task.dto.request.CreateVehicleRequest;
import dealer.task.dto.request.UpdateVehicleRequest;
import dealer.task.dto.response.VehicleResponse;
import dealer.task.entity.Dealer;
import dealer.task.entity.Vehicle;
import dealer.task.entity.SubscriptionType;
import dealer.task.entity.VehicleStatus;
import dealer.task.exception.AccessDeniedException;
import dealer.task.exception.ResourceNotFoundException;
import dealer.task.repository.DealerRepository;
import dealer.task.repository.VehicleRepository;
import dealer.task.repository.VehicleSpecification;
import dealer.task.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DealerRepository  dealerRepository;

    @Override
    @Transactional
    public VehicleResponse create(CreateVehicleRequest request, String tenantId) {
        Dealer dealer = findDealerByIdAndTenant(request.getDealerId(), tenantId);

        Vehicle vehicle = Vehicle.builder()
                .tenantId(tenantId)
                .dealer(dealer)
                .model(request.getModel())
                .price(request.getPrice())
                .status(request.getStatus())
                .build();

        return toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getById(UUID id, String tenantId) {
        return toResponse(findByIdAndTenant(id, tenantId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleResponse> getAll(
            String tenantId,
            String model,
            VehicleStatus status,
            BigDecimal priceMin,
            BigDecimal priceMax,
            SubscriptionType subscription,
            Pageable pageable) {

        // PREMIUM shortcut — delegate to specific derived query
        if (subscription == SubscriptionType.PREMIUM) {
            return vehicleRepository
                    .findAllByTenantIdAndDealer_SubscriptionType(tenantId, SubscriptionType.PREMIUM, pageable)
                    .map(this::toResponse);
        }

        // General filtering via Specification
        Specification<Vehicle> spec = VehicleSpecification.filter(tenantId, model, status, priceMin, priceMax);
        return vehicleRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public VehicleResponse update(UUID id, UpdateVehicleRequest request, String tenantId) {
        Vehicle vehicle = findByIdAndTenant(id, tenantId);

        if (request.getModel() != null && !request.getModel().isBlank()) {
            vehicle.setModel(request.getModel());
        }
        if (request.getPrice() != null) {
            vehicle.setPrice(request.getPrice());
        }
        if (request.getStatus() != null) {
            vehicle.setStatus(request.getStatus());
        }

        return toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional
    public void delete(UUID id, String tenantId) {
        Vehicle vehicle = findByIdAndTenant(id, tenantId);
        vehicleRepository.delete(vehicle);
    }

    // ─── helpers ────────────────────────────────────────────────────────────────

    private Vehicle findByIdAndTenant(UUID id, String tenantId) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        if (!vehicle.getTenantId().equals(tenantId)) {
            throw new AccessDeniedException("Access denied: vehicle does not belong to your tenant.");
        }
        return vehicle;
    }

    private Dealer findDealerByIdAndTenant(UUID dealerId, String tenantId) {
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer not found with id: " + dealerId));

        if (!dealer.getTenantId().equals(tenantId)) {
            throw new AccessDeniedException("Access denied: dealer does not belong to your tenant.");
        }
        return dealer;
    }

    private VehicleResponse toResponse(Vehicle v) {
        return VehicleResponse.builder()
                .id(v.getId())
                .tenantId(v.getTenantId())
                .dealerId(v.getDealer().getId())
                .dealerName(v.getDealer().getName())
                .model(v.getModel())
                .price(v.getPrice())
                .status(v.getStatus())
                .build();
    }
}
