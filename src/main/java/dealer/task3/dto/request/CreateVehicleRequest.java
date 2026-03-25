package dealer.task3.dto.request;

import dealer.task3.entity.VehicleStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateVehicleRequest {

    @NotNull(message = "Dealer ID is required")
    private UUID dealerId;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Status is required")
    private VehicleStatus status;
}
