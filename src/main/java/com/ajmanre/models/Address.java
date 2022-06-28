package com.ajmanre.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class Address {

    @NotBlank
    @Size(max = 32)
    private String adressLine1;

    @Size(max = 32)
    private String adressLine2;

    @Size(max = 32)
    private String area;

    @Size(max = 32)
    private String city;

    @Size(max = 32)
    private String country;

    @Size(max = 16)
    private String zip;
}
