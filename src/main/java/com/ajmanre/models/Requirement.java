package com.ajmanre.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Getter
@Setter
@Document(collection = "requirements")
public class Requirement {

    @Id
    private String id;

    private OffsetDateTime updatedAt;

    private String name;

    private String email;

    private String phoneNo;

    private String whatsapp;

    private String message;

    private Boolean featured;

    private OffsetDateTime listingExpDate;

    private OffsetDateTime postedDate;

    private Source postedBy;
}
