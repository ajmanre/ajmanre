package com.ajmanre.services;

import com.ajmanre.models.Agency;
import com.ajmanre.repository.AgencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AgencyService {

    @Autowired
    AgencyRepository agencyRepository;

    public Page<Agency> getPage(int pageNo, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pg = PageRequest.of(pageNo - 1, size, sort);
        Page<Agency> page = agencyRepository.findAll(pg);
        return page;
    }
}
