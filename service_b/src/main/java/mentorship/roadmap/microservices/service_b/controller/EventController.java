package mentorship.roadmap.microservices.service_b.controller;

import lombok.RequiredArgsConstructor;
import mentorship.roadmap.microservices.service_b.dto.EventDto;
import mentorship.roadmap.microservices.service_b.service.EventService;
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

    @PostMapping("/process")
    public ResponseEntity<Void> eventHandler(
            @RequestHeader("Idempotency-Key") UUID idempotencyKey,
            @RequestBody EventDto eventDto) {
        eventService.processEvent(idempotencyKey, eventDto);
        return ResponseEntity.accepted().build();
    }

}
