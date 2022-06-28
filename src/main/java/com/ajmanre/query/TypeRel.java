package com.ajmanre.query;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TypeRel {
    private String typeId, type, subType;
}
