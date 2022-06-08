package com.ajmanre.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Getter
@Setter
@Document(collection = "events")
public class Event {

    @Id
    private String id;

    private String identifier;

    @NotNull
    private OffsetDateTime updatedAt;

    @NotNull
    private EventTypeEnum type;

    @NotNull
    private StatusEnum status;

    private int retries;
}
