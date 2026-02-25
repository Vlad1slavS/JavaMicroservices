package mentorship.roadmap.microservices.service_c.repository;

import mentorship.roadmap.microservices.service_c.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
}
