package dealer.task.dto.request;

import dealer.task.entity.VehicleStatus;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateVehicleRequest {

    private String model;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private BigDecimal price;

    private VehicleStatus status;
}
