package com.ajmanre.controllers;

import com.ajmanre.models.File;
import com.ajmanre.repository.PropertyRepository;
import com.ajmanre.security.jwt.JwtUtils;
import com.ajmanre.services.PropertyService;
import com.ajmanre.services.SequenceService;
import com.ajmanre.services.UserService;
import org.openapitools.api.PropertyApi;
import org.openapitools.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
public class PropertyController implements PropertyApi {

    @Autowired
    PropertyRepository propertyRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    @Autowired
    SequenceService sequenceService;

    @Autowired
    PropertyService propertyService;

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
        prop.setIdentifier(sequenceService.nextIdentifier(com.ajmanre.models.Property.SEQUENCE));
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
    public ResponseEntity<PropertyPage> propertyRetrieve(PropertySpec spec) {

        /*List<com.ajmanre.models.Property> props = propertyRepository.findRandomRecords();
        List<Property> properties = null;
        if(null != props && !props.isEmpty()) {
            properties = props.stream().map(this::toOut).collect(Collectors.toList());
        }
        long count = propertyRepository.count();*/

        List<com.ajmanre.query.TypeRel> types = null;
        if(null != spec.getTypes() && !spec.getTypes().isEmpty()) {
            types = spec.getTypes().stream().map(s -> com.ajmanre.query.TypeRel.builder()
                .typeId(s.getTypeId()).type(s.getType()).subType(s.getSubType()).build())
                .collect(Collectors.toList());
        }

        com.ajmanre.query.PropertyResult result = propertyService.propertyRetrieve(com.ajmanre.query.PropertySpec.builder()
                .bedroom(spec.getBedroom()).locationIds(spec.getLocationIds())
                .statusIds(spec.getStatusIds()).types(types).build());

        List<Property> properties = result.getData().stream().map(this::toOut).collect(Collectors.toList());

        return ResponseEntity.ok(new PropertyPage().data(properties).total(result.getTotal()));
    }

    @Override
    public ResponseEntity<List<Property>> agencyPropListing(String authorization) {
        String userId = null;
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String jwt = authorization.substring(7, authorization.length());
            userId = jwtUtils.getUserIdFromJwtClaim(jwt);
        }

        Optional<com.ajmanre.models.Source> agncyOpt = userService.getUserAgency(userId);
        if(agncyOpt.isPresent()) {
            List<Property> agencyListing = propertyRepository.findByAssocWithId(agncyOpt.get().getId())
                .stream().map(this::toOut).collect(Collectors.toList());
            return ResponseEntity.ok(agencyListing);
        }

        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<Property>> myPropListing(String authorization) {
        String userId = null;
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String jwt = authorization.substring(7, authorization.length());
            userId = jwtUtils.getUserIdFromJwtClaim(jwt);
        }

        List<Property> myListing = propertyRepository.findByUserId(userId)
                .stream().map(this::toOut).collect(Collectors.toList());

        return ResponseEntity.ok(myListing);
    }

    @Override
    public ResponseEntity<List<Property>> propListingByAgency(String agencyId) {
        List<Property> agencyListing = propertyRepository.findByAssocWithId(agencyId)
                .stream().map(this::toOut).collect(Collectors.toList());
        return ResponseEntity.ok(agencyListing);
    }

    private com.ajmanre.models.Property toModel(Property property, com.ajmanre.models.Property prop) {

        prop = prop == null ? new com.ajmanre.models.Property() : prop;

        prop.setTitle(property.getTitle());
        prop.setStatus(property.getStatus());
        prop.setStatusId(property.getStatusId());
        prop.setType(property.getType());
        prop.setTypeId(property.getTypeId());
        prop.setSubType(property.getSubType());
        prop.setLocation(property.getLocation());
        prop.setLocationId(property.getLocationId());
        prop.setPropFor(property.getPropFor());
        prop.setPropForId(property.getPropForId());
        prop.setRentingType(property.getRentingType());
        prop.setRentingTypeId(property.getRentingTypeId());
        prop.setDescription(property.getDescription());
        prop.setPrice(property.getPrice());
        prop.setSqFt(property.getSqFt());
        prop.setSqMt(property.getSqMt());
        prop.setBuildupArea(property.getBuildupArea());
        prop.setTotalLandArea(property.getTotalLandArea());
        prop.setBathrooms(property.getBathrooms());
        prop.setBedrooms(property.getBedrooms());
        prop.setYearBuilt(property.getYearBuilt());
        prop.setRooms(property.getRooms());
        prop.setParking(property.getParking());
        prop.setGarage(property.getGarage());
        prop.setGarageSize(property.getGarageSize());

        LocalDateTime availabilityDate = null;
        if(null != property.getAvailabilityDate()) {
            prop.setAvailabilityDate(property.getAvailabilityDate().toLocalDateTime());
        }


        prop.setFeatures(property.getFeatures());
        prop.setGenFeatures(property.getGenFeatures());
        prop.setNearby(property.getNearby());
        prop.setTags(property.getTags());
        prop.setTel(property.getTel());
        prop.setPhoneNo(property.getPhoneNo());

        if(null != property.getAddress()) {
            Address addr = property.getAddress();
            com.ajmanre.models.Address address = new com.ajmanre.models.Address();
            address.setAdressLine1(addr.getAdressLine1());
            address.setAdressLine2(addr.getAdressLine2());
            address.setArea(addr.getArea());
            address.setCity(addr.getCity());
            address.setZip(addr.getZip());
            address.setCountry(addr.getCountry());
            prop.setAddress(address);
        }

        if(null != property.getFloorPlan()) {
            FloorPlan flrPln = property.getFloorPlan();
            com.ajmanre.models.FloorPlan floorPlan = new com.ajmanre.models.FloorPlan();
            floorPlan.setTitle(flrPln.getTitle());
            floorPlan.setBedroom(flrPln.getBedroom());
            floorPlan.setBathroom(flrPln.getBathroom());
            floorPlan.setPrice(flrPln.getPrice());
            floorPlan.setPostFixPrice(flrPln.getPostFixPrice());
            floorPlan.setPlanSize(flrPln.getPlanSize());
            if(null != flrPln.getPlanImage()) {
                Fileo plnImg = flrPln.getPlanImage();
                File planImage = new File();
                planImage.setName(plnImg.getName());
                planImage.setLink(planImage.getLink());
                floorPlan.setPlanImage(planImage);
            }
            floorPlan.setDescription(flrPln.getDescription());
            prop.setFloorPlan(floorPlan);
        }
        prop.setDisclaimer(property.getDisclaimer());

        if(null != property.getProject()) {
            Project proj = property.getProject();
            com.ajmanre.models.Project project = new com.ajmanre.models.Project();
            project.setName(proj.getName());
            project.setOverview(proj.getOverview());
            prop.setProject(project);
        }
        if(null != property.getFiles() && !property.getFiles().isEmpty()) {
            List<File> files = property.getFiles().stream().map(f -> {
                com.ajmanre.models.File file = new com.ajmanre.models.File();
                file.setLink(f.getLink());
                file.setName(f.getName());
                return file;
            }).collect(Collectors.toList());
            prop.setFiles(files);
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

        OffsetDateTime updatedAt = null;
        if(null != prop.getUpdatedAt()) {
            updatedAt = prop.getUpdatedAt().atOffset(ZoneOffset.UTC);
        }

        OffsetDateTime availabilityDate = null;
        if(null != prop.getUpdatedAt()) {
            availabilityDate = prop.getUpdatedAt().atOffset(ZoneOffset.UTC);
        }

        Address address = null;
        if(null != prop.getAddress()) {
            com.ajmanre.models.Address addr = prop.getAddress();
            address = new Address().adressLine1(addr.getAdressLine1()).adressLine2(addr.getAdressLine2())
                .area(addr.getArea()).city(addr.getCity()).zip(addr.getZip()).country(addr.getCountry());
        }

        FloorPlan floorPlan = null;
        if(null != prop.getFloorPlan()) {
            com.ajmanre.models.FloorPlan flrPln = prop.getFloorPlan();
            Fileo planImage = null;
            if(null != flrPln.getPlanImage()) {
                com.ajmanre.models.File plnImg = flrPln.getPlanImage();
                planImage = new Fileo().name(plnImg.getName()).link(plnImg.getLink());
            }
            floorPlan = new FloorPlan().title(flrPln.getTitle()).bedroom(flrPln.getBedroom())
                .bathroom(flrPln.getBathroom()).price(flrPln.getPrice()).postFixPrice(flrPln.getPostFixPrice())
                .planSize(flrPln.getPlanSize()).description(flrPln.getDescription()).planImage(planImage);
        }

        Project project = null;
        if(null != prop.getProject()) {
            com.ajmanre.models.Project proj = prop.getProject();
            project = new Project().name(proj.getName()).overview(proj.getOverview());
        }

        List<Fileo> files = null;
        if(null != prop.getFiles() && !prop.getFiles().isEmpty()) {
            files = prop.getFiles().stream()
                .map(f -> new Fileo().link(f.getLink()).name(f.getName()))
                .collect(Collectors.toList());
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

        // User has not been exposed in API response

        return new Property().id(prop.getId()).identifier(prop.getIdentifier())
            .status(prop.getStatus()).statusId(prop.getStatusId())
            .propFor(prop.getPropFor()).propForId(prop.getPropForId())
            .rentingType(prop.getRentingType()).rentingTypeId(prop.getRentingTypeId())
            .type(prop.getType()).typeId(prop.getTypeId()).subType(prop.getSubType())
            .title(prop.getTitle()).location(prop.getLocation()).locationId(prop.getLocationId())
            .description(prop.getDescription()).price(prop.getPrice()).sqFt(prop.getSqFt()).sqMt(prop.getSqMt())
            .buildupArea(prop.getBuildupArea()).totalLandArea(prop.getTotalLandArea())
            .bathrooms(prop.getBathrooms()).bedrooms(prop.getBedrooms()).yearBuilt(prop.getYearBuilt())
            .rooms(prop.getRooms()).parking(prop.getParking()).garage(prop.getGarage())
            .garageSize(prop.getGarageSize()).tel(prop.getTel()).phoneNo(prop.getPhoneNo())
            .disclaimer(prop.getDisclaimer()).updatedAt(updatedAt).availabilityDate(availabilityDate)
            .features(prop.getFeatures()).genFeatures(prop.getGenFeatures()).nearby(prop.getNearby())
            .tags(prop.getTags()).address(address).floorPlan(floorPlan).project(project).files(files)
            .assocWith(assocWith).managedBy(managedBy);
    }
}
