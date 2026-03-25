package dealer.task3.repository;

import dealer.task3.entity.Vehicle;
import dealer.task3.entity.SubscriptionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID>,
        JpaSpecificationExecutor<Vehicle> {

    Optional<Vehicle> findByIdAndTenantId(UUID id, String tenantId);

    // For PREMIUM subscription filter — tenant-scoped
    Page<Vehicle> findAllByTenantIdAndDealer_SubscriptionType(
            String tenantId,
            SubscriptionType subscriptionType,
            Pageable pageable);
}
