package mentorship.roadmap.microservices.service_c.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mentorship.roadmap.microservices.service_c.model.Event;
import mentorship.roadmap.microservices.service_c.model.OutboxEvent;
import mentorship.roadmap.microservices.service_c.repository.EventRepository;
import mentorship.roadmap.microservices.service_c.repository.OutboxRepository;
import mentorship.roadmap.microservices.service_c.repository.ProcessedMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final ProcessedMessageRepository processedMessageRepository;
    private final OutboxRepository outboxRepository;
    private final EventRepository eventRepository;

    @Transactional
    public void processEvent(UUID idempotencyKey, Event event) {
        int inserted = processedMessageRepository.ignoreInsert(idempotencyKey);
        if (inserted == 0) {
            log.warn("Duplicate event {}, skipping", event.getId());
            return;
        }

        eventRepository.save(event);

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .eventId(event.getId())
                .eventName(event.getName())
                .createdAt(event.getCreatedAt())
                .build();

        outboxRepository.save(outboxEvent);
    }
}
