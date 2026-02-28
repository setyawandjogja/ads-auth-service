package ads.autservice.service;

import ads.autservice.dto.UserCreatedMessage;
import ads.autservice.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    public void publishUserCreated(User user) {

        UserCreatedMessage message = UserCreatedMessage.builder()
                .username(user.getUserName())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();

        rabbitTemplate.convertAndSend(exchange, routingKey, message);

        log.info("UserCreated event published for {}", user.getUserName());
    }
}
