package com.ajmanre.services;

import com.ajmanre.models.*;
import com.ajmanre.payload.Converter;
import com.ajmanre.payload.response.Page;
import com.ajmanre.repository.AgencyRepository;
import com.ajmanre.repository.AgentRepository;
import com.ajmanre.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AgentRepository agentRepository;

    @Autowired
    AgencyRepository agencyRepository;

    public Page<com.ajmanre.payload.response.User> getPage(int pageNo, int size) {
        //Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pg = PageRequest.of(pageNo, size);
        org.springframework.data.domain.Page<com.ajmanre.models.User> page = userRepository.findAll(pg);
        Page<com.ajmanre.payload.response.User> p = new Page<>();
        p.setPage(pageNo);
        p.setSize(page.getSize());
        p.setData(page.getContent().stream().map(Converter::toPayload).collect(Collectors.toList()));
        p.setTotal(page.getTotalElements());
        return p;
    }

    public void agentCreated(User user) {

        Agent agent = new Agent();
        Source usr = new Source();
        usr.setId(user.getId());
        usr.setName(user.getName());
        agent.setUser(usr);
        agent.setName(user.getName());
        agent.setUpdatedAt(LocalDateTime.now());
        agentRepository.save(agent);
    }

    public void agencyCreated(User user, String agencyName) {
        Source usr = new Source();
        usr.setId(user.getId());
        usr.setName(user.getName());
        Agency agency = new Agency();
        agency.setUser(usr);
        agency.setName(agencyName);
        agency.setUpdatedAt(LocalDateTime.now());
        agencyRepository.save(agency);
    }

    public Optional<Source> getUserAgency(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Error: User is not found."));
        List<RoleEnum> userRoles = user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList());
        if(userRoles.contains(RoleEnum.ROLE_AGENCY)) {
            Agency agency = agencyRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Error: Agency is not found."));
            Source agncy = new Source();
            agncy.setName(agency.getName());
            agncy.setId(agency.getId());
            return Optional.ofNullable(agncy);
        } else if (userRoles.contains(RoleEnum.ROLE_AGENT)) {
            Agent agent = agentRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Error: Agent is not found."));
            return Optional.ofNullable(agent.getAgency());
        }
        return Optional.empty();
    }

    public org.springframework.data.domain.Page<User> getPageNew(int pageNo, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pg = PageRequest.of(pageNo - 1, size, sort);
        org.springframework.data.domain.Page<User> page = userRepository.findAll(pg);
        return page;
    }
}
