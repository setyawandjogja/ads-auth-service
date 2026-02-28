package ads.user_management_service.dto;

import ads.user_management_service.constant.EventType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseEvent<T> {

    private UUID eventId;
    private EventType eventType;
    private LocalDateTime createdAt;
    private UUID createdBy;
    private T data;
}