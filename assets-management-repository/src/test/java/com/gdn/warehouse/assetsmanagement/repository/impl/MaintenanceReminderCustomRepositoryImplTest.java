package com.gdn.warehouse.assetsmanagement.repository.impl;

import com.gdn.warehouse.assetsmanagement.entity.Maintenance;
import com.gdn.warehouse.assetsmanagement.entity.MaintenanceReminder;
import com.gdn.warehouse.assetsmanagement.repository.request.GetMaintenanceReminderCriteriaRequest;
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
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MaintenanceReminderCustomRepositoryImplTest {

   @InjectMocks
   private MaintenanceReminderCustomRepositoryImpl repo;

   @Mock
   private ReactiveMongoTemplate mongoTemplate;

   @Mock
   private ReactiveMongoOperations mongoOperations;

   private GetMaintenanceReminderCriteriaRequest request,requestNoFilter;
   private Sort sort;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      request = GetMaintenanceReminderCriteriaRequest.builder()
            .assetNumberFilter("ASSET-NUMBER").intervalFilter(1)
            .maintenanceReminderNumberFilter("MR-NUMBER").itemCodeFilter("ITEM-CODE")
            .emailFilter("EMAIL").scheduledDateFilter(new Date()).build();
      requestNoFilter = GetMaintenanceReminderCriteriaRequest.builder().build();
      sort = Sort.by(Sort.DEFAULT_DIRECTION,"test");
   }

   @Test
   public void findByCriteria_noFilter() {
      when(mongoTemplate.find(any(Query.class),eq(MaintenanceReminder.class))).thenReturn(Flux.just(new MaintenanceReminder()));
      when(mongoOperations.count(any(Query.class),eq(MaintenanceReminder.class))).thenReturn(Mono.just(1L));
      Page<MaintenanceReminder> maintenanceReminders = repo.findByCriteria(requestNoFilter,1,1,sort).block();
      Assert.assertEquals(maintenanceReminders.get().collect(Collectors.toList()).size(),1 );
      verify(mongoTemplate).find(any(Query.class),eq(MaintenanceReminder.class));
      verify(mongoOperations).count(any(Query.class),eq(MaintenanceReminder.class));
   }

   @Test
   public void testFindByCriteria_noResults(){
      when(mongoTemplate.find(any(Query.class),eq(MaintenanceReminder.class))).thenReturn(Flux.empty());
      when(mongoOperations.count(any(Query.class),eq(MaintenanceReminder.class))).thenReturn(Mono.just(1L));
      Page<MaintenanceReminder> maintenanceReminders = repo.findByCriteria(request,1,1, sort).block();
      Assert.assertEquals(maintenanceReminders.get().collect(Collectors.toList()).size(),0);
      verify(mongoTemplate).find(any(Query.class),eq(MaintenanceReminder.class));
      verify(mongoOperations).count(any(Query.class),eq(MaintenanceReminder.class));
   }
}