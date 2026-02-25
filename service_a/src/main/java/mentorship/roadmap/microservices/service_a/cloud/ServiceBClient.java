package mentorship.roadmap.microservices.service_a.cloud;

import mentorship.roadmap.microservices.service_a.model.Event;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "service-b", url = "${service-b.uri}")
public interface ServiceBClient {

    @PostMapping("/api/process")
    void processEvent(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody Event event
    );
}
