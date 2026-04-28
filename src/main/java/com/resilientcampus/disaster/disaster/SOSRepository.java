package com.resilientcampus.disaster.disaster;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
// CRITICAL: This MUST say 'interface', not 'class'
public interface SOSRepository extends MongoRepository<SOSRequest, String> {
}