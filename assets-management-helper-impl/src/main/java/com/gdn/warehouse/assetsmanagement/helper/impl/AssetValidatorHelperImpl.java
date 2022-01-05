package com.gdn.warehouse.assetsmanagement.helper.impl;

import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.helper.AssetValidatorHelper;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AssetValidatorHelperImpl implements AssetValidatorHelper {

   @Autowired
   private AssetRepository assetRepository;

   @Override
   public Mono<List<Asset>> validateAssetForMaintenanceReminder(List<String> assetNumbers) {
      return assetRepository.findByAssetNumberIn(assetNumbers)
            .flatMap(this::validateReminder).collectList()
            .flatMap(assets -> validateAssetNameAndLocation(assets,assetNumbers));
   }

   private Mono<Asset> validateReminder(Asset asset){
      if(asset.getHasReminder()){
         return Mono.defer(()->Mono.error(new CommandErrorException("There is an active Maintenance Reminder for" +
               " asset number: " + asset.getAssetNumber() + " !", HttpStatus.BAD_REQUEST)));
      }else {
         return Mono.defer(()->Mono.just(asset));
      }
   }

   @Override
   public Mono<List<Asset>> validateAssetFromRequest(List<String> assetNumbers) {
      return assetRepository.findByAssetNumberIn(assetNumbers)
            .flatMap(this::validateAsset).collectList()
            .flatMap(assets -> validateAssetNameAndLocation(assets,assetNumbers));
   }

   private Mono<Asset> validateAsset(Asset asset){
      if(!AssetStatus.NORMAL.equals(asset.getStatus())){
         return Mono.defer(()-> Mono.error(new CommandErrorException("Asset "+asset.getAssetNumber()+" status is "+asset.getStatus(),HttpStatus.BAD_REQUEST)));
      }
      return Mono.defer(()->Mono.just(asset));
   }

   private Mono<List<Asset>> validateAssetNameAndLocation(List<Asset> assetList, List<String> assetNumberRequestList){
      List<String> assetNumberList = assetList.stream().map(Asset::getAssetNumber).collect(Collectors.toList());
      Collection<String> validateAssetExist = CollectionUtils.subtract(assetNumberRequestList,assetNumberList);
      if(CollectionUtils.isNotEmpty(validateAssetExist)){
         return Mono.defer(()->Mono.error(new CommandErrorException("Asset: "+validateAssetExist+" does not exist!",HttpStatus.BAD_REQUEST)));
      }

      boolean allItemAndLocationSame = assetList.stream().allMatch(asset -> assetList.get(0).getItemCode().equals(asset.getItemCode())&&
            assetList.get(0).getLocation().equals(asset.getLocation()));
      if(allItemAndLocationSame){
         return Mono.defer(()->Mono.just(assetList));
      }else {
         return Mono.defer(()->Mono.error(new CommandErrorException("All Asset need to have same item and from the same warehouse!",HttpStatus.BAD_REQUEST)));
      }
   }
}
