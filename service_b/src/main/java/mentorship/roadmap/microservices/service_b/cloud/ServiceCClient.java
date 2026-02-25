package mentorship.roadmap.microservices.service_b.cloud;

import mentorship.roadmap.microservices.service_b.model.OutboxEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "service-c", url = "${service-c.uri}")
public interface ServiceCClient {

    @PostMapping("/api/save")
    void processEvent(@RequestHeader("Idempotency-Key") String idempotencyKey,
                      @RequestBody OutboxEvent event
    );
}
