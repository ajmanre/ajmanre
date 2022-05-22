package com.ajmanre.repository;

import com.ajmanre.models.Select;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SelectRepository extends MongoRepository<Select, String> {

    List<Select> findByType(String type);
}
