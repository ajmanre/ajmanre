package com.ajmanre.repository;

import com.ajmanre.models.PropertyType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PropertyTypeRepository extends MongoRepository<PropertyType, String> {

    public Optional<PropertyType> findByType(String type);
}
