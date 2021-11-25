package com.gdn.warehouse.assetsmanagement.repository.impl;

import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.repository.custom.AssetCustomRepository;
import com.gdn.warehouse.assetsmanagement.repository.request.GetAssetCriteriaRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AssetCustomRepositoryImpl extends GenericCustomRepository implements AssetCustomRepository {

   @Override
   public Mono<Page<Asset>> findByCriteria(GetAssetCriteriaRequest request, Integer limit, Integer page, Sort sort) {
      return findByCriteria(Asset.class,constructCriteria(request),limit,page,sort);
   }

   private Criteria constructCriteria(GetAssetCriteriaRequest request){
      Criteria criteria = new Criteria();

      if(StringUtils.isNotEmpty(request.getAssetNumberFilter())){
         criteria = criteria.and("assetNumber").regex(request.getAssetNumberFilter(),"i");
      }
      if(StringUtils.isNotEmpty(request.getOrganisationFilter())){
         criteria = criteria.and("organisation").regex(request.getOrganisationFilter(),"i");
      }
      if(StringUtils.isNotEmpty(request.getVendorFilter())){
         criteria = criteria.and("vendor").regex(request.getVendorFilter(),"i");
      }
      if(StringUtils.isNotEmpty(request.getItemCodeFilter())){
         criteria = criteria.and("itemCode").regex(request.getItemCodeFilter(),"i");
      }
      if(StringUtils.isNotEmpty(request.getLocationFilter())){
         criteria = criteria.and("location").regex(request.getLocationFilter(),"i");
      }
      if(StringUtils.isNotEmpty(request.getStatusFilter())){
         criteria = criteria.and("status").regex(request.getStatusFilter());
      }
      if(StringUtils.isNotEmpty(request.getCategoryFilter())){
         criteria = criteria.and("category").regex(request.getCategoryFilter(),"i");
      }
      return criteria;
   }
}
