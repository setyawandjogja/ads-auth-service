package ads.user_management_service.service;

import ads.user_management_service.constant.EventType;
import ads.user_management_service.dto.BaseEvent;
import ads.user_management_service.dto.UserRegistrationData;
import ads.user_management_service.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    public void publishUserCreated(User user, UUID createdBy) {

        // data payload
        UserRegistrationData data = UserRegistrationData.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roleId(user.getRole().getId())
                .build();

        // event wrapper
        BaseEvent<UserRegistrationData> event = BaseEvent.<UserRegistrationData>builder()
                .eventId(UUID.randomUUID())
                .eventType(EventType.USER_REGISTRATION)
                .createdAt(LocalDateTime.now())
                .createdBy(createdBy)
                .data(data)
                .build();

        rabbitTemplate.convertAndSend(exchange, routingKey, event);

        log.info("UserRegistration event published for {}", user.getUserName());
    }
}
