package com.ajmanre.query;

import com.ajmanre.models.Property;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PropertyResult {

    private List<Property> data;
    private Long total;
}
