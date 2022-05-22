package com.ajmanre.controllers;

import com.ajmanre.models.PropertyType;
import com.ajmanre.repository.PropertyTypeRepository;
import com.ajmanre.services.PropertyTypeService;
import org.openapitools.api.PropApi;
import org.openapitools.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class PropertyTypeController implements PropApi{

    @Autowired
    PropertyTypeService propertyTypeService;

    @Autowired
    PropertyTypeRepository propertyTypeRepository;

    @Override
    public ResponseEntity<MessageResponse> propType(PropertyTypeRequest propertyTypeRequest) {

        PropertyType propertyType = null != propertyTypeRequest.getId() ?
                propertyTypeService.get(propertyTypeRequest.getId()) :
                propertyTypeRepository.findByType(propertyTypeRequest.getType()).orElse(new PropertyType());


        propertyType.setType(propertyTypeRequest.getType());
        propertyType.setOrder(propertyTypeRequest.getOrder());

        PropertyTypeChild child = propertyTypeRequest.getChild();
        if(null != child) {
            if (null == propertyType.getChildren()) {
                propertyType.setChildren(new ArrayList<>());
            }
            List<com.ajmanre.models.PropertyTypeChild> children = propertyType.getChildren();
            com.ajmanre.models.PropertyTypeChild look = null;

            while (null != child) {
                final String type = child.getType();
                Optional<com.ajmanre.models.PropertyTypeChild> find =
                        children.stream().filter(prpTypChld -> prpTypChld.getType().equals(type))
                                .findFirst();

                if (find.isPresent()) {
                    look = find.get();
                    look.setOrder(child.getOrder());
                } else {
                    look = new com.ajmanre.models.PropertyTypeChild();
                    look.setType(child.getType());
                    look.setOrder(child.getOrder());
                    children.add(look);
                }
                if (null != child.getChild() && look.getChildren() == null) {
                    look.setChildren(new ArrayList<>());
                    children = look.getChildren();
                }
                child = child.getChild();
            }
        }

        PropertyType saved = propertyTypeService.create(propertyType);

        return ResponseEntity.ok(
            new MessageResponse()
                .message(MessageFormat.format("{0} has been added", saved.getType()))
                .identifier(saved.getId())
        );
    }

    private List<PropertyTypeItem> trasform(List<com.ajmanre.models.PropertyTypeChild> childrn) {

        List<PropertyTypeItem> children = new ArrayList<>();
        for(com.ajmanre.models.PropertyTypeChild chld : childrn) {

            PropertyTypeItem child = new PropertyTypeItem()
                .type(chld.getType()).order(chld.getOrder());

            if(chld.getChildren() != null && !chld.getChildren().isEmpty()) {
                List<PropertyTypeItem> list = trasform(chld.getChildren());
                child.setChildren(list);
            }
            children.add(child);
        }

        return children;
    }

    @Override
    public ResponseEntity<List<PropertyTypeHierarchy>> propTypeHierarchy() {

        List<PropertyTypeHierarchy> propTypeHierarchy = propertyTypeRepository.findAll().stream().map(propType -> {

            PropertyTypeHierarchy propH = new PropertyTypeHierarchy().id(propType.getId())
                    .type(propType.getType()).order(propType.getOrder());

            if(null != propType.getChildren() && !propType.getChildren().isEmpty()) {
                List<com.ajmanre.models.PropertyTypeChild> childrn = propType.getChildren();
                List<PropertyTypeItem> children = trasform(childrn);
                propH.setChildren(children);
            }
            return propH;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(propTypeHierarchy);
    };

    /*@Override
    public ResponseEntity<MessageResponse> propType(PropertyTypeRequest propertyTypeRequest) {





        PropertyTypeRequest propRequestParent = propertyTypeRequest.getChild();
        if(null != propRequestParent) {
            PropTypeParent parent = new PropTypeParent();
            parent.setType(propRequestParent.getType());
            propertyType.setParent(parent);


            propRequestParent = propRequestParent.getParent();
            // parent may be child
            PropTypeParent child = parent;
            while(null != propRequestParent) {
                PropTypeParent propTypeParent = new PropTypeParent();
                propTypeParent.setType(propRequestParent.getType());
                child.setParent(propTypeParent);

                propRequestParent = propRequestParent.getParent();
                // parent may be child
                child = propTypeParent;
            }
        }

        PropertyType saved = propertyTypeService.create(propertyType);

        return ResponseEntity.ok(
                new MessageResponse()
                        .message("Property type has been added")
                        .identifier(saved.getType())
        );
    }*/
}
