package com.ajmanre.controllers;

import com.ajmanre.models.PropertyType;
import com.ajmanre.payload.response.ActivityResponse;
import com.ajmanre.repository.AreaRepository;
import com.ajmanre.repository.PropertyTypeRepository;
import com.ajmanre.repository.SelectRepository;
import org.openapitools.model.MessageResponse;
import org.openapitools.model.PropertyTypeChild;
import org.openapitools.model.SelectRequest;
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

    @Autowired
    AreaController areaController;

    @Autowired
    AreaRepository areaRepository;

    @Autowired
    SelectRepository selectRepository;

    @Autowired
    SelectController selectController;

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

    @GetMapping("/area/init")
    public ResponseEntity<MessageResponse> area(@RequestParam String key) {

        if(key.equals("REF_AREA")) {

            Object[][] array = new Object[][]{
                    {null, "Ajman Industrial 1"},
                    {null, "Ajman Industrial 2"},
                    {null, "Al Alia	Al Alia"},
                    {null, "Al Amerah"},
                    {null, "Al Bustan"},
                    {null, "al-butain	2"},
                    {null, "Al Hamidiya"},
                    {null, "Al Hamidiya 1	"},
                    {null, "Al Hamidiya 2"},
                    {null, "Al Jerf"},
                    {"Al Jerf Industrial 1", "Al Jerf"},
                    {"Al Jerf Industrial 2", "Al Jerf"},
                    {"Al Jerf Industrial 3", "Al Jerf"},
                    {null, "Al Manama"},
                    {null, "al-manama 2"},
                    {null, "Al Mowaihat"},
                    {null, "EAl Mowaihat 1"},
                    {null, "Al Mowaihat 2"},
                    {null, "Al Mowaihat 3"},
                    {null, "Al Nakhil"},
                    {null, "Nakheel"},
                    {null, "Al Nuaimia 1"},
                    {null, "Eal-nuaimia-1"},
                    {null, "Al Nuaimia 2"},
                    {null, "Al Nuaimiya 3"},
                    {null, "Al Rawda 1"},
                    {null, "Al Rawda 2"},
                    {null, "Al Rawda 3"},
                    {null, "Al Rumailah 1"},
                    {null, "Al Tallah"},
                    {"Al Tallah 1", "Al Tallah"},
                    {"Al Tallah 2", "Al Tallah"},
                    {null, "Al Yasmeen"},
                    {null, "Al Zahya"},
                    {null, "Al Zohra"},
                    {null, "Argoub"},
                    {null, "Corniche"},
                    {null, "corniche 5"},
                    {null, "Helio"},
                    {null, "helio	8"},
                    {null, "Liwara"},
                    {null, "liwara	5"},
                    {null, "Masfout"},
                    {null, "masfout	2"},
                    {null, "Musheirif"},
                    {null, "Rashidya"},
                    {"Al Rashidiya 1", "Rashidiya"},
                    {"Al Rashidiya 2", "Rashidiya"},
                    {"Al Rashidiya 3", "Rashidiya"}
            };

            areaRepository.deleteAll();
            Arrays.asList(array).stream().map(row -> {
                String locality = null == row[0] ? null : (String) row[0];
                String areaStr = (String) row[1];
                org.openapitools.model.AreaRequest areReq = new org.openapitools.model.
                        AreaRequest().locality(locality).name(areaStr).emirate("Ajman").country("United Arab Emirates");

                return areReq;
            }).forEach(areaRequest -> {
                areaController.areaPost(areaRequest);
            });
        }

        return ResponseEntity.ok(new MessageResponse().message("Referenced area location"));
    }

    @GetMapping("/select/init")
    public ResponseEntity<MessageResponse> select(@RequestParam String key) {

        if(key.equals("REF_SEL")) {
            Object[][] array = new Object[][]{
                    {"Available", "Available", 0, "prop-status"},
                    {"For Rent", "For Rent", 0, "prop-status"},
                    {"For Sale", "For Sale", 0, "prop-status"},
                    {"Foreclosures", "Foreclosures", 0, "prop-status"},
                    {"New Costruction", "New Costruction", 0, "prop-status"},
                    {"New Listing", "New Listing", 0, "prop-status"},
                    {"Open House", "Open House", 0, "prop-status"},
                    {"Reduced Price", "Reduced Price", 0, "prop-status"},
                    {"Resale", "Resale", 0, "prop-status"}
            };

            selectRepository.deleteAll();
            Arrays.asList(array).stream().map(row -> {
                String ky = (String) row[0], val = (String) row[1], type = (String) row[3];
                Integer order = (Integer) row[2];
                return new SelectRequest().key(ky).value(val).order(order).type(type);
            }).forEach(selectReq -> {
                selectController.selectPost(selectReq);
            });
        }
        return ResponseEntity.ok(new MessageResponse().message("Referenced select types"));
    }

}
