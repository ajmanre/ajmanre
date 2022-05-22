package com.ajmanre.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Document(collection = "selections")
public class Select {

    @Id
    private String id;

    @NotBlank
    @Size(max = 32)
    private String key;

    @NotBlank
    @Size(max = 64)
    private String value;

    @NotNull
    private Integer order;

    @NotBlank
    @Size(max = 32)
    private String type;
}
