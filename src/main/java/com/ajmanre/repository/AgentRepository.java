package com.ajmanre.repository;

import com.ajmanre.models.Agent;
import com.ajmanre.models.Source;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AgentRepository extends MongoRepository<Agent, String> {

    @Query(value = "{}", fields = "{name : 1, _id : 1}")
    List<Source> getAgentSource();

    Optional<Agent> findByUserId(String userId);
}
