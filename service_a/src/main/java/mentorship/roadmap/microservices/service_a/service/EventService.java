package mentorship.roadmap.microservices.service_a.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mentorship.roadmap.microservices.service_a.dto.EventDto;
import mentorship.roadmap.microservices.service_a.model.Event;
import mentorship.roadmap.microservices.service_a.repository.EventRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${spring.kafka.topic.in}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(String message, Acknowledgment ack) {
        try {
            EventDto eventDto = objectMapper.readValue(message, EventDto.class);
            Event event = Event.builder()
                    .id(eventDto.id())
                    .name(eventDto.name())
                    .createdAt(eventDto.createdAt())
                    .build();
            eventRepository.save(event);
            log.debug("Event {} processed successfully", event.getId());
            ack.acknowledge();
        } catch (JacksonException e) {
            log.error("Non-deserializable message{}", message, e);
            ack.acknowledge();
        } catch (DuplicateKeyException e) {
            log.warn("Duplicate event, skipping: {}", message);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Unexpected error processing message: {}", message, e);
        }
    }
}
