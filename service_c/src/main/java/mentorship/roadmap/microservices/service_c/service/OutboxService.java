package mentorship.roadmap.microservices.service_c.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mentorship.roadmap.microservices.service_c.model.OutboxEvent;
import mentorship.roadmap.microservices.service_c.repository.OutboxRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxService {

    @Value("${spring.kafka.topic.out}")
    private String outTopic;

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "${app.dispatcher.delay-ms:5000}")
    @SchedulerLock(name = "service_c_outbox_dispatcher", lockAtMostFor = "30s", lockAtLeastFor = "5s")
    @Transactional
    public void dispatchPendingEvents() {
        List<OutboxEvent> events = outboxRepository.
                findAll(PageRequest.of(0, 100)).getContent();
        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(outTopic, event.getEventId().toString(),
                                objectMapper.writeValueAsString(event))
                        .get(3, TimeUnit.SECONDS);
                outboxRepository.deleteById(event.getEventId());
            } catch (Exception e) {
                log.error("Failed to dispatch event {}: {}", event.getEventId(), e.getMessage());
            }
        }
    }
}
