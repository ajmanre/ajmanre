package com.ajmanre.controllers;

import com.ajmanre.repository.LeadRepository;
import com.ajmanre.services.LeadService;
import org.openapitools.api.LeadApi;
import org.openapitools.model.Lead;
import org.openapitools.model.LeadPage;
import org.openapitools.model.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class LeadController implements LeadApi {

    @Autowired
    LeadRepository leadRepository;

    @Autowired
    LeadService leadService;

    @Override
    public ResponseEntity<List<Lead>> leadAll() {
        List<Lead> leads = leadRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"))
            .stream().map(this::toOut).collect(Collectors.toList());
        return ResponseEntity.ok(leads);
    }

    @Override
    public ResponseEntity<Lead> leadId(String leadId) {
        return ResponseEntity.ok(toOut(
            leadRepository.findById(leadId).orElseThrow(() -> {
                return new RuntimeException("Lead does not exist");
            })
        ));
    }

    @Override
    public ResponseEntity<LeadPage> leadPageSize(Integer page, Integer size) {
        Page<com.ajmanre.models.Lead> pg = leadService.getPage(page, size);
        if(null != pg.getContent() && !pg.getContent().isEmpty()) {
            List<Lead> leads = pg.getContent().stream().map(this::toOut).collect(Collectors.toList());
            return ResponseEntity.ok(new LeadPage().page(page).size(pg.getSize()).data(leads).total(pg.getTotalElements()));
        }
        return ResponseEntity.ok(new LeadPage().page(page).size(0).data(null).total(0l));
    }

    @Override
    public ResponseEntity<MessageResponse> leadPost(Lead lead, String authorization) {
        com.ajmanre.models.Lead lead1 = toModel(lead, null);
        lead1.setUpdatedAt(LocalDateTime.now());
        lead1 = leadRepository.save(lead1);

        return ResponseEntity.ok(new MessageResponse()
                .message("Lead created successfully")
                .identifier(lead1.getId()));
    }

    @Override
    public ResponseEntity<MessageResponse> leadPut(String id, Lead lead) {
        com.ajmanre.models.Lead lead1 = leadRepository.findById(id).orElseThrow(() -> {
            return new RuntimeException("Lead does not exist");
        });

        lead1 = toModel(lead, lead1);
        lead1.setUpdatedAt(LocalDateTime.now());
        lead1 = leadRepository.save(lead1);

        return ResponseEntity.ok(new MessageResponse()
                .message("Lead updated successfully")
                .identifier(lead1.getId()));
    }

    private com.ajmanre.models.Lead toModel(Lead ld, com.ajmanre.models.Lead lead) {

        lead = lead != null ? lead : new com.ajmanre.models.Lead();

        lead.setType(ld.getType());
        lead.setTypeId(ld.getTypeId());
        lead.setName(ld.getName());
        lead.setFirstName(ld.getFirstName());
        lead.setLastName(ld.getLastName());
        lead.setPhoneNo(ld.getPhoneNo());
        lead.setEmail(ld.getEmail());

        return lead;
    }

    private  Lead toOut(com.ajmanre.models.Lead lead) {
        OffsetDateTime updatedAt = null;
        if(null != lead.getUpdatedAt()) {
            updatedAt = lead.getUpdatedAt().atOffset(ZoneOffset.UTC);
        }
        return new Lead().id(lead.getId()).updatedAt(updatedAt).type(lead.getType()).typeId(lead.getTypeId())
                .name(lead.getName()).firstName(lead.getFirstName()).lastName(lead.getLastName())
                .phoneNo(lead.getPhoneNo()).email(lead.getEmail());
    }

}
