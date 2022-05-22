package com.ajmanre.repository;

import com.ajmanre.models.Area;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AreaRepository extends MongoRepository<Area, String> {
}
