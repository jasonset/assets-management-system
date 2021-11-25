package com.gdn.warehouse.assetsmanagement.repository.impl;

import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.repository.request.GetAssetCriteriaRequest;
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

public class AssetCustomRepositoryImplTest {
   @InjectMocks
   private AssetCustomRepositoryImpl repo;

   @Mock
   private ReactiveMongoTemplate mongoTemplate;

   @Mock
   private ReactiveMongoOperations mongoOperations;

   private GetAssetCriteriaRequest request,requestNoFilter;
   private Sort sort;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      request = GetAssetCriteriaRequest.builder().assetNumberFilter("ASSET-NUMBER").organisationFilter("DJARUM").vendorFilter("VENDOR")
            .itemCodeFilter("CODE").locationFilter("LOCATION").statusFilter("NORMAL").categoryFilter("MHE").build();
      requestNoFilter = GetAssetCriteriaRequest.builder().build();
      sort = Sort.by(Sort.DEFAULT_DIRECTION,"test");
   }

   @Test
   public void findByCriteria_noFilter() {
      when(mongoTemplate.find(any(Query.class),eq(Asset.class))).thenReturn(Flux.just(new Asset()));
      when(mongoOperations.count(any(Query.class),eq(Asset.class))).thenReturn(Mono.just(1L));
      Page<Asset> assets = repo.findByCriteria(requestNoFilter,1,1,sort).block();
      Assert.assertEquals(assets.get().collect(Collectors.toList()).size(),1 );
      verify(mongoTemplate).find(any(Query.class),eq(Asset.class));
      verify(mongoOperations).count(any(Query.class),eq(Asset.class));
   }

   @Test
   public void testFindByCriteria_noResults(){
      when(mongoTemplate.find(any(Query.class),eq(Asset.class))).thenReturn(Flux.empty());
      when(mongoOperations.count(any(Query.class),eq(Asset.class))).thenReturn(Mono.just(1L));
      Page<Asset> assets =
            repo.findByCriteria(request,1,1, sort).block();
      Assert.assertEquals(assets.get().collect(Collectors.toList()).size(),0);
      verify(mongoTemplate).find(any(Query.class),eq(Asset.class));
      verify(mongoOperations).count(any(Query.class),eq(Asset.class));
   }
}