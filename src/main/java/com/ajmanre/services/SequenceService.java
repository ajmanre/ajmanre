package com.ajmanre.services;

import com.ajmanre.models.Sequence;
import com.ajmanre.repository.SequenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SequenceService {

    @Autowired
    SequenceRepository sequenceRepository;

    @Autowired
    MongoOperations mongoOperations;

    Object lock = new Object();

    public String nextIdentifier(String seqName) {
        synchronized (lock) {
            Sequence sequence = mongoOperations.findAndModify(Query.query(Criteria.where("name").is(seqName)),
                    new Update().inc("next",1), FindAndModifyOptions.options().returnNew(true).upsert(true),
                    Sequence.class);
            return String.valueOf(!Objects.isNull(sequence) ? sequence.getNext() : 1L);
        }
    }
}
