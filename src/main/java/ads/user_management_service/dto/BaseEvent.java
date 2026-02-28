package ads.user_management_service.dto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BaseEvent<T> {

    private UUID eventId;
    private String eventType;
    private LocalDateTime createdAt;
    private UUID createdBy;
    private T data;
}