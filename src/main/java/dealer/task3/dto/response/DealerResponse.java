package dealer.task3.dto.response;

import dealer.task3.entity.SubscriptionType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DealerResponse {
    private UUID id;
    private String tenantId;
    private String name;
    private String email;
    private SubscriptionType subscriptionType;
}
