package com.gdn.warehouse.assetsmanagement.repository.impl;

import com.gdn.warehouse.assetsmanagement.entity.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Objects;

@Component
public abstract class GenericCustomRepository {
   @Autowired
   private ReactiveMongoTemplate reactiveMongoTemplate;

   @Autowired
   private ReactiveMongoOperations reactiveMongoOperations;

   protected  <T extends BaseEntity> Mono<Page<T>> findByCriteria(Class<T> clazz, Criteria criteria,
                                                                  Integer limit, Integer page, Sort sort) {
      final Integer validLimit = getDenullifiedLimit(limit);
      final Integer validPage = getDenullifiedPage(page);
      Pageable pageable = PageRequest.of(validPage-1, validLimit, sort);
      Query query = new Query();
      query.addCriteria(criteria);
      query.with(pageable);
      return reactiveMongoTemplate.find(query, clazz)
            .collectList()
            .flatMap(results ->
                  countByCriteria(clazz,criteria)
                        .map(count->
                              PageableExecutionUtils.getPage(results, pageable, () -> count)))
            .switchIfEmpty(Mono.fromSupplier(()-> new PageImpl<>(new ArrayList<>(), PageRequest.of(validPage-1, validLimit),0L)));
   }

   protected Mono<Long> countByCriteria(Class clazz, Criteria criteria){
      Query query = new Query();
      query.addCriteria(criteria);
      return reactiveMongoOperations.count(query,clazz);
   }

   protected <T, R extends BaseEntity> Mono<Page<T>> findByAggregation(Aggregation aggregation,
                                                                       Aggregation countAggregation, Class<R> requestClass, Class<T> responseClass, Integer limit,
                                                                       Integer page, Sort sort) {

      final Integer validLimit = getDenullifiedLimit(limit);
      final Integer validPage = getDenullifiedPage(page);
      Pageable pageable = PageRequest.of(validPage-1, validLimit, sort);
      return reactiveMongoTemplate
            .aggregate(aggregation, requestClass, responseClass).collectList()
            .flatMap(results -> countByAggregation(countAggregation,requestClass,responseClass)
                  .map(count -> PageableExecutionUtils.getPage(results, pageable, () -> count)))
            .switchIfEmpty(
                  Mono.fromSupplier(() -> new PageImpl<>(new ArrayList<>(), PageRequest.of(validPage-1, validLimit), 0L)));

   }
   private <R extends BaseEntity, T> Mono<Long> countByAggregation(Aggregation aggregation,Class<R> requestClass,Class<T> responseClass) {
      return reactiveMongoTemplate.aggregate(aggregation,requestClass,responseClass)
            .collectList().map(results -> Long.valueOf(results.size()));
   }

   private Integer getDenullifiedLimit(Integer limit){
      if(Objects.isNull(limit)){
         limit = Integer.MAX_VALUE;
      }
      return limit;
   }

   private Integer getDenullifiedPage(Integer page){
      if(Objects.isNull(page)){
         page = 1;
      }
      return page;
   }

   protected Mono<Object> genericFindBy(String key, String value, Class entityClass) {
      Query query = new Query(Criteria.where(key).is(value));
      return reactiveMongoOperations.findOne(query, entityClass);
   }

   protected Mono<Object> genericSave(Object obj) {
      return reactiveMongoOperations.save(obj);
   }
}