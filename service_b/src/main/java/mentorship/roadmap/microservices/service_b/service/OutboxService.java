package mentorship.roadmap.microservices.service_b.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mentorship.roadmap.microservices.service_b.cloud.ServiceCClient;
import mentorship.roadmap.microservices.service_b.model.OutboxEvent;
import mentorship.roadmap.microservices.service_b.repository.OutboxRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final ServiceCClient serviceCClient;

    @Scheduled(fixedDelayString = "${app.dispatcher.delay-ms:5000}")
    @SchedulerLock(name = "outbox_dispatcher", lockAtMostFor = "30s", lockAtLeastFor = "5s")
    public void dispatchPendingEvents() {
        List<OutboxEvent> events = outboxRepository.
                findAll(PageRequest.of(0, 100)).getContent();
        for (OutboxEvent event : events) {
            try {
                log.info("Dispatching event {}", event.getEventId());
                serviceCClient.processEvent(event.getEventId().toString(), event);
                outboxRepository.deleteById(event.getEventId());
            } catch (FeignException.FeignClientException e) {
                log.error("Service C rejected event {} (4xx), delete", event.getEventId(), e);
                outboxRepository.deleteById(event.getEventId());
            } catch (FeignException e) {
                log.error("Service C unavailable for event {}, will retry", event.getEventId(), e);
            }
        }
    }
}
