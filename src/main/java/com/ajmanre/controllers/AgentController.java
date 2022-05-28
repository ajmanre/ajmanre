package com.ajmanre.controllers;

import com.ajmanre.repository.AgentRepository;
import com.ajmanre.services.AgentService;
import org.openapitools.api.AgentApi;
import org.openapitools.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
public class AgentController implements AgentApi {

    @Autowired
    AgentRepository agentRepository;

    @Autowired
    AgentService agentService;

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
            return ResponseEntity.ok(new AgentPage().page(page).size(pg.getSize()).data(agents).total((int)pg.getTotalElements()));
        }
        return ResponseEntity.ok(new AgentPage().page(page).size(0).data(null).total(0));
    }

    @Override
    public ResponseEntity<MessageResponse> agentPost(AgentRequest agentRequest) {
        com.ajmanre.models.Agent agent = toModel(agentRequest);
        agent = agentRepository.save(agent);
        return ResponseEntity.ok(new MessageResponse()
                .message("Agency created successfully")
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

    private Agent toOut(com.ajmanre.models.Agent agent) {


        Address address = null;
        if(null != agent) {
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
                .address(address).contact(contact).agency(agency);
    }

    private com.ajmanre.models.Agent toModel(AgentRequest agentRequest) {

        com.ajmanre.models.Agent agent = new com.ajmanre.models.Agent();
        agent.setUpdatedAt(LocalDateTime.now());
        agent.setName(agentRequest.getName());
        agent.setServiceAreas(agentRequest.getServiceAreas());
        agent.setPosition(agentRequest.getPosition());
        agent.setLicense(agentRequest.getLicense());
        agent.setImageFile(agentRequest.getImageFile());

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
}
