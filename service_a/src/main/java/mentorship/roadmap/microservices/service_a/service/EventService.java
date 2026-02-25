package mentorship.roadmap.microservices.service_a.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mentorship.roadmap.microservices.service_a.cloud.ServiceBClient;
import mentorship.roadmap.microservices.service_a.dto.EventDto;
import mentorship.roadmap.microservices.service_a.model.Event;
import mentorship.roadmap.microservices.service_a.repository.EventRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;
    private final ServiceBClient serviceBClient;

    @KafkaListener(
            topics = "${spring.kafka.topic.in}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(String message) {
        EventDto eventDto = objectMapper.readValue(message, EventDto.class);
        if (eventDto.id() == null) {
            log.error("Event ID null, skip message: {}", message);
            return;
        }
        String idempotencyKey = eventDto.id().toString();
        Event event = Event.builder()
                .id(eventDto.id())
                .messageType(eventDto.messageType())
                .name(eventDto.name())
                .createdAt(eventDto.createdAt())
                .build();

        eventRepository.save(event);
        serviceBClient.processEvent(idempotencyKey, event);

        log.info("Event {} processed successfully", event.getId());
    }
}
