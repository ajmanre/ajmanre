package com.ajmanre.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ajmanre.models.RoleEnum;
import com.ajmanre.models.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(RoleEnum name);
}
