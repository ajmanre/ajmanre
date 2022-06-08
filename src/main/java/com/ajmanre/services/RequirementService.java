package com.ajmanre.services;

import com.ajmanre.models.Agent;
import com.ajmanre.models.Requirement;
import com.ajmanre.repository.RequirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class RequirementService {

    @Autowired
    RequirementRepository requirementRepository;

    public Page<Requirement> getPage(int pageNo, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pg = PageRequest.of(pageNo - 1, size, sort);
        Page<Requirement> page = requirementRepository.findAll(pg);
        return page;
    }
}
