package com.gdn.warehouse.assetsmanagement.repository.impl;

import com.gdn.warehouse.assetsmanagement.entity.Maintenance;
import com.gdn.warehouse.assetsmanagement.enums.MaintenanceStatus;
import com.gdn.warehouse.assetsmanagement.properties.StringConstants;
import com.gdn.warehouse.assetsmanagement.repository.request.GetMaintenanceCriteriaRequest;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MaintenanceCustomRepositoryImplTest {
   @InjectMocks
   private MaintenanceCustomRepositoryImpl repo;

   @Mock
   private ReactiveMongoTemplate mongoTemplate;

   @Mock
   private ReactiveMongoOperations mongoOperations;

   private GetMaintenanceCriteriaRequest request,requestNoFilter;
   private Sort sort;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      request= GetMaintenanceCriteriaRequest.builder()
            .maintenanceNumberFilter("MAINTENANCE-NUMBER")
            .assetNumberFilter("ASSET-NUMBER")
            .itemCodeFilter("ITEM-CODE")
            .locationFilter("LOCATION")
            .statusFilter(MaintenanceStatus.ON_MAINTENANCE.name())
            .requesterFilter("USERNAME")
            .tanggalLaporanStartFilter(new Date())
            .tanggalLaporanEndFilter(new Date()).build();
      requestNoFilter= GetMaintenanceCriteriaRequest.builder().build();
      sort = Sort.by(Sort.DEFAULT_DIRECTION,"test");
   }

   @Test
   public void findByCriteria_noFilter() {
      when(mongoTemplate.find(any(Query.class),eq(Maintenance.class))).thenReturn(Flux.just(new Maintenance()));
      when(mongoOperations.count(any(Query.class),eq(Maintenance.class))).thenReturn(Mono.just(1L));
      Page<Maintenance> maintenances = repo.findByCriteria(requestNoFilter,1,1,sort).block();
      Assert.assertEquals(maintenances.get().collect(Collectors.toList()).size(),1 );
      verify(mongoTemplate).find(any(Query.class),eq(Maintenance.class));
      verify(mongoOperations).count(any(Query.class),eq(Maintenance.class));
   }

   @Test
   public void testFindByCriteria_noResults(){
      when(mongoTemplate.find(any(Query.class),eq(Maintenance.class))).thenReturn(Flux.empty());
      when(mongoOperations.count(any(Query.class),eq(Maintenance.class))).thenReturn(Mono.just(1L));
      Page<Maintenance> maintenances = repo.findByCriteria(request,1,1, sort).block();
      Assert.assertEquals(maintenances.get().collect(Collectors.toList()).size(),0);
      verify(mongoTemplate).find(any(Query.class),eq(Maintenance.class));
      verify(mongoOperations).count(any(Query.class),eq(Maintenance.class));
   }

   @Test
   public void testFindByCriteria_noResults_status_ongoing(){
      request.setStatusFilter(StringConstants.ONGOING);
      when(mongoTemplate.find(any(Query.class),eq(Maintenance.class))).thenReturn(Flux.empty());
      when(mongoOperations.count(any(Query.class),eq(Maintenance.class))).thenReturn(Mono.just(1L));
      Page<Maintenance> maintenances = repo.findByCriteria(request,1,1, sort).block();
      Assert.assertEquals(maintenances.get().collect(Collectors.toList()).size(),0);
      verify(mongoTemplate).find(any(Query.class),eq(Maintenance.class));
      verify(mongoOperations).count(any(Query.class),eq(Maintenance.class));
   }

   @Test
   public void testFindByCriteria_noResults_status_history(){
      request.setStatusFilter(StringConstants.HISTORY);
      when(mongoTemplate.find(any(Query.class),eq(Maintenance.class))).thenReturn(Flux.empty());
      when(mongoOperations.count(any(Query.class),eq(Maintenance.class))).thenReturn(Mono.just(1L));
      Page<Maintenance> maintenances = repo.findByCriteria(request,1,1, sort).block();
      Assert.assertEquals(maintenances.get().collect(Collectors.toList()).size(),0);
      verify(mongoTemplate).find(any(Query.class),eq(Maintenance.class));
      verify(mongoOperations).count(any(Query.class),eq(Maintenance.class));
   }
}