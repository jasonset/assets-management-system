package com.gdn.warehouse.assetsmanagement.command.impl;

import com.blibli.oss.backend.common.model.request.SortBy;
import com.blibli.oss.backend.common.model.response.Paging;
import com.gdn.warehouse.assetsmanagement.command.GetAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.model.GetAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.properties.StringConstants;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.custom.AssetCustomRepository;
import com.gdn.warehouse.assetsmanagement.repository.request.GetAssetCriteriaRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetAssetWebResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GetAssetCommandImpl implements GetAssetCommand {

   @Autowired
   private AssetCustomRepository assetCustomRepository;

   @Autowired
   private ItemRepository itemRepository;

   @Override
   public Mono<Pair<List<GetAssetWebResponse>,Paging>> execute(GetAssetCommandRequest request) {
      return assetCustomRepository.findByCriteria(constructCriteriaRequest(request), request.getLimit(), request.getPage(),
                  constructSort(request.getSortBy()))
            .flatMap(assets -> getAllItems(assets.getContent())
                  .map(itemMap -> Pair.of(toGetAssetWebResponse(assets.getContent(),itemMap),getPaginationForAsset(assets))));
   }

   private Sort constructSort(List<SortBy> sortBy){
      return CollectionUtils.isEmpty(sortBy)?
            Sort.by(Sort.Direction.fromString(StringConstants.DEFAULT_SORT_DIRECTION),"assetNumber"):
            Sort.by(sortBy.stream()
                  .map(sort -> new Sort.Order(Sort.Direction.fromString(sort.getDirection().name()), sort.getPropertyName()))
                  .collect(Collectors.toList()));
   }

   private GetAssetCriteriaRequest constructCriteriaRequest(GetAssetCommandRequest request){
      return GetAssetCriteriaRequest.builder()
            .assetNumberFilter(request.getAssetNumberFilter())
            .organisationFilter(request.getOrganisationFilter())
            .vendorFilter(request.getVendorFilter())
            .itemCodeFilter(request.getItemCodeFilter())
            .locationFilter(request.getLocationFilter())
            .statusFilter(request.getStatusFilter())
            .categoryFilter(request.getCategoryFilter()).build();
   }

   private Mono<Map<String,String>> getAllItems(List<Asset> assets){
      List<String> itemCodes = assets.stream().map(Asset::getItemCode).distinct().collect(Collectors.toList());
      return itemRepository.findByItemCodeIn(itemCodes).collectMap(Item::getItemCode, Item::getItemName);
   }

   private List<GetAssetWebResponse> toGetAssetWebResponse(List<Asset> assets, Map<String,String> itemMap){
      return assets.stream().map(asset ->
            GetAssetWebResponse.builder().assetNumber(asset.getAssetNumber())
                  .organisation(asset.getOrganisation().name())
                  .vendor(asset.getVendor())
                  .itemName(itemMap.get(asset.getItemCode()))
                  .location(asset.getLocation())
                  .status(asset.getStatus().name())
                  .build()).collect(Collectors.toList());
   }

   private Paging getPaginationForAsset(Page<Asset> assets){
      return Paging.builder().itemPerPage((long)assets.getSize())
            .page((long)assets.getNumber()+1)
            .totalItem(assets.getTotalElements())
            .totalPage((long)assets.getTotalPages()).build();
   }
}
