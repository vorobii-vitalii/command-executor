package org.vitalii.vorobii.repository;

import org.springframework.data.repository.CrudRepository;
import org.vitalii.vorobii.entity.RedisCommand;

import java.util.UUID;

public interface CommandRepository extends CrudRepository<RedisCommand, UUID> {

}
