package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.GetTransferAssetDetailCommand;
import com.gdn.warehouse.assetsmanagement.command.model.GetTransferAssetDetailCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.TransferAssetRepository;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetTransferAssetDetailWebResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class GetTransferAssetDetailCommandImpl implements GetTransferAssetDetailCommand {

   @Autowired
   private TransferAssetRepository transferAssetRepository;

   @Autowired
   private ItemRepository itemRepository;

   @Override
   public Mono<GetTransferAssetDetailWebResponse> execute(GetTransferAssetDetailCommandRequest request) {
      return transferAssetRepository.findByTransferAssetNumber(request.getTransferAssetNumber())
            .switchIfEmpty(Mono.defer(()->Mono.error(new CommandErrorException("Transfer Asset doesn't exist!", HttpStatus.BAD_REQUEST))))
            .flatMap(this::constructWebResponse);
   }

   private Mono<GetTransferAssetDetailWebResponse> constructWebResponse(TransferAsset transferAsset){
      return itemRepository.findByItemCode(transferAsset.getItemCode())
            .map(item -> GetTransferAssetDetailWebResponse.builder()
                  .transferAssetNumber(transferAsset.getTransferAssetNumber())
                  .assetNumbers(transferAsset.getAssetNumbers())
                  .arrivalDate(transferAsset.getArrivalDate())
                  .itemName(item.getItemName())
                  .origin(transferAsset.getOrigin())
                  .destination(transferAsset.getDestination())
                  .status(transferAsset.getStatus().name())
                  .notes(transferAsset.getNotes())
                  .referenceNumber(transferAsset.getReferenceNumber())
                  .deliveryDate(transferAsset.getDeliveryDate())
                  .transferAssetType(transferAsset.getTransferAssetType().name()).build());
   }
}
