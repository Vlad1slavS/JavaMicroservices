package mentorship.roadmap.microservices.service_b.repository;

import mentorship.roadmap.microservices.service_b.model.OutboxEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
    Page<OutboxEvent> findAll(Pageable pageable);
}
