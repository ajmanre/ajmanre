package com.ajmanre.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "agents")
public class Agent {

    private String id;

    private LocalDateTime updatedAt;

    private String name;

    private String serviceAreas;

    private String specialties;

    private String position;

    private String license;

    private String imageFile;

    private Address address;

    private Contact contact;

    private Source agency;

}
