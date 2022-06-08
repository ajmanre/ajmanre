package com.ajmanre.controllers;

import com.ajmanre.repository.PropertyRepository;
import com.ajmanre.security.jwt.JwtUtils;
import org.openapitools.api.PropertyApi;
import org.openapitools.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
public class PropertyController implements PropertyApi {

    @Autowired
    PropertyRepository propertyRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public ResponseEntity<Property> propertyId(String propertyId) {
        com.ajmanre.models.Property prop = propertyRepository.findById(propertyId).orElseThrow(() -> {
            return new RuntimeException("property does not exist");
        });
        return ResponseEntity.ok(toOut(prop));
    }

    @Override
    public ResponseEntity<MessageResponse> propertyPost(Property property, String authorization) {

        com.ajmanre.models.Source user = null;
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String jwt = authorization.substring(7, authorization.length());
            final String username = jwtUtils.getUserNameFromJwtToken(jwt);
            final String userId = jwtUtils.getUserIdFromJwtClaim(jwt);
            user = new com.ajmanre.models.Source();
            user.setId(userId);
            user.setName(username);
        }

        com.ajmanre.models.Property prop = toModel(property, null);
        prop.setUpdatedAt(LocalDateTime.now());
        prop.setUser(user);
        prop = propertyRepository.save(prop);

        return ResponseEntity.ok(new MessageResponse()
                .message("Property created successfully")
                .identifier(prop.getId()));
    }

    @Override
    public ResponseEntity<MessageResponse> propertyPut(String id, Property property) {

        com.ajmanre.models.Property prop = propertyRepository.findById(id).orElseThrow(() -> {
            return new RuntimeException("property does not exist");
        });

        prop = toModel(property, prop);
        prop.setUpdatedAt(LocalDateTime.now());
        prop = propertyRepository.save(prop);

        return ResponseEntity.ok(new MessageResponse()
                .message("Property updated successfully")
                .identifier(prop.getId()));
    }

    @Override
    public ResponseEntity<PropertyPage> propertyRetrieve() {

        List<com.ajmanre.models.Property> props = propertyRepository.findRandomRecords();

        List<Property> properties = null;
        if(null != props && !props.isEmpty()) {
            properties = props.stream().map(this::toOut).collect(Collectors.toList());
        }

        long count = propertyRepository.count();

        return ResponseEntity.ok(new PropertyPage().data(properties).total(count));
    }


    private com.ajmanre.models.Property toModel(Property property, com.ajmanre.models.Property prop) {

        prop = prop == null ? new com.ajmanre.models.Property() : prop;

        prop.setIdentifier(property.getIdentifier());

        prop.setType(property.getType());
        prop.setTypeId(property.getTypeId());
        prop.setSubType(property.getSubType());

        prop.setStatus(property.getStatus());
        prop.setStatusId(property.getStatusId());

        prop.setSummary(property.getSummary());
        prop.setLocation(property.getLocation());
        prop.setLocationId(property.getLocationId());
        prop.setSqFt(property.getSqFt());
        prop.setSqMt(property.getSqMt());
        prop.setBedroom(property.getBedroom());
        prop.setFeatures(property.getFeatures());
        prop.setTags(property.getTags());

        if(null != property.getProject()) {
            Project proj = property.getProject();
            com.ajmanre.models.Project project = new com.ajmanre.models.Project();
            project.setName(proj.getName());
            project.setOverview(proj.getOverview());
            prop.setProject(project);
        }
        if(null != property.getAssocWith()) {
            Source src = property.getAssocWith();
            com.ajmanre.models.Source source = new com.ajmanre.models.Source();
            source.setId(src.getId());
            source.setName(src.getName());
            prop.setAssocWith(source);
        }
        if(null != property.getManagedBy()) {
            Source src = property.getManagedBy();
            com.ajmanre.models.Source source = new com.ajmanre.models.Source();
            source.setId(src.getId());
            source.setName(src.getName());
            prop.setManagedBy(source);
        }

        return prop;
    }

    private Property toOut(com.ajmanre.models.Property prop) {

        Project project = null;
        if(null != prop.getProject()) {
            com.ajmanre.models.Project proj = prop.getProject();
            project = new Project().name(proj.getName()).overview(proj.getOverview());
        }

        Source assocWith = null;
        if(null != prop.getAssocWith()) {
            com.ajmanre.models.Source asswth = prop.getAssocWith();
            assocWith = new Source().id(asswth.getId()).name(asswth.getName());
        }
        Source managedBy = null;
        if(null != prop.getManagedBy()) {
            com.ajmanre.models.Source mngdby = prop.getManagedBy();
            managedBy = new Source().id(mngdby.getId()).name(mngdby.getName());
        }

        return new Property().id(prop.getId()).updatedAt(prop.getUpdatedAt().atOffset(ZoneOffset.UTC))
                .identifier(prop.getIdentifier()).type(prop.getType()).typeId(prop.getTypeId())
                .subType(prop.getSubType()).status(prop.getStatus()).statusId(prop.getStatusId())
                .summary(prop.getSummary()).location(prop.getLocation()).locationId(prop.getLocationId())
                .sqFt(prop.getSqFt()).sqMt(prop.getSqMt()).bedroom(prop.getBedroom())
                .features(prop.getFeatures()).tags(prop.getTags())
                .project(project).assocWith(assocWith).managedBy(managedBy);
    }
}
