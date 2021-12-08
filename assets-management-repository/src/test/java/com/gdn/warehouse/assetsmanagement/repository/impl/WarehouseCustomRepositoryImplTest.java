package com.gdn.warehouse.assetsmanagement.repository.impl;

import com.gdn.warehouse.assetsmanagement.entity.Warehouse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WarehouseCustomRepositoryImplTest {

   @InjectMocks
   private WarehouseCustomRepositoryImpl repo;

   @Mock
   private ReactiveMongoTemplate mongoTemplate;

   @Mock
   private ReactiveMongoOperations mongoOperations;

   private Sort sort;
   private Criteria criteria;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      sort = Sort.by(Sort.DEFAULT_DIRECTION,"test");
      criteria = new Criteria();
   }

   @Test
   public void findByCriteria() {
      when(mongoTemplate.find(any(Query.class),eq(Warehouse.class))).thenReturn(Flux.just(new Warehouse()));
      when(mongoOperations.count(any(Query.class),eq(Warehouse.class))).thenReturn(Mono.just(1L));
      Page<Warehouse> warehouses = repo.findByCriteria(criteria,1,1,sort).block();
      Assert.assertEquals(1,warehouses.get().collect(Collectors.toList()).size());
      verify(mongoTemplate).find(any(Query.class),eq(Warehouse.class));
      verify(mongoOperations).count(any(Query.class),eq(Warehouse.class));
   }
}