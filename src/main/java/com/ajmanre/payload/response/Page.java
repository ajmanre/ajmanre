package com.ajmanre.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Page <T>{

    List<T> data;
    int page;
    int size;
    long total;
}
