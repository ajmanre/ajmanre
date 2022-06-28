package com.ajmanre.controllers;

import com.ajmanre.repository.EnquiryRepository;
import com.ajmanre.security.jwt.JwtUtils;
import com.ajmanre.services.EnquiryService;
import org.openapitools.model.*;
import org.openapitools.api.EnquiryApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
public class EnquiryController implements EnquiryApi {


    @Autowired
    EnquiryRepository enquiryRepository;

    @Autowired
    EnquiryService enquiryService;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public ResponseEntity<Enquiry> enquiryId(String enquiryId) {

        return ResponseEntity.ok(toOut(
                enquiryRepository.findById(enquiryId).orElseThrow(() -> {
                    return new RuntimeException("Enquiry does not exist");
                })
        ));
    }

    @Override
    public ResponseEntity<EnquiryPage> enquiryPageSize(Integer page, Integer size) {
        Page<com.ajmanre.models.Enquiry> pg = enquiryService.getPage(page, size);
        if(null != pg.getContent() && !pg.getContent().isEmpty()) {
            List<Enquiry> enquiries = pg.getContent().stream().map(this::toOut).collect(Collectors.toList());
            return ResponseEntity.ok(new EnquiryPage().page(page).size(pg.getSize()).data(enquiries).total(pg.getTotalElements()));
        }
        return ResponseEntity.ok(new EnquiryPage().page(page).size(0).data(null).total(0l));
    }

    @Override
    public ResponseEntity<MessageResponse> enquiryPost(Enquiry enquiry, String authorization) {
        com.ajmanre.models.Source user = null;
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String jwt = authorization.substring(7, authorization.length());
            final String username = jwtUtils.getUserNameFromJwtToken(jwt);
            final String userId = jwtUtils.getUserIdFromJwtClaim(jwt);
            user = new com.ajmanre.models.Source();
            user.setId(userId);
            user.setName(username);
        }

        com.ajmanre.models.Enquiry enq = toModel(enquiry, null);
        enq.setUpdatedAt(LocalDateTime.now());
        enq.setUser(user);
        enq = enquiryRepository.save(enq);

        return ResponseEntity.ok(new MessageResponse()
                .message("Enquiry created successfully")
                .identifier(enq.getId()));
    }

    @Override
    public ResponseEntity<MessageResponse> enquiryPut(String id, Enquiry enquiry) {
        com.ajmanre.models.Enquiry enq = enquiryRepository.findById(id).orElseThrow(() -> {
            return new RuntimeException("Enquiry does not exist");
        });

        enq = toModel(enquiry, enq);
        enq.setUpdatedAt(LocalDateTime.now());
        enq = enquiryRepository.save(enq);

        return ResponseEntity.ok(new MessageResponse()
                .message("Requirement updated successfully")
                .identifier(enq.getId()));
    }

    private com.ajmanre.models.Enquiry toModel(Enquiry enq, com.ajmanre.models.Enquiry enquiry) {
        enquiry = enquiry != null ? enquiry : new com.ajmanre.models.Enquiry();
        com.ajmanre.models.Contact contact = null;
        if(null != enq.getContact()) {
            Contact con = enq.getContact();
            contact = new com.ajmanre.models.Contact();
            contact.setName(con.getName());
            contact.setTel(con.getTel());
            contact.setMobile(con.getMobile());
            contact.setEmail(con.getEmail());
        }
        enquiry.setContact(contact);
        enquiry.setEnqType(enq.getEnqType());
        enquiry.setEnqTypeId(enq.getEnqTypeId());
        enquiry.setPropType(enq.getPropType());
        enquiry.setPropTypeId(enq.getPropTypeId());
        enquiry.setPriceFrom(enq.getPriceFrom());
        enquiry.setPriceTo(enq.getPriceTo());
        enquiry.setBedMin(enq.getBedMin());
        enquiry.setBedMax(enq.getBedMax());
        enquiry.setBathMin(enq.getBathMin());
        enquiry.setBathMax(enq.getBathMax());
        enquiry.setSqftMin(enq.getSqftMin());
        enquiry.setSqftMax(enq.getSqftMax());
        com.ajmanre.models.Address address = null;
        if(null != enq.getAddress()) {
            Address addr = enq.getAddress();
            address = new com.ajmanre.models.Address();
            address.setAdressLine1(addr.getAdressLine1());
            address.setAdressLine2(addr.getAdressLine2());
            address.setArea(addr.getArea());
            address.setCity(addr.getCity());
            address.setZip(addr.getZip());
            address.setCountry(addr.getCountry());
        }
        enquiry.setAddress(address);
        enquiry.setNotes(enq.getNotes());
        return  enquiry;
    }

    private Enquiry toOut(com.ajmanre.models.Enquiry enquiry) {
        OffsetDateTime updatedAt = null;
        if(null != enquiry.getUpdatedAt()) {
            updatedAt = enquiry.getUpdatedAt().atOffset(ZoneOffset.UTC);
        }
        Contact contact = null;
        if(null != enquiry.getContact()) {
            com.ajmanre.models.Contact con = enquiry.getContact();
            contact = new Contact().name(con.getName()).tel(con.getTel()).mobile(con.getMobile())
                .email(con.getEmail());
        }

        Address address = null;
        if(null != enquiry.getAddress()) {
            com.ajmanre.models.Address addr = enquiry.getAddress();
            address.setAdressLine1(addr.getAdressLine1());
            address.setAdressLine2(addr.getAdressLine2());
            address.setArea(addr.getArea());
            address.setCity(addr.getCity());
            address.setZip(addr.getZip());
            address.setCountry(addr.getCountry());
        }


        return new Enquiry().id(enquiry.getId()).updatedAt(updatedAt).contact(contact)
            .enqType(enquiry.getEnqType()).enqTypeId(enquiry.getEnqTypeId()).propType(enquiry.getPropType())
            .propTypeId(enquiry.getPropTypeId()).priceFrom(enquiry.getPriceFrom()).priceTo(enquiry.getPriceTo())
            .bedMin(enquiry.getBedMin()).bedMax(enquiry.getBedMax()).bathMin(enquiry.getBathMin())
            .bathMax(enquiry.getBathMax()).sqftMin(enquiry.getSqftMin()).sqftMax(enquiry.getSqftMax())
            .address(address).notes(enquiry.getNotes());
    }
}
