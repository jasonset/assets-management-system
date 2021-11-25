package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.CreateAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.model.CreateAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import com.gdn.warehouse.assetsmanagement.helper.GenerateSequenceHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.GenerateAssetNumberRequest;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.WarehouseRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

@Slf4j
@Service
@AllArgsConstructor
public class CreateAssetCommandImpl implements CreateAssetCommand {
   @Autowired
   private AssetRepository assetRepository;

   @Autowired
   private WarehouseRepository warehouseRepository;

   @Autowired
   private GenerateSequenceHelper generateSequenceHelper;

   @Override
   public Mono<String> execute(CreateAssetCommandRequest request) {
      return getAssetNumber(request)
            .flatMap(assetNumber -> createAsset(request,assetNumber))
            .map(Asset::getAssetNumber);
   }

   private Mono<String> getAssetNumber(CreateAssetCommandRequest request){
      if(StringUtils.isEmpty(request.getAssetNumber())){
         GenerateAssetNumberRequest generateAssetNumberRequest = GenerateAssetNumberRequest.builder()
               .purchase(request.getPurchase().name())
               .organisation(request.getOrganisation().name())
               .category(request.getCategory().name()).build();
         return generateSequenceHelper.generateDocumentNumberForAsset(DocumentType.ASSET,generateAssetNumberRequest);
      }else {
         return Mono.just(request.getAssetNumber());
      }
   }

   private Mono<Asset> createAsset(CreateAssetCommandRequest request,String assetNumber){
      return warehouseRepository.findByWarehouseName(request.getLocation())
                  .switchIfEmpty(Mono.defer(()->Mono.error(new CommandErrorException("Warehouse Not Exist or Input Correct Location!",HttpStatus.BAD_REQUEST))))
            .flatMap(warehouse -> {
               Asset asset = new Asset();
               BeanUtils.copyProperties(request,asset);
               asset.setPoIssuedDate(ObjectUtils.isEmpty(request.getPoIssuedDate())?null:new Date(request.getPoIssuedDate()));
               asset.setDeliveryDate(ObjectUtils.isEmpty(request.getDeliveryDate())?null:new Date(request.getDeliveryDate()));
               asset.setAssetNumber(assetNumber);
               asset.setCreatedBy(request.getUsername());
               asset.setCreatedDate(new Date());
               asset.setLastModifiedBy(request.getUsername());
               asset.setLastModifiedDate(new Date());
               asset.setHasReminder(Boolean.FALSE);
               asset.setWarehouseCode(warehouse.getWarehouseCode());
               asset.setItemCode(request.getItemCode());
               asset.setDipinjam(Boolean.FALSE);
               return assetRepository.save(asset);
            });
   }
}
