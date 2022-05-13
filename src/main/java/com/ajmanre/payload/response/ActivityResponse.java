package com.ajmanre.payload.response;

import javax.validation.constraints.NotBlank;

public class ActivityResponse {

    @NotBlank
    private String id;
    
    @NotBlank
    private String name;

    private Double points;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "ActivityResponse{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", points=" + points +
                '}';
    }
}
