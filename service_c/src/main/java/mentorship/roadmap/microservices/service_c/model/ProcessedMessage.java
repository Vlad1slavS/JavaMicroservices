package mentorship.roadmap.microservices.service_c.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "processed_messages")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProcessedMessage {
    @Id
    private UUID id;
}
