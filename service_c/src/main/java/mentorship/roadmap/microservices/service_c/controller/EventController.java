package mentorship.roadmap.microservices.service_c.controller;

import lombok.RequiredArgsConstructor;
import mentorship.roadmap.microservices.service_c.dto.EventDto;
import mentorship.roadmap.microservices.service_c.model.Event;
import mentorship.roadmap.microservices.service_c.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/save")
    public ResponseEntity<Void> processEvent(
            @RequestHeader("Idempotency-Key") UUID idempotencyKey,
            @RequestBody EventDto eventDto) {
        Event event = Event.builder()
                .id(eventDto.id())
                .name(eventDto.name())
                .createdAt(eventDto.createdAt())
                .build();
        eventService.processEvent(idempotencyKey, event);
        return ResponseEntity.accepted().build();
    }

}
