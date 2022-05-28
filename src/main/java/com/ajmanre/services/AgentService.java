package com.ajmanre.services;

import com.ajmanre.models.Agent;
import com.ajmanre.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AgentService {

    @Autowired
    AgentRepository agentRepository;

    public Page<Agent> getPage(int pageNo, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pg = PageRequest.of(pageNo - 1, size, sort);
        Page<Agent> page = agentRepository.findAll(pg);
        return page;
    }
}
