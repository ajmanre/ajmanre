package com.ajmanre.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document(collection = "properties")
public class Property {

    @Transient
    public static final String SEQUENCE = "properties";

    @Id
    private String id;

    @NotNull
    private LocalDateTime updatedAt;

    @NotBlank
    @Size(max = 32)
    private String identifier;

    @NotBlank
    @Size(max = 24)
    private String status;
    private String statusId;

    @Size(max = 48)
    private String propFor;
    private String propForId;

    @NotBlank
    @Size(max = 24)
    private String type;
    private String typeId;
    private String subType;

    @NotBlank
    @Size(max = 128)
    private String title;

    @Size(max = 48)
    private String rentingType;
    private String rentingTypeId;

    @NotBlank
    @Size(max = 24)
    private String location;
    private String locationId;

    private String description;

    private Double price;
    private Double sqFt;
    private Double sqMt;

    private Double buildupArea;
    private Double totalLandArea;

    private Integer bathrooms;
    private Integer bedrooms;
    private String yearBuilt;
    private Integer rooms;

    private Integer parking;

    private Integer garage;

    private Double garageSize;

    private LocalDateTime availabilityDate;

    private List<String> features;
    private List<String> genFeatures;
    private List<String> nearby;
    private List<String> tags;
    private String tel;
    private String phoneNo;

    private Address address;

    private FloorPlan floorPlan;

    private String disclaimer;

    private Project project;
    private List<File> files;
    private Source assocWith;
    private Source managedBy;
    private Source user;




}
