package mentorship.roadmap.microservices.service_b.dto;

import lombok.Builder;
import mentorship.roadmap.microservices.service_b.enums.MessageType;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record EventDto(
        UUID id,
        String name,
        MessageType messageType,
        LocalDateTime createdAt) {
}
