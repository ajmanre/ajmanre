package com.ajmanre.services;

import com.ajmanre.models.Agency;
import com.ajmanre.models.Agent;
import com.ajmanre.models.Source;
import com.ajmanre.models.User;
import com.ajmanre.payload.Converter;
import com.ajmanre.payload.response.Page;
import com.ajmanre.repository.AgencyRepository;
import com.ajmanre.repository.AgentRepository;
import com.ajmanre.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
}
