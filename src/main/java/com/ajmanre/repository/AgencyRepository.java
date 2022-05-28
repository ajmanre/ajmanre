package com.ajmanre.repository;

import com.ajmanre.models.Agency;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AgencyRepository extends MongoRepository<Agency, String> {

}
