package com.ajmanre.services;

import com.ajmanre.models.PropertyType;
import com.ajmanre.repository.PropertyTypeRepository;
import org.openapitools.model.PropertyTypeHierarchy;
import org.openapitools.model.PropertyTypeItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PropertyTypeService {

    @Autowired
    PropertyTypeRepository propertyTypeRepository;

    public PropertyType create(PropertyType propertyType) {
        PropertyType saved = propertyTypeRepository.save(propertyType);
        return saved;
    }

    public PropertyType get(String id) {
        return propertyTypeRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Error: PropertyType is not found."));
    }

    public Map<String, List<PropertyType>> getPropTypes() {
        Map<String, List<PropertyType>> map =  propertyTypeRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                PropertyType::getType,
                HashMap::new,
                Collectors.toCollection(ArrayList::new)
            ));
        return map;
    }
}
