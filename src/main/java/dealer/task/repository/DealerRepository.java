package dealer.task.repository;

import dealer.task.entity.Dealer;
import dealer.task.entity.SubscriptionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, UUID> {

    // All dealers for a tenant (paginated)
    Page<Dealer> findAllByTenantId(String tenantId, Pageable pageable);

    // Find dealer by id within tenant
    Optional<Dealer> findByIdAndTenantId(UUID id, String tenantId);

    // Count by subscriptionType per tenant
    long countByTenantIdAndSubscriptionType(String tenantId, SubscriptionType subscriptionType);

    // GLOBAL count by subscriptionType (across all tenants — used by GLOBAL_ADMIN)
    long countBySubscriptionType(SubscriptionType subscriptionType);

    boolean existsByEmailAndTenantId(String email, String tenantId);
}
