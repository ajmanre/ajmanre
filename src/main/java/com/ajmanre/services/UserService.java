package com.ajmanre.services;

import com.ajmanre.payload.Converter;
import com.ajmanre.payload.response.Page;
import com.ajmanre.payload.response.User;
import com.ajmanre.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserService {

    private UserRepository userRepository;

    public Page<User> getPage(int pageNo, int size) {
        //Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pg = PageRequest.of(pageNo, size);
        org.springframework.data.domain.Page<com.ajmanre.models.User> page = userRepository.findAll(pg);
        Page<User> p = new Page<>();
        p.setPage(pageNo);
        p.setSize(page.getSize());
        p.setData(page.getContent().stream().map(Converter::toPayload).collect(Collectors.toList()));
        p.setTotal(page.getTotalElements());
        return p;
    }
}
