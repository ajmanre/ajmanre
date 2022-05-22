package com.ajmanre.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Document(collection = "agencies")
public class Agency {
    @Id
    private String id;

    @NotBlank
    @Size(max = 128)
    private String name;

    @Size(max = 16)
    private String tel;

    @Size(max = 128)
    private String facebook;

    @Size(max = 128)
    private String insta;

}
