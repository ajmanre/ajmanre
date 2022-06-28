package com.ajmanre.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "leads")
public class Lead {

    @Id
    private String id;

    @NotNull
    private LocalDateTime updatedAt;

    private String type;

    private String typeId;

    private String name;

    private String firstName;

    private String lastName;

    private String email;

    @NotBlank
    @Size(max = 16)
    private String phoneNo;
}
