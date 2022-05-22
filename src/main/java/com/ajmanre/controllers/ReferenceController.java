package com.ajmanre.controllers;

import com.ajmanre.models.PropertyType;
import com.ajmanre.payload.response.ActivityResponse;
import com.ajmanre.repository.PropertyTypeRepository;
import org.openapitools.model.MessageResponse;
import org.openapitools.model.PropertyTypeChild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@CrossOrigin("*")
@RequestMapping("reference")
public class ReferenceController {

    @Autowired
    PropertyTypeController propertyTypeController;

    @Autowired
    PropertyTypeRepository propertyTypeRepository;

    @GetMapping("/property/types")
    public ResponseEntity<MessageResponse> activity(@RequestParam String key) {

        if(key.equals("REF_PRP_TYP")) {

            Object[][] array = new Object[][]{
                    {"Residential", 1, "Apartment", 1},
                    {"Residential", 1, "Condo", 2},
                    {"Residential", 1, "Hotel Apartment", 3},
                    {"Residential", 1, "Multi Family Home", 4},
                    {"Residential", 1, "Residential Building", 5},
                    {"Residential", 1, "Residential Plot", 6},
                    {"Residential", 1, "Single Family Home", 7},
                    {"Residential", 1, "Studio", 8},
                    {"Residential", 1, "Townhouse", 9},
                    {"Residential", 1, "Villa", 10},
                    {"Residential", 1, "Villa Compound", 11},
                    {"Land", 2},
                    {"Building For Sale", 3, "Commercial Building For Sale", 1},
                    {"Building For Sale", 3, "Residential Building For Sale", 2},
                    {"Commercial", 4, "Commercial Building", 1},
                    {"Commercial", 4, "Labour Camp", 1},
                    {"Commercial", 4, "Mixed Use Land", 1},
                    {"Commercial", 4, "Shop", 1},
                    {"Commercial", 4, "Showroom", 1},
                    {"Commercial", 4, "Warehouse", 1}
            };

            propertyTypeRepository.deleteAll();
            Arrays.asList(array).stream().map(row -> {
                String type = (String) row[0]; Integer order = (Integer) row[1];
                org.openapitools.model.PropertyTypeRequest propType = new org.openapitools.model.
                        PropertyTypeRequest().type(type).order(order);
                if(row.length > 2) {
                    String childType = (String) row[2]; Integer childOrder = (Integer) row[3];
                    propType
                        .child(new PropertyTypeChild().type(childType).order(childOrder));
                }

                return propType;
            }).forEach(propertyTypeRequest -> {
                propertyTypeController.propType(propertyTypeRequest);
            });
        }

        return ResponseEntity.ok(new MessageResponse().message("Referenced property types"));
    }

}
