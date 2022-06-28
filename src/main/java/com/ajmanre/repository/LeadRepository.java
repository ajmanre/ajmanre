package com.ajmanre.repository;

import com.ajmanre.models.Lead;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LeadRepository extends MongoRepository<Lead, String> {


}
