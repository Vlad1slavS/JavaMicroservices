package mentorship.roadmap.microservices.service_a.dto;

import lombok.Builder;
import mentorship.roadmap.microservices.service_a.enums.MessageType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность получения сообщения из кафки
 *
 * @author Владислав Степанов
 */
@Builder
public record EventDto(
        UUID id,
        String name,
        MessageType messageType,
        LocalDateTime createdAt) {
}
