package com.ajmanre.repository;

import com.ajmanre.models.Agency;
import com.ajmanre.models.Source;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AgencyRepository extends MongoRepository<Agency, String> {

    @Query(value = "{}", fields = "{name : 1, _id : 1}")
    List<Source> getAgencySource();

    Optional<Agency> findByUserId(String userId);
}
