package com.ajmanre.repository;

import com.ajmanre.models.Enquiry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EnquiryRepository extends MongoRepository<Enquiry, String> {

}
