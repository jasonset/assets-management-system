package com.gdn.warehouse.assetsmanagement.repository.impl;

import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetType;
import com.gdn.warehouse.assetsmanagement.repository.request.GetTransferAssetCriteriaRequest;
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

import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransferAssetCustomRepositoryImplTest {
   
   @InjectMocks
   private TransferAssetCustomRepositoryImpl repo;

   @Mock
   private ReactiveMongoTemplate mongoTemplate;

   @Mock
   private ReactiveMongoOperations mongoOperations;
   
   private GetTransferAssetCriteriaRequest request,requestNoFilter;
   private Sort sort;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      request = GetTransferAssetCriteriaRequest.builder()
            .transferAssetNumberFilter("TRANSFER-ASSET")
            .assetNumberFilter("ASSET-NUMBER")
            .itemCodeFilter("ITEM-CODE")
            .originFilter("ORIGIN")
            .destinationFilter("DESTINATION")
            .statusFilter(TransferAssetStatus.ON_DELIVERY.name())
            .transferAssetTypeFilter(TransferAssetType.NORMAL.name())
            .referenceNumberFilter("REFERENCE").build();
      requestNoFilter = GetTransferAssetCriteriaRequest.builder().build();
      sort = Sort.by(Sort.DEFAULT_DIRECTION,"test");
   }

   @Test
   public void findByCriteria_noFilter() {
      when(mongoTemplate.find(any(Query.class),eq(TransferAsset.class))).thenReturn(Flux.just(new TransferAsset()));
      when(mongoOperations.count(any(Query.class),eq(TransferAsset.class))).thenReturn(Mono.just(1L));
      Page<TransferAsset> TransferAssets = repo.findByCriteria(requestNoFilter,1,1,sort).block();
      Assert.assertEquals(1,TransferAssets.get().collect(Collectors.toList()).size());
      verify(mongoTemplate).find(any(Query.class),eq(TransferAsset.class));
      verify(mongoOperations).count(any(Query.class),eq(TransferAsset.class));
   }

   @Test
   public void testFindByCriteria_noResults(){
      when(mongoTemplate.find(any(Query.class),eq(TransferAsset.class))).thenReturn(Flux.empty());
      when(mongoOperations.count(any(Query.class),eq(TransferAsset.class))).thenReturn(Mono.just(1L));
      Page<TransferAsset> TransferAssets =
            repo.findByCriteria(request,1,1, sort).block();
      Assert.assertEquals(0,TransferAssets.get().collect(Collectors.toList()).size());
      verify(mongoTemplate).find(any(Query.class),eq(TransferAsset.class));
      verify(mongoOperations).count(any(Query.class),eq(TransferAsset.class));
   }

   @Test
   public void testFindByCriteria_noResults_status_LIST(){
      request.setStatusFilter("LIST");
      when(mongoTemplate.find(any(Query.class),eq(TransferAsset.class))).thenReturn(Flux.empty());
      when(mongoOperations.count(any(Query.class),eq(TransferAsset.class))).thenReturn(Mono.just(1L));
      Page<TransferAsset> TransferAssets =
            repo.findByCriteria(request,1,1, sort).block();
      Assert.assertEquals(0,TransferAssets.get().collect(Collectors.toList()).size());
      verify(mongoTemplate).find(any(Query.class),eq(TransferAsset.class));
      verify(mongoOperations).count(any(Query.class),eq(TransferAsset.class));
   }
}