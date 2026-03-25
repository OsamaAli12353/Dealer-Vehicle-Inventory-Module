package dealer.task3.repository;

import dealer.task3.entity.Vehicle;
import dealer.task3.entity.VehicleStatus;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specifications for dynamic Vehicle filtering.
 * Supports: model (contains, case-insensitive), status, priceMin, priceMax, tenantId.
 */
public class VehicleSpecification {

    private VehicleSpecification() {}

    public static Specification<Vehicle> filter(
            String tenantId,
            String model,
            VehicleStatus status,
            BigDecimal priceMin,
            BigDecimal priceMax) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter by tenant
            predicates.add(cb.equal(root.get("tenantId"), tenantId));

            if (model != null && !model.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("model")),
                        "%" + model.toLowerCase() + "%"
                ));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (priceMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), priceMin));
            }

            if (priceMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), priceMax));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
