package com.ajmanre.controllers;

import com.ajmanre.models.PropertyType;
import com.ajmanre.services.PropertyTypeService;
import org.openapitools.api.PropApi;
import org.openapitools.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class PropertyTypeController implements PropApi{

    @Autowired
    PropertyTypeService propertyTypeService;

    @Override
    public ResponseEntity<MessageResponse> propType(PropertyTypeRequest propertyTypeRequest) {

        PropertyType propertyType = null != propertyTypeRequest.getId() ?
            propertyTypeService.get(propertyTypeRequest.getId()) : new PropertyType();

        propertyType.setType(propertyTypeRequest.getType());
        propertyType.setOrder(propertyTypeRequest.getOrder());

        PropertyTypeChild child = propertyTypeRequest.getChild();
        if(null != child) {
            com.ajmanre.models.PropertyTypeChild propChild = new com.ajmanre.models.PropertyTypeChild();
            propChild.setType(child.getType());
            propChild.setOrder(child.getOrder());
            propertyType.setChild(propChild);

            // let propChild is parent of children
            com.ajmanre.models.PropertyTypeChild parent = propChild;
            child = child.getChild();
            while (null != child) {
                com.ajmanre.models.PropertyTypeChild whilePropChild = new com.ajmanre.models.PropertyTypeChild();
                whilePropChild.setType(child.getType());
                whilePropChild.setOrder(child.getOrder());
                parent.setChild(whilePropChild);

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

    @Override
    public ResponseEntity<List<PropertyTypeHierarchy>> propTypeHierarchy() {

        List<PropertyTypeHierarchy> propTypeHierarchy = propertyTypeService.getPropTypes().entrySet()
                .stream().map(entry -> {
                    PropertyTypeHierarchy h = new PropertyTypeHierarchy();
                    h.setType(entry.getKey());
                    h.setOrder(entry.getValue().get(0).getOrder());

                    List<PropertyTypeItem> children =  entry.getValue().stream().map(propertyType -> {

                        com.ajmanre.models.PropertyTypeChild child = propertyType.getChild();
                        PropertyTypeItem propertyTypeItem = new PropertyTypeItem().type(child.getType())
                                .order(child.getOrder());

                        PropertyTypeItem parent = propertyTypeItem;
                        com.ajmanre.models.PropertyTypeChild nextChild  = child.getChild();
                        while(null != nextChild) {
                            PropertyTypeItem nextChildPropertyTypeItem = new PropertyTypeItem()
                                    .type(nextChild.getType()).order(nextChild.getOrder());
                            parent.setChildren(Arrays.asList(nextChildPropertyTypeItem));

                            parent = nextChildPropertyTypeItem;
                            nextChild  = nextChild.getChild();
                        }

                        return propertyTypeItem;
                    }).collect(Collectors.toList());
                    h.setChildren(children);
                    return h;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(propTypeHierarchy);
    }

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
