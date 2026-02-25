package mentorship.roadmap.microservices.service_c.repository;

import mentorship.roadmap.microservices.service_c.model.ProcessedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, UUID> {

    @Modifying
    @Query(value = "INSERT INTO processed_messages (id) VALUES (:id) ON CONFLICT (id) DO NOTHING", nativeQuery = true)
    int ignoreInsert(@Param("id") UUID id);
}
