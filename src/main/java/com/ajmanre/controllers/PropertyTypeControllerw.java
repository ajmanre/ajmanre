package com.ajmanre.controllers;

import com.ajmanre.models.PropertyType;
import com.ajmanre.repository.PropertyTypeRepository;
import com.ajmanre.services.PropertyTypeService;
import org.openapitools.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PropertyTypeControllerw {

    @Autowired
    PropertyTypeService propertyTypeService;

    @Autowired
    PropertyTypeRepository propertyTypeRepository;

    public ResponseEntity<MessageResponse> propType(PropertyTypeRequest propertyTypeRequest) {

        PropertyType propertyType = null != propertyTypeRequest.getId() ?
                propertyTypeService.get(propertyTypeRequest.getId()) :
                propertyTypeRepository.findByType(propertyTypeRequest.getType()).orElse(new PropertyType());

        propertyType.setType(propertyTypeRequest.getType());
        propertyType.setOrder(propertyTypeRequest.getOrder());

        PropertyTypeChild child = propertyTypeRequest.getChild();
        if(null != child) {
            com.ajmanre.models.PropertyTypeChild propChild = new com.ajmanre.models.PropertyTypeChild();
            propChild.setType(child.getType());
            propChild.setOrder(child.getOrder());
            //propertyType.setChild(propChild);

            // let propChild is parent of children
            com.ajmanre.models.PropertyTypeChild parent = propChild;
            child = child.getChild();
            while (null != child) {
                com.ajmanre.models.PropertyTypeChild whilePropChild = new com.ajmanre.models.PropertyTypeChild();
                whilePropChild.setType(child.getType());
                whilePropChild.setOrder(child.getOrder());
                //parent.setChild(whilePropChild);

                // let whilePropChild is parent of children
                parent = whilePropChild;
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

    public ResponseEntity<List<PropertyTypeHierarchy>> propTypeHierarchy() {
        List<PropertyTypeHierarchy> propTypeHierarchy = propertyTypeService.getPropTypes().entrySet()
                .stream().map(entry -> {
                    PropertyTypeHierarchy tree = new PropertyTypeHierarchy();
                    tree.setType(entry.getKey());
                    tree.setOrder(entry.getValue().get(0).getOrder());
                    return tree;
                }).sorted((o1,o2) -> o1.getOrder().compareTo(o2.getOrder())).collect(Collectors.toList());

        propertyTypeRepository.findAll().stream().forEach(propType -> {

            PropertyTypeHierarchy tree = propTypeHierarchy.stream().filter(node -> node.getType().equals(propType.getType())).findFirst()
                    .orElseThrow(() -> new RuntimeException(String.format("Error: %s type is not found.", propType.getType())));

            List<PropertyTypeItem> children = tree.getChildren();
            List<PropertyTypeItem> parents = null;

            com.ajmanre.models.PropertyTypeChild child = null;// propType.getChild();
            if(child != null) {
                if(children == null) {
                    children = new ArrayList<>();
                    tree.children(children);
                }
                // let children be parents
                parents = children;
            }

            while(child != null) {
                final String type = child.getType();

                PropertyTypeItem propertyTypeItem = null;

                Optional<PropertyTypeItem> opt = parents.stream()
                        .filter(item -> item.getType().equals(type))
                        .findFirst();
                if(!opt.isPresent()) {
                    propertyTypeItem = new PropertyTypeItem()
                            .type(child.getType()).order(child.getOrder());
                    parents.add(propertyTypeItem);
                } else {
                    propertyTypeItem = opt.get();
                }
                if(child.toString() != null) { //if(child.getChild() != null) {
                    if(propertyTypeItem.getChildren() == null) {
                        propertyTypeItem.children(new ArrayList<>());
                    }
                    parents = propertyTypeItem.getChildren();
                }
                child = null; //child.getChild();
            }
        });

        return ResponseEntity.ok(propTypeHierarchy);
    };
}
