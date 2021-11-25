package com.gdn.warehouse.assetsmanagement.command.impl;

import com.blibli.oss.backend.common.model.request.SortBy;
import com.blibli.oss.backend.common.model.response.Paging;
import com.gdn.warehouse.assetsmanagement.command.GetTransferAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.model.GetTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.properties.StringConstants;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.custom.TransferAssetCustomRepository;
import com.gdn.warehouse.assetsmanagement.repository.request.GetTransferAssetCriteriaRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetTransferAssetWebResponse;
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
public class GetTransferAssetCommandImpl implements GetTransferAssetCommand {

   @Autowired
   private TransferAssetCustomRepository transferAssetCustomRepository;

   @Autowired
   private ItemRepository itemRepository;

   @Override
   public Mono<Pair<List<GetTransferAssetWebResponse>,Paging>> execute(GetTransferAssetCommandRequest request) {
      return transferAssetCustomRepository.findByCriteria(constructCriteriaRequest(request), request.getLimit(), request.getPage(),
            constructSort(request.getSortBy()))
            .flatMap(transferAssets -> getAllItems(transferAssets.getContent())
                  .map(itemMap -> Pair.of(toGetTransferAssetWebResponse(transferAssets.getContent(),itemMap),getPaginationForAsset(transferAssets))));
   }

   private Sort constructSort(List<SortBy> sortBy){
      return CollectionUtils.isEmpty(sortBy)?
            Sort.by(Sort.Direction.fromString(StringConstants.DEFAULT_SORT_DIRECTION),"transferAssetNumber"):
            Sort.by(sortBy.stream()
                  .map(sort -> new Sort.Order(Sort.Direction.fromString(sort.getDirection().name()), sort.getPropertyName()))
                  .collect(Collectors.toList()));
   }

   private GetTransferAssetCriteriaRequest constructCriteriaRequest(GetTransferAssetCommandRequest request){
      return GetTransferAssetCriteriaRequest.builder()
            .transferAssetNumberFilter(request.getTransferAssetNumberFilter())
            .assetNumberFilter(request.getAssetNumberFilter())
            .originFilter(request.getOriginFilter())
            .destinationFilter(request.getDestinationFilter())
            .itemCodeFilter(request.getItemCodeFilter())
            .statusFilter(request.getStatusFilter())
            .transferAssetTypeFilter(request.getTransferAssetTypeFilter())
            .referenceNumberFilter(request.getReferenceNumberFilter()).build();
   }

   private Mono<Map<String,String>> getAllItems(List<TransferAsset> transferAssets){
      List<String> itemCodes = transferAssets.stream().map(TransferAsset::getItemCode).distinct().collect(Collectors.toList());
      return itemRepository.findByItemCodeIn(itemCodes).collectMap(Item::getItemCode, Item::getItemName);
   }

   private List<GetTransferAssetWebResponse> toGetTransferAssetWebResponse(List<TransferAsset> transferAssets, Map<String,String> itemMap){
      return transferAssets.stream().map(transferAsset ->
            GetTransferAssetWebResponse.builder().transferAssetNumber(transferAsset.getTransferAssetNumber())
                  .assetNumbers(transferAsset.getAssetNumbers())
                  .origin(transferAsset.getOrigin())
                  .destination(transferAsset.getDestination())
                  .itemName(itemMap.get(transferAsset.getItemCode()))
                  .status(transferAsset.getStatus().name())
                  .transferAssetType(transferAsset.getTransferAssetType().name()).build()).collect(Collectors.toList());
   }

   private Paging getPaginationForAsset(Page<TransferAsset> transferAssets){
      return Paging.builder().itemPerPage((long)transferAssets.getSize())
            .page((long)transferAssets.getNumber()+1)
            .totalItem(transferAssets.getTotalElements())
            .totalPage((long)transferAssets.getTotalPages()).build();
   }
}
