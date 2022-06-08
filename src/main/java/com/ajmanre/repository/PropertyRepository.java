package com.ajmanre.repository;

import com.ajmanre.models.Property;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PropertyRepository extends MongoRepository<Property, String> {

    @Aggregation(pipeline = {"{ $sample: { size: 20 } }"})
    public List<Property> findRandomRecords();
}
