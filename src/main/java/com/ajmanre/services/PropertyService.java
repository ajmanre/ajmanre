package com.ajmanre.services;

import com.ajmanre.models.Property;
import com.ajmanre.query.PropertyResult;
import com.ajmanre.query.PropertySpec;
import com.ajmanre.query.TypeRel;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PropertyService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    MongoOperations mongoOperations;

    public PropertyResult propertyRetrieve(PropertySpec spec) {

        List<Criteria> criteriaList = new ArrayList<>();
        if(spec.getBedroom() != null) {
            criteriaList.add(Criteria.where("bedroom").is(spec.getBedroom()));
        }
        if(spec.getTypes() != null && !spec.getTypes().isEmpty()) {
            spec.getTypes().forEach(rel -> {
                criteriaList.add(Criteria.where("typeId").is(rel.getTypeId()));
                criteriaList.add(Criteria.where("type").is(rel.getType()));
                criteriaList.add(Criteria.where("subType").is(rel.getSubType()));
            });
        }
        if(spec.getLocationIds() != null && !spec.getLocationIds().isEmpty()) {
            criteriaList.add(Criteria.where("locationId").in(spec.getLocationIds()));
        }
        if(spec.getStatusIds() != null && !spec.getStatusIds().isEmpty()) {
            criteriaList.add(Criteria.where("statusId").in(spec.getStatusIds()));
        }

        Long count = 0l;
        if(!criteriaList.isEmpty()) {
            Query query = new Query(new Criteria().orOperator(criteriaList));
            count = mongoOperations.count(query, Property.class);
        } else {
            count = mongoOperations.estimatedCount(Property.class);
        }

        if(count > 0) {
            List<AggregationOperation> operations = new ArrayList<>();
            if(!criteriaList.isEmpty()) {
                operations.add(Aggregation.match(new Criteria().orOperator(criteriaList)));
            }

            AggregationOperation sample = Aggregation.sample(20);
            operations.add(sample);

            Aggregation aggregation = Aggregation.newAggregation(operations);
            List<Property> properties = mongoTemplate.aggregate(aggregation, Property.class, Property.class)
                    .getMappedResults();
            return PropertyResult.builder().data(properties).total(count).build();
        }
        return PropertyResult.builder().data(Collections.emptyList()).total(count).build();
    }
}
