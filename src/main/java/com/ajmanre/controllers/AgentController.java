package com.ajmanre.controllers;

import com.ajmanre.models.Role;
import com.ajmanre.models.RoleEnum;
import com.ajmanre.models.User;
import com.ajmanre.repository.AgentRepository;
import com.ajmanre.repository.RoleRepository;
import com.ajmanre.repository.UserRepository;
import com.ajmanre.security.jwt.JwtUtils;
import com.ajmanre.services.AgentService;
import org.openapitools.api.AgentApi;
import org.openapitools.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
public class AgentController implements AgentApi {

    @Autowired
    AgentRepository agentRepository;

    @Autowired
    AgentService agentService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public ResponseEntity<Agent> agentId(String agentId) {
        com.ajmanre.models.Agent agent = agentRepository.findById(agentId).orElseThrow(() -> {
            return new RuntimeException("agent does not exist");
        });
        return ResponseEntity.ok(toOut(agent));
    }

    @Override
    public ResponseEntity<AgentPage> agentPageSize(Integer page, Integer size) {
        Page<com.ajmanre.models.Agent> pg = agentService.getPage(page, size);
        if(null != pg.getContent() && !pg.getContent().isEmpty()) {
            List<Agent> agents = pg.getContent().stream().map(this::toOut).collect(Collectors.toList());
            return ResponseEntity.ok(new AgentPage().page(page).size(pg.getSize()).data(agents).total(pg.getTotalElements()));
        }
        return ResponseEntity.ok(new AgentPage().page(page).size(0).data(null).total(0l));
    }

    @Override
    public ResponseEntity<MessageResponse> agentPost(AgentRequest agentRequest, String authorization) {

        org.openapitools.model.MessageResponse response = new org.openapitools.model.MessageResponse();
        if (userRepository.existsByUsername(agentRequest.getUsername())) {
            response.setMessage("Error: Username is already taken!");
            return ResponseEntity
                    .badRequest()
                    .body(response);
        }

        if (userRepository.existsByEmail(agentRequest.getEmail())) {
            response.setMessage("Error: Email is already in use!");
            return ResponseEntity
                    .badRequest()
                    .body(response);
        }

        // Create new user's account
        User newUser = new User(agentRequest.getUsername(),
                agentRequest.getEmail(),
                encoder.encode(agentRequest.getPassword()));

        newUser.setName(agentRequest.getName());
        newUser.setEmail(agentRequest.getEmail());
        Set<Role> roles = new HashSet<>();

        Role modRole = roleRepository.findByName(RoleEnum.ROLE_AGENT)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(modRole);

        newUser.setRoles(roles);
        newUser = userRepository.save(newUser);

        com.ajmanre.models.Source agentUser = new com.ajmanre.models.Source();
        agentUser.setId(newUser.getId());
        agentUser.setName(newUser.getUsername());

        com.ajmanre.models.Agent agent = toModel(agentRequest);
        agent.setUpdatedAt(LocalDateTime.now());
        agent.setUser(agentUser);

        com.ajmanre.models.Source user = null;
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String jwt = authorization.substring(7, authorization.length());
            final String username = jwtUtils.getUserNameFromJwtToken(jwt);
            final String userId = jwtUtils.getUserIdFromJwtClaim(jwt);
            user = new com.ajmanre.models.Source();
            user.setId(userId);
            user.setName(username);
        }
        agent.setCreatedBy(user);
        agent = agentRepository.save(agent);
        return ResponseEntity.ok(new MessageResponse()
                .message("Agent created successfully")
                .identifier(agent.getId()));
    }

    @Override
    public ResponseEntity<MessageResponse> agentPut(String id, Agent agent) {
        com.ajmanre.models.Agent saved = agentRepository.findById(id).orElseThrow(() -> {
            return new RuntimeException("agent does not exist");
        });

        saved.setUpdatedAt(LocalDateTime.now());
        saved.setName(agent.getName());
        saved.setServiceAreas(agent.getServiceAreas());
        saved.setPosition(agent.getPosition());
        saved.setLicense(agent.getLicense());
        saved.setImageFile(agent.getImageFile());
        saved.setLanguages(agent.getLangauges());
        saved.setAbout(agent.getAbout());

        if(agent.getAddress() != null) {
            Address addr = agent.getAddress();
            com.ajmanre.models.Address address = new com.ajmanre.models.Address();
            address.setAdressLine1(addr.getAdressLine1());
            address.setAdressLine2(addr.getAdressLine2());
            address.setArea(addr.getArea());
            address.setCity(addr.getCity());
            address.setCountry(addr.getCountry());
            saved.setAddress(address);
        }

        if(agent.getContact() != null) {
            Contact con = agent.getContact();
            com.ajmanre.models.Contact contact = new com.ajmanre.models.Contact();
            contact.setName(con.getName());
            contact.setTel(con.getTel());
            contact.setMobile(con.getMobile());
            contact.setEmail(con.getEmail());
            saved.setContact(contact);
        }

        saved = agentRepository.save(saved);

        return ResponseEntity.ok(new MessageResponse()
                .message("Agency updated successfully")
                .identifier(saved.getId()));
    }

    @Override
    public ResponseEntity<List<Source>> agentSourceList() {
        List<com.ajmanre.models.Source> srcs = agentRepository.getAgentSource();
        List<Source> sources = null;
        if( null != srcs) {
            sources = srcs.stream().map(s -> new Source().id(s.getId()).name(s.getName()))
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(sources);
    }

    private Agent toOut(com.ajmanre.models.Agent agent) {


        Address address = null;
        if(null != agent.getAddress()) {
            com.ajmanre.models.Address addr = agent.getAddress();
            address = new Address().adressLine1(addr.getAdressLine1())
                    .adressLine2(addr.getAdressLine2()).area(addr.getArea())
                    .city(addr.getCity()).country(addr.getCountry());
        }

        Contact contact = null;
        if( null != agent.getContact()) {
            com.ajmanre.models.Contact con = agent.getContact();
            contact = new Contact().name(con.getName())
                        .tel(con.getTel()).mobile(con.getMobile()).email(con.getEmail());
        }

        Source agency = null;
        if((null != agent.getAgency())) {
            com.ajmanre.models.Source agncy = agent.getAgency();
            agency = new Source().id(agncy.getId()).name(agncy.getName());
        }


        return new Agent().id(agent.getId()).updatedAt(agent.getUpdatedAt().atOffset(ZoneOffset.UTC))
                .name(agent.getName()).serviceAreas(agent.getServiceAreas())
                .specialties(agent.getSpecialties()).position(agent.getPosition())
                .license(agent.getLicense()).imageFile(agent.getImageFile())
                .langauges(agent.getLanguages()).about(agent.getAbout())
                .address(address).contact(contact).agency(agency);
    }

    private com.ajmanre.models.Agent toModel(AgentRequest agentRequest) {

        com.ajmanre.models.Agent agent = new com.ajmanre.models.Agent();
        agent.setUpdatedAt(LocalDateTime.now());
        agent.setName(agentRequest.getName());
        agent.setServiceAreas(agentRequest.getServiceAreas());
        agent.setSpecialties(agentRequest.getSpecialties());
        agent.setPosition(agentRequest.getPosition());
        agent.setLicense(agentRequest.getLicense());
        agent.setImageFile(agentRequest.getImageFile());
        agent.setLanguages(agentRequest.getLangauges());
        agent.setAbout(agentRequest.getAbout());

        if(agentRequest.getAddress() != null) {
            Address addr = agentRequest.getAddress();
            com.ajmanre.models.Address address = new com.ajmanre.models.Address();
            address.setAdressLine1(addr.getAdressLine1());
            address.setAdressLine2(addr.getAdressLine2());
            address.setArea(addr.getArea());
            address.setCity(addr.getCity());
            address.setCountry(addr.getCountry());
            agent.setAddress(address);
        }

        if(agentRequest.getContact() != null) {
            Contact con = agentRequest.getContact();
            com.ajmanre.models.Contact contact = new com.ajmanre.models.Contact();
            contact.setName(con.getName());
            contact.setTel(con.getTel());
            contact.setMobile(con.getMobile());
            contact.setEmail(con.getEmail());
            agent.setContact(contact);
        }

        if(null != agentRequest.getAgency()) {
            Source agncy = agentRequest.getAgency();
            com.ajmanre.models.Source agency = new com.ajmanre.models.Source();
            agency.setId(agncy.getId());
            agency.setName(agncy.getName());
            agent.setAgency(agency);
        }

        return agent;
    }

    @Override
    public ResponseEntity<List<Agent>> agentByAgencyId(String agencyId) {
        List<Agent> agencyAssociates = agentRepository.findByAgencyId(agencyId)
            .stream().map(this::toOut).collect(Collectors.toList());
        return ResponseEntity.ok(agencyAssociates);
    }
}
