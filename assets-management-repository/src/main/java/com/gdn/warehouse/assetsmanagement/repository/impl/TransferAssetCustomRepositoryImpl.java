package com.gdn.warehouse.assetsmanagement.repository.impl;

import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetStatus;
import com.gdn.warehouse.assetsmanagement.repository.custom.TransferAssetCustomRepository;
import com.gdn.warehouse.assetsmanagement.repository.request.GetTransferAssetCriteriaRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Component
public class TransferAssetCustomRepositoryImpl extends GenericCustomRepository implements TransferAssetCustomRepository {

   @Override
   public Mono<Page<TransferAsset>> findByCriteria(GetTransferAssetCriteriaRequest request, Integer limit, Integer page, Sort sort) {
      return findByCriteria(TransferAsset.class,constructCriteria(request),limit,page,sort);
   }

   private Criteria constructCriteria(GetTransferAssetCriteriaRequest request){
      Criteria criteria = new Criteria();

      if(StringUtils.isNotEmpty(request.getTransferAssetNumberFilter())){
         criteria = criteria.and("transferAssetNumber").regex(request.getTransferAssetNumberFilter(),"i");
      }
      if(StringUtils.isNotEmpty(request.getAssetNumberFilter())){
         criteria = criteria.and("assetNumbers").regex(request.getAssetNumberFilter(),"i");
      }
      if(StringUtils.isNotEmpty(request.getOriginFilter())){
         criteria = criteria.and("origin").regex(request.getOriginFilter(),"i");
      }
      if(StringUtils.isNotEmpty(request.getDestinationFilter())){
         criteria = criteria.and("destination").regex(request.getDestinationFilter(),"i");
      }
      if(StringUtils.isNotEmpty(request.getItemCodeFilter())){
         criteria = criteria.and("itemCode").regex(request.getItemCodeFilter(),"i");
      }
      if ("LIST".equals(request.getStatusFilter())){
         criteria = criteria.and("status").in(Arrays.asList(TransferAssetStatus.APPROVED.name(),TransferAssetStatus.ON_DELIVERY.name()));
      } else if(StringUtils.isNotEmpty(request.getStatusFilter())){
      criteria = criteria.and("status").regex(request.getStatusFilter());
      } else {
         criteria = criteria.and("status").in(Arrays.asList(TransferAssetStatus.DELIVERED.name(),TransferAssetStatus.DECLINED.name()));
      }
      if(StringUtils.isNotEmpty(request.getTransferAssetTypeFilter())){
         criteria = criteria.and("transferAssetType").regex(request.getTransferAssetTypeFilter(),"i");
      }
      if(StringUtils.isNotEmpty(request.getReferenceNumberFilter())){
         criteria = criteria.and("referenceNumber").regex(request.getReferenceNumberFilter(),"i");
      }
      return criteria;
   }
}
