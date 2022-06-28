package com.ajmanre.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@Document(collection = "enquiries")
public class Enquiry {

    private String id;

    private LocalDateTime updatedAt;

    private Contact contact;

    private String enqType;

    private String enqTypeId;

    private String propType;

    private String propTypeId;

    private Double priceFrom;

    private Double priceTo;

    private Integer bedMin;

    private Integer bedMax;

    private Integer bathMin;

    private Integer bathMax;

    private Double sqftMin;

    private Double sqftMax;

    private Address address;

    private String notes;

    private Source user;
}
