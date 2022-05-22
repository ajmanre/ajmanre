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
@Document(collection = "propertyTypes")
public class PropertyType {

    @Id
    private String id;

    @NotNull
    private Boolean enable = true;

    @NotBlank
    @Size(max = 20)
    private String type;

    private Integer order;

    private List<PropertyTypeChild> children;
}
