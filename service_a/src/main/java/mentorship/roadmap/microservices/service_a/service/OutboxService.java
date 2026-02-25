package mentorship.roadmap.microservices.service_a.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mentorship.roadmap.microservices.service_a.cloud.ServiceBClient;
import mentorship.roadmap.microservices.service_a.model.Event;
import mentorship.roadmap.microservices.service_a.repository.EventRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxService {

    private final EventRepository eventRepository;
    private final ServiceBClient serviceBClient;

    @Scheduled(fixedDelayString = "${app.dispatcher.delay-ms:5000}")
    @SchedulerLock(name = "outbox_dispatcher", lockAtMostFor = "30s", lockAtLeastFor = "5s")
    public void dispatchPendingEvents() {
        List<Event> pending = eventRepository.findAll(PageRequest.of(0, 100))
                .getContent();
        log.debug("Found {} pending events", pending.size());
        for (Event event : pending) {
            try {
                String idempotencyKey = event.getId().toString();
                log.debug("Processing event {}", event);
                serviceBClient.processEvent(idempotencyKey, event);
                eventRepository.deleteById(event.getId());
                log.info("Event {} processed successfully", event.getId());
            } catch (FeignException.FeignClientException e) {
                log.error("Service B rejected event {} (4xx), delete", event.getId(), e);
                eventRepository.deleteById(event.getId());
            } catch (FeignException e) {
                log.error("Service B unavailable for event {}, will retry", event.getId(), e);
            }
        }
    }
}
