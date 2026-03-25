package dealer.task3.dto.response;

import dealer.task3.entity.VehicleStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class VehicleResponse {
    private UUID id;
    private String tenantId;
    private UUID dealerId;
    private String dealerName;
    private String model;
    private BigDecimal price;
    private VehicleStatus status;
}
