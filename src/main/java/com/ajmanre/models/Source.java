package com.ajmanre.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class Source {

    @Id
    private String id;

    @NotBlank
    @Size(max = 256)
    private String name;
}
