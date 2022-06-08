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
@Document(collection = "properties")
public class Property {

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

    @NotBlank
    @Size(max = 24)
    private String type;
    private String typeId;
    private String subType;

    @NotBlank
    @Size(max = 128)
    private String summary;

    @NotBlank
    @Size(max = 24)
    private String location;
    private String locationId;

    @NotNull
    private Integer bedroom;

    private Double sqFt;
    private Double sqMt;

    private List<String> features;
    private List<String> tags;

    private Project project;
    private Source assocWith;
    private Source managedBy;
    private Source user;




}
