package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.DeliveredTransferAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.model.DeliveredTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetType;
import com.gdn.warehouse.assetsmanagement.helper.GenerateSequenceHelper;
import com.gdn.warehouse.assetsmanagement.helper.TransferAssetHistoryHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.TransferAssetHistoryHelperRequest;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.TransferAssetRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

@Slf4j
@Service
@AllArgsConstructor
public class DeliveredTransferAssetCommandImpl implements DeliveredTransferAssetCommand {

   @Autowired
   private TransferAssetRepository transferAssetRepository;

   @Autowired
   private AssetRepository assetRepository;

   @Autowired
   private TransferAssetHistoryHelper transferAssetHistoryHelper;

   @Autowired
   private GenerateSequenceHelper generateSequenceHelper;

   @Override
   public Mono<Boolean> execute(DeliveredTransferAssetCommandRequest request) {
      return transferAssetRepository.findByTransferAssetNumber(request.getTransferAssetNumber())
            .switchIfEmpty(Mono.defer(()->Mono.error(new CommandErrorException("Transfer Asset doesn't exist!", HttpStatus.BAD_REQUEST))))
            .flatMap(transferAsset -> updateStatus(transferAsset,request))
            .flatMap(transferAsset -> saveToHistory(transferAsset,request))
            .flatMap(this::createReturnTransferAsset)
            .map(result -> Boolean.TRUE);
   }

   private Mono<TransferAsset> updateStatus(TransferAsset transferAsset, DeliveredTransferAssetCommandRequest request){
      return assetRepository.findByAssetNumberIn(transferAsset.getAssetNumbers())
            .map(asset -> {
               asset.setStatus(AssetStatus.NORMAL);
               asset.setLocation(transferAsset.getDestination());
               if(TransferAssetType.RETURN.equals(transferAsset.getTransferAssetType())){
                  asset.setDipinjam(Boolean.FALSE);
               }
               return asset;
            }).collectList()
            .flatMap(assets -> assetRepository.saveAll(assets).collectList())
            .flatMap(assets -> {
               transferAsset.setArrivalDate(new Date(request.getArrivalDate()));
               transferAsset.setStatus(TransferAssetStatus.DELIVERED);
               transferAsset.setLastModifiedBy(request.getUsername());
               transferAsset.setLastModifiedDate(new Date());
               return transferAssetRepository.save(transferAsset);
            });
   }

   private Mono<TransferAsset> saveToHistory(TransferAsset transferAsset, DeliveredTransferAssetCommandRequest request){
      return transferAssetHistoryHelper.createTransferAssetHistory(toTransferAssetHistoryHelperRequest(transferAsset,request))
            .map(result-> transferAsset);
   }

   private TransferAssetHistoryHelperRequest toTransferAssetHistoryHelperRequest(TransferAsset transferAsset, DeliveredTransferAssetCommandRequest request){
      return TransferAssetHistoryHelperRequest.builder()
            .transferAssetNumber(transferAsset.getTransferAssetNumber())
            .transferAssetType(transferAsset.getTransferAssetType())
            .transferAssetStatus(transferAsset.getStatus())
            .updatedBy(request.getUsername())
            .updatedDate(new Date())
            .build();
   }

   private Mono<TransferAsset> createReturnTransferAsset(TransferAsset transferAsset){
      if(TransferAssetType.BORROW.equals(transferAsset.getTransferAssetType())){
         return generateSequenceHelper.generateDocumentNumber(DocumentType.TRANSFER_ASSET)
               .switchIfEmpty(Mono.defer(()-> Mono.error(new CommandErrorException("Failed to Generate Document Number for Transfer Asset",HttpStatus.INTERNAL_SERVER_ERROR))))
               .flatMap(documentNumber -> transferAssetRepository.save(TransferAsset.builder()
                     .transferAssetNumber(documentNumber)
                     .assetNumbers(transferAsset.getAssetNumbers())
                     .itemCode(transferAsset.getItemCode())
                     .origin(transferAsset.getDestination())
                     .destination(transferAsset.getOrigin())
                     .status(TransferAssetStatus.APPROVED)
                     .notes(transferAsset.getNotes())
                     .arrivalDate(null)
                     .deliveryDate(null)
                     .transferAssetType(TransferAssetType.RETURN)
                     .originWarehouseManagerEmail(transferAsset.getDestinationWarehouseManagerEmail())
                     .destinationWarehouseManagerEmail(transferAsset.getOriginWarehouseManagerEmail())
                     .createdBy("SYSTEM")
                     .createdDate(new Date())
                     .lastModifiedBy("SYSTEM")
                     .lastModifiedDate(new Date())
                     .referenceNumber(transferAsset.getTransferAssetNumber()).build()));
      }else {
         return mono(()->transferAsset);
      }
   }
}
