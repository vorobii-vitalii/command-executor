package org.vitalii.vorobii.config.dao;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.vitalii.vorobii.config.entity.MongoDoneCommand;

import java.util.List;

public interface DoneCommandRepository extends MongoRepository<MongoDoneCommand, ObjectId> {
    List<MongoDoneCommand> findByClientId(String clientId);
}
