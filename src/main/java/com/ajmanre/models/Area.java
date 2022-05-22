package com.ajmanre.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Document(collection = "areas")
public class Area {

    @Id
    private String id;

    @NotBlank
    @Size(max = 64)
    private String name;

    @NotBlank
    @Size(max = 64)
    private String locality;

    @NotBlank
    @Size(max = 32)
    private String emirate;

    @NotBlank
    @Size(max = 32)
    private String country;
}
