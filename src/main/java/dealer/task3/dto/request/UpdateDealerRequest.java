package dealer.task3.dto.request;

import dealer.task3.entity.SubscriptionType;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateDealerRequest {

    private String name;

    @Email(message = "Email must be valid")
    private String email;

    private SubscriptionType subscriptionType;
}
