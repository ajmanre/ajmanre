package com.ajmanre.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class PropertyTypeChild {

    @NotBlank
    @Size(max = 20)
    private String type;

    private Integer order;

    private List<PropertyTypeChild> children;
}
