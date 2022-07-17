package com.ajmanre.controllers;

import com.ajmanre.models.File;
import com.ajmanre.repository.AgencyRepository;
import com.ajmanre.services.AgencyService;
import org.openapitools.api.AgencyApi;
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
public class AgencyController implements AgencyApi {

    @Autowired
    AgencyRepository agencyRepository;

    @Autowired
    AgencyService agencyService;

    @Override
    public ResponseEntity<Agency> agencyId(String agencyId) {
        com.ajmanre.models.Agency agency = agencyRepository.findById(agencyId).orElseThrow(() -> {
            return new RuntimeException("agency does not exist");
        });
        return ResponseEntity.ok(toOut(agency));
    }

    @Override
    public ResponseEntity<AgencyPage> agencyPageSize(Integer page, Integer size) {
        Page<com.ajmanre.models.Agency> pg = agencyService.getPage(page, size);
        if(null != pg.getContent() && !pg.getContent().isEmpty()) {
            List<Agency> agencies = pg.getContent().stream().map(this::toOut).collect(Collectors.toList());
            return ResponseEntity.ok(new AgencyPage().page(page).size(pg.getSize()).data(agencies).total(pg.getTotalElements()));
        }
        return ResponseEntity.ok(new AgencyPage().page(page).size(0).data(null).total(0l));
    }

    @Override
    public ResponseEntity<MessageResponse> agencyPost(AgencyRequest agencyRequest) {
        com.ajmanre.models.Agency agency = new com.ajmanre.models.Agency();
        agency.setUpdatedAt(LocalDateTime.now());
        agency.setName(agencyRequest.getName());
        agency.setTel(agencyRequest.getTel());
        agency.setFacebook(agencyRequest.getFacebook());
        agency.setInsta(agencyRequest.getInsta());
        agency.setAbout(agencyRequest.getAbout());

        if(agencyRequest.getImage() != null) {
            File image = new File();
            image.setName(agencyRequest.getImage().getName());
            image.setLink(agencyRequest.getImage().getLink());
            agency.setImage(image);
        }

        if(agencyRequest.getAdresses() != null && !agencyRequest.getAdresses().isEmpty()) {
            List<com.ajmanre.models.Address> addresses = agencyRequest.getAdresses().stream().map(addr -> {
                com.ajmanre.models.Address address = new com.ajmanre.models.Address();
                address.setAdressLine1(addr.getAdressLine1());
                address.setAdressLine2(addr.getAdressLine2());
                address.setArea(addr.getArea());
                address.setCity(addr.getCity());
                address.setCountry(addr.getCountry());
                return address;
            }).collect(Collectors.toList());
            agency.setAddresses(addresses);
        }

        if(agencyRequest.getContacts() != null && !agencyRequest.getContacts().isEmpty()) {
            List<com.ajmanre.models.Contact> contacts = agencyRequest.getContacts().stream().map(con -> {
                com.ajmanre.models.Contact contact = new com.ajmanre.models.Contact();
                contact.setName(con.getName());
                contact.setTel(con.getTel());
                contact.setMobile(con.getMobile());
                contact.setEmail(con.getEmail());
                return contact;
            }).collect(Collectors.toList());
            agency.setContacts(contacts);
        }
        agency = agencyRepository.save(agency);

        return ResponseEntity.ok(new MessageResponse()
                .message("Agency created successfully")
                .identifier(agency.getId()));
    }

    @Override
    public ResponseEntity<MessageResponse> agencyPut(String id, Agency agencyRequest) {


        com.ajmanre.models.Agency agency = agencyRepository.findById(id).orElseThrow(() -> {
            return new RuntimeException("agency does not exist");
        });

        agency.setUpdatedAt(LocalDateTime.now());
        agency.setName(agencyRequest.getName());
        agency.setTel(agencyRequest.getTel());
        agency.setFacebook(agencyRequest.getFacebook());
        agency.setInsta(agencyRequest.getInsta());
        agency.setAbout(agencyRequest.getAbout());

        if(agencyRequest.getImage() != null) {
            File image = new File();
            image.setName(agencyRequest.getImage().getName());
            image.setLink(agencyRequest.getImage().getLink());
            agency.setImage(image);
        }
        if(agencyRequest.getAdresses() != null && !agencyRequest.getAdresses().isEmpty()) {
            List<com.ajmanre.models.Address> addresses = agencyRequest.getAdresses().stream().map(addr -> {
                com.ajmanre.models.Address address = new com.ajmanre.models.Address();
                address.setAdressLine1(addr.getAdressLine1());
                address.setAdressLine2(addr.getAdressLine2());
                address.setArea(addr.getArea());
                address.setCity(addr.getCity());
                address.setCountry(addr.getCountry());
                return address;
            }).collect(Collectors.toList());
            agency.setAddresses(addresses);
        }

        if(agencyRequest.getContacts() != null && !agencyRequest.getContacts().isEmpty()) {
            List<com.ajmanre.models.Contact> contacts = agencyRequest.getContacts().stream().map(con -> {
                com.ajmanre.models.Contact contact = new com.ajmanre.models.Contact();
                contact.setName(con.getName());
                contact.setTel(con.getTel());
                contact.setMobile(con.getMobile());
                contact.setEmail(con.getEmail());
                return contact;
            }).collect(Collectors.toList());
            agency.setContacts(contacts);
        }
        agency = agencyRepository.save(agency);

        return ResponseEntity.ok(new MessageResponse()
                .message("Agency updated successfully")
                .identifier(agency.getId()));
    }

    private Agency toOut(com.ajmanre.models.Agency agency) {

        Fileo image = null;
        if(agency.getImage() != null) {
            image = new Fileo().name(agency.getImage().getName()).link(agency.getImage().getLink());
        }

        List <Address> addresses = null;
        if(null != agency.getAddresses() && !agency.getAddresses().isEmpty()) {
            addresses = agency.getAddresses().stream().map(addr -> {
                return new Address().adressLine1(addr.getAdressLine1())
                        .adressLine2(addr.getAdressLine2()).area(addr.getArea())
                        .city(addr.getCity()).country(addr.getCountry());
            }).collect(Collectors.toList());
        }

        List<Contact> contacts = null;
        if( null != agency.getContacts() && !agency.getContacts().isEmpty()) {
            contacts = agency.getContacts().stream().map(con -> {
                return new Contact().name(con.getName())
                        .tel(con.getTel()).mobile(con.getMobile()).email(con.getEmail());
            }).collect(Collectors.toList());
        }

        return new Agency().id(agency.getId()).updatedAt(agency.getUpdatedAt().atOffset(ZoneOffset.UTC))
                .name(agency.getName()).tel(agency.getTel())
                .facebook(agency.getFacebook()).insta(agency.getInsta())
                .image(image).adresses(addresses).contacts(contacts);
    }

    @Override
    public ResponseEntity<List<Source>> agencySourceList() {

        List<com.ajmanre.models.Source> srcs = agencyRepository.getAgencySource();
        List<Source> sources = null;
        if( null != srcs) {
            sources = srcs.stream().map(s -> new Source().id(s.getId()).name(s.getName()))
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(sources);
    }
}
