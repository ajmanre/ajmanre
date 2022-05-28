package com.ajmanre.repository;

import com.ajmanre.models.Property;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PropertyRepository extends MongoRepository<Property, String> {
}
