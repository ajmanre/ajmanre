package com.ajmanre.services;

import com.ajmanre.models.Enquiry;
import com.ajmanre.repository.EnquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class EnquiryService {

    @Autowired
    EnquiryRepository enquiryRepository;

    public Page<Enquiry> getPage(int pageNo, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pg = PageRequest.of(pageNo - 1, size, sort);
        Page<Enquiry> page = enquiryRepository.findAll(pg);
        return page;
    }
}
