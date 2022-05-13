package com.ajmanre.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Document(collection = "roles")
public class Property {

    @Id
    private String id;

    private String identifier;

    @NotBlank
    @Size(max = 128)
    private String summary;

    @NotBlank
    @Size(max = 24)
    private String status;

    @NotBlank
    @Size(max = 24)
    private String type;


    @NotBlank
    @Size(max = 24)
    private String location;

    @NotNull
    private Integer bedroom;

    private Double sqFt;
    private Double sqMt;

    private String owner;
    private String agent;
    private String project;
    private String tel;

    private List<String> features;
    private List<String> tags;

    private String ownerId;
    private String agentId;
    private String projectId;
    private String statusId;
    private String typeId;




}
