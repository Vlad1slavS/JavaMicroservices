package mentorship.roadmap.microservices.service_c.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record EventDto(
        UUID id,
        String name,
        LocalDateTime createdAt) {
}
