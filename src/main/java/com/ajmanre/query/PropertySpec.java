package com.ajmanre.query;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PropertySpec {

    private List<TypeRel> types;
    private List<String> statusIds;
    private List<String> locationIds;
    private Integer bedroom;

}
