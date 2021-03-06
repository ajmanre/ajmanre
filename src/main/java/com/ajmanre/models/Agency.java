package com.ajmanre.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document(collection = "agencies")
public class Agency {
    @Id
    private String id;

    @NotNull
    private LocalDateTime updatedAt;

    private boolean enabled;

    @NotBlank
    @Size(max = 128)
    private String name;

    @Size(max = 16)
    private String tel;

    @Size(max = 128)
    private String facebook;

    @Size(max = 128)
    private String insta;

    @Size(max = 1024)
    private String about;

    private File image;

    private List<Address> addresses;

    private List<Contact> contacts;

    private Source user;

}
