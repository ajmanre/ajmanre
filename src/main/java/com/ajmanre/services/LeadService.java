package com.ajmanre.services;

import com.ajmanre.models.Lead;
import com.ajmanre.models.Requirement;
import com.ajmanre.repository.LeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class LeadService {

    @Autowired
    LeadRepository leadRepository;

    public Page<Lead> getPage(int pageNo, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pg = PageRequest.of(pageNo - 1, size, sort);
        Page<Lead> page = leadRepository.findAll(pg);
        return page;
    }
}
