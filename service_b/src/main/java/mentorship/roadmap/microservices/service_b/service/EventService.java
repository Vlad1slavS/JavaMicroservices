package mentorship.roadmap.microservices.service_b.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mentorship.roadmap.microservices.service_b.dto.EventDto;
import mentorship.roadmap.microservices.service_b.enums.MessageType;
import mentorship.roadmap.microservices.service_b.model.OutboxEvent;
import mentorship.roadmap.microservices.service_b.repository.OutboxRepository;
import mentorship.roadmap.microservices.service_b.repository.ProcessedMessageRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private static final long redisTTL = 5;

    private final RedisTemplate<String, String> redisTemplate;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final ProcessedMessageRepository processedMessageRepository;

    @Transactional
    public void processEvent(UUID idempotencyKey, EventDto event) {
        int inserted = processedMessageRepository.ignoreInsert(idempotencyKey);
        if (inserted == 0) {
            log.warn("Duplicate event {}, skipping", event.id());
            return;
        }

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .eventId(event.id())
                .eventName(event.name())
                .createdAt(event.createdAt())
                .build();

        outboxRepository.save(outboxEvent);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                cacheEvent(event);
            }
        });
    }

    private void cacheEvent(EventDto event) {
        if (event.messageType().equals(MessageType.IMPORTANT)) {
            redisTemplate.opsForValue().set(
                    "event:" + event.id(),
                    objectMapper.writeValueAsString(event),
                    redisTTL,
                    TimeUnit.MINUTES
            );
        }
    }
}
