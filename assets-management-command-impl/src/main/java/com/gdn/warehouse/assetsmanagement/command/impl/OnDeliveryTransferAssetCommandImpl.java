package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.OnDeliveryTransferAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.model.OnDeliveryTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetStatus;
import com.gdn.warehouse.assetsmanagement.helper.TransferAssetHistoryHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.TransferAssetHistoryHelperRequest;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.TransferAssetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

@Slf4j
@Service
public class OnDeliveryTransferAssetCommandImpl implements OnDeliveryTransferAssetCommand {
   @Autowired
   private TransferAssetRepository transferAssetRepository;

   @Autowired
   private TransferAssetHistoryHelper transferAssetHistoryHelper;

   @Autowired
   private AssetRepository assetRepository;

   @Override
   public Mono<Boolean> execute(OnDeliveryTransferAssetCommandRequest request) {
      return transferAssetRepository.findByTransferAssetNumber(request.getTransferAssetNumber())
            .switchIfEmpty(Mono.defer(()->Mono.error(new CommandErrorException("Transfer Asset doesn't exist!", HttpStatus.BAD_REQUEST))))
            .flatMap(transferAsset -> {
               transferAsset.setStatus(TransferAssetStatus.ON_DELIVERY);
               transferAsset.setDeliveryDate(new Date(request.getDeliveryDate()));
               return transferAssetRepository.save(transferAsset);
            })
            .doOnSuccess(this::updateAssetStatus)
            .doOnSuccess(transferAsset -> saveToHistory(transferAsset,request))
            .map(result -> Boolean.TRUE);
   }

   private void updateAssetStatus(TransferAsset transferAsset){
      assetRepository.findByAssetNumberIn(transferAsset.getAssetNumbers())
            .map(asset -> {
               asset.setStatus(AssetStatus.ON_TRANSFER_DELIVERY);
               return asset;
            }).collectList()
            .flatMap(assets -> assetRepository.saveAll(assets).collectList()).subscribe();
   }

   private void saveToHistory(TransferAsset transferAsset, OnDeliveryTransferAssetCommandRequest request){
      transferAssetHistoryHelper.createTransferAssetHistory(toTransferAssetHistoryHelperRequest(transferAsset,request)).subscribe();
   }

   private TransferAssetHistoryHelperRequest toTransferAssetHistoryHelperRequest(TransferAsset transferAsset, OnDeliveryTransferAssetCommandRequest request){
      return TransferAssetHistoryHelperRequest.builder()
            .transferAssetNumber(transferAsset.getTransferAssetNumber())
            .transferAssetType(transferAsset.getTransferAssetType())
            .transferAssetStatus(transferAsset.getStatus())
            .updatedBy(request.getUsername())
            .updatedDate(new Date())
            .build();
   }
}
