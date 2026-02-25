package mentorship.roadmap.microservices.service_a.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mentorship.roadmap.microservices.service_a.enums.MessageType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
public class Event {

    @Id
    private UUID id;

    private String name;

    private MessageType messageType;

    private LocalDateTime createdAt;

}
