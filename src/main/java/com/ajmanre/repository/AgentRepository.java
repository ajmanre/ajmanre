package com.ajmanre.repository;

import com.ajmanre.models.Agent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AgentRepository extends MongoRepository<Agent, String> {
}
