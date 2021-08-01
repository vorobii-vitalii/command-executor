package org.vitalii.vorobii.config.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "org.vitalii.vorobii")
public class MongoConfig {


}
