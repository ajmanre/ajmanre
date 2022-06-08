package com.ajmanre.controllers;

import com.ajmanre.repository.RequirementRepository;
import com.ajmanre.security.jwt.JwtUtils;
import com.ajmanre.services.RequirementService;
import org.openapitools.api.RequirementApi;
import org.openapitools.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
public class RequirementController implements RequirementApi {

    @Autowired
    RequirementRepository requirementRepository;

    @Autowired
    RequirementService requirementService;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public ResponseEntity<Requirement> reqId(String reqId) {
        com.ajmanre.models.Requirement req = requirementRepository.findById(reqId).orElseThrow(() -> {
            return new RuntimeException("requirement does not exist");
        });
        return ResponseEntity.ok(toOut(req));
    }

    @Override
    public ResponseEntity<RequirementPage> requirementPageSize(Integer page, Integer size) {
        Page<com.ajmanre.models.Requirement> pg = requirementService.getPage(page, size);
        if(null != pg.getContent() && !pg.getContent().isEmpty()) {
            List<Requirement> requirements = pg.getContent().stream().map(this::toOut).collect(Collectors.toList());
            return ResponseEntity.ok(new RequirementPage().page(page).size(pg.getSize()).data(requirements).total(pg.getTotalElements()));
        }
        return ResponseEntity.ok(new RequirementPage().page(page).size(0).data(null).total(0l));
    }

    @Override
    public ResponseEntity<MessageResponse> requirementPost(Requirement requirement, String authorization) {
        com.ajmanre.models.Source user = null;
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String jwt = authorization.substring(7, authorization.length());
            final String username = jwtUtils.getUserNameFromJwtToken(jwt);
            final String userId = jwtUtils.getUserIdFromJwtClaim(jwt);
            user = new com.ajmanre.models.Source();
            user.setId(userId);
            user.setName(username);
        }

        com.ajmanre.models.Requirement req = toModel(requirement, null);
        req.setUpdatedAt(OffsetDateTime.now());
        req.setPostedDate(OffsetDateTime.now());
        req.setPostedBy(user);
        req = requirementRepository.save(req);

        return ResponseEntity.ok(new MessageResponse()
                .message("Requirement created successfully")
                .identifier(req.getId()));
    }

    @Override
    public ResponseEntity<MessageResponse> requirementPut(String id, Requirement requirement) {
        com.ajmanre.models.Requirement req = requirementRepository.findById(id).orElseThrow(() -> {
            return new RuntimeException("Requirement does not exist");
        });

        req = toModel(requirement, req);
        req.setUpdatedAt(OffsetDateTime.now());
        req = requirementRepository.save(req);

        return ResponseEntity.ok(new MessageResponse()
                .message("Requirement updated successfully")
                .identifier(req.getId()));
    }

    private com.ajmanre.models.Requirement toModel(Requirement req, com.ajmanre.models.Requirement requirement) {

        requirement = requirement == null ? new com.ajmanre.models.Requirement() : requirement;

        requirement.setName(req.getName());
        requirement.setEmail(req.getEmail());
        requirement.setWhatsapp(req.getWhatsapp());
        requirement.setPhoneNo(req.getPhoneNo());
        requirement.setMessage(req.getMessage());
        requirement.setFeatured(req.getFeatured());
        requirement.setListingExpDate(req.getListingExpDate());
        requirement.setPostedDate(req.getPostedDate());

        if(req.getPostedBy() != null) {
            com.ajmanre.models.Source postedby = new com.ajmanre.models.Source();
            postedby.setId(req.getPostedBy().getId());
            postedby.setName(req.getPostedBy().getName());
            requirement.setPostedBy(postedby);
        }
        return requirement;
    }

    private Requirement toOut(com.ajmanre.models.Requirement req) {

        Source postedBy = null;
        if(null != req.getPostedBy()) {
            com.ajmanre.models.Source postBy = req.getPostedBy();
            postedBy = new Source().name(postBy.getName()).id(postBy.getId());
        }

        return  new Requirement().id(req.getId()).updatedAt(req.getUpdatedAt())
                .name(req.getName()).email(req.getEmail()).phoneNo(req.getPhoneNo())
                .whatsapp(req.getWhatsapp()).message(req.getMessage())
                .featured(req.getFeatured()).listingExpDate(req.getListingExpDate())
                .postedDate(req.getPostedDate()).postedBy(postedBy);
    }
}
