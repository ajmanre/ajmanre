package com.ajmanre.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FloorPlan {

    private String title;

    private Integer bedroom;

    private Integer bathroom;

    private Double price;

    private String postFixPrice;

    private Double planSize;

    private File planImage;

    private String description;
}
