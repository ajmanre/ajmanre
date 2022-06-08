package com.ajmanre.repository;

import com.ajmanre.models.Requirement;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RequirementRepository extends MongoRepository<Requirement, String> {
}
