package mentorship.roadmap.microservices.service_a.repository;

import mentorship.roadmap.microservices.service_a.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventRepository extends MongoRepository<Event, UUID> {
    Page<Event> findAll(Pageable pageable);
}
