package com.ajmanre.controllers;

import com.ajmanre.repository.SelectRepository;
import org.openapitools.api.SelectApi;
import org.openapitools.model.MessageResponse;
import org.openapitools.model.Select;
import org.openapitools.model.SelectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

public class SelectController implements SelectApi {

    @Autowired
    SelectRepository selectRepository;

    @Override
    public ResponseEntity<List<Select>> selectList(String _list) {

        List<Select> list = selectRepository.findByType(_list).stream().map(select -> new Select()
                .id(select.getId()).key(select.getKey()).value(select.getValue())
                .type(select.getType()).order(select.getOrder())).collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<MessageResponse> selectPost(SelectRequest selectRequest) {

        com.ajmanre.models.Select select = new com.ajmanre.models.Select();
        select.setKey(selectRequest.getKey());
        select.setValue(selectRequest.getValue());
        select.setOrder(selectRequest.getOrder());
        select.setType(selectRequest.getType());

        select = selectRepository.save(select);
        return ResponseEntity.ok(new MessageResponse().message("sellection added").identifier(select.getId()));
    }
}
