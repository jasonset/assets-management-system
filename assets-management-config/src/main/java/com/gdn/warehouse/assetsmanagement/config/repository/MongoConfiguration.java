package com.gdn.warehouse.assetsmanagement.config.repository;

import com.gdn.warehouse.assetsmanagement.properties.MongoDBProperties;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.gdn.warehouse.assetsmanagement")
@ConditionalOnClass(MongoDBProperties.class)
@Slf4j
public class MongoConfiguration {
   @Autowired
   private MongoDBProperties mongoDBProperties;

   @Bean
   public MongoClient reactiveMongoClient() {
      log.info("initializing Mongo: {}", mongoDBProperties);
      MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .credential(MongoCredential.createScramSha1Credential(
                  mongoDBProperties.getUsername(),
                  mongoDBProperties.getDatabase(),
                  mongoDBProperties.getPassword().toCharArray()))
            .applyToClusterSettings(builder -> builder.hosts(getServerAddresses()))
            .applyToConnectionPoolSettings(builder -> builder
                  .maxWaitTime(mongoDBProperties.getMaxWaitTime(), TimeUnit.MILLISECONDS)
                  .maxConnectionLifeTime(mongoDBProperties.getMaxConnectionLifeTime(), TimeUnit.MILLISECONDS)
                  .maxConnectionIdleTime(mongoDBProperties.getMaxConnectionIdleTime(), TimeUnit.MILLISECONDS)
                  .minSize(mongoDBProperties.getMinConnectionsPerHost())
                  .maxSize(mongoDBProperties.getMaxConnectionsPerHost()))
            .applyToSocketSettings(builder -> builder
                  .readTimeout(mongoDBProperties.getReadTimeout(), TimeUnit.MILLISECONDS)
                  .connectTimeout(mongoDBProperties.getConnectTimeout(), TimeUnit.MILLISECONDS))
            .applyToServerSettings(builder -> builder
                  .heartbeatFrequency(mongoDBProperties.getHeartbeatFrequency(), TimeUnit.MILLISECONDS)
                  .minHeartbeatFrequency(mongoDBProperties.getMinHeartbeatFrequency(), TimeUnit.MILLISECONDS))
            .build();
      return MongoClients.create(mongoClientSettings);
   }

   private List<ServerAddress> getServerAddresses() {
      ServerAddress primaryAddress = new ServerAddress(mongoDBProperties.getPrimary().getHost(),
            mongoDBProperties.getPrimary().getPort());
      ServerAddress secondaryAddress = new ServerAddress(mongoDBProperties.getSecondary().getHost(),
            mongoDBProperties.getSecondary().getPort());
      return Arrays.asList(primaryAddress, secondaryAddress);
   }

   @Bean
   public ReactiveMongoTemplate reactiveMongoTemplate(@Qualifier("reactiveMongoClient") MongoClient reactiveMongoClient,
                                                      MappingMongoConverter mappingMongoConverter) throws Exception {
      log.info("initializing mongo template: {} {}", reactiveMongoClient, mappingMongoConverter);
      return new ReactiveMongoTemplate(new SimpleReactiveMongoDatabaseFactory(reactiveMongoClient,
            mongoDBProperties.getDatabase()), mappingMongoConverter);
   }

   @Bean
   public ReactiveMongoOperations reactiveMongoOperations(ReactiveMongoTemplate reactiveMongoTemplate) throws Exception {
      return reactiveMongoTemplate;
   }
}
