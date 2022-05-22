package com.ajmanre.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class Contact {

    @NotBlank
    @Size(max = 32)
    private String name;

    @Size(max = 32)
    private String tel;

    @Size(max = 32)
    private String mobile;

    @Size(max = 32)
    private String email;
}
