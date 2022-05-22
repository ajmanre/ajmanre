package com.ajmanre.controllers;

import com.ajmanre.repository.AreaRepository;
import org.openapitools.api.AreaApi;
import org.openapitools.model.Area;
import org.openapitools.model.AreaRequest;
import org.openapitools.model.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class AreaController implements AreaApi {

    @Autowired
    private AreaRepository areaRepository;

    @Override
    public ResponseEntity<List<Area>> areaAll() {
        List<Area> areas = areaRepository.findAll().stream().map(area -> {
            return new Area().id(area.getId()).name(area.getName())
            .locality(area.getLocality()).emirate(area.getEmirate())
                .country(area.getCountry());
        }).collect(Collectors.toList());
        return ResponseEntity.ok(areas);
    }

    @Override
    public ResponseEntity<MessageResponse> areaPost(AreaRequest areaRequest) {
        com.ajmanre.models.Area area = new com.ajmanre.models.Area();
        area.setLocality(areaRequest.getLocality());
        area.setName(areaRequest.getName());
        area.setEmirate(areaRequest.getEmirate());
        area.setCountry(areaRequest.getCountry());
        area = areaRepository.save(area);
        return ResponseEntity.ok(new MessageResponse().message("Area added").identifier(area.getId()));
    }
}
