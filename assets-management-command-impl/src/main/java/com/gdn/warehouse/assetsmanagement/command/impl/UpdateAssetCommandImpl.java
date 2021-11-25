package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.UpdateAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.model.UpdateAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.Warehouse;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.WarehouseRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Date;

@Slf4j
@Service
@AllArgsConstructor
public class UpdateAssetCommandImpl implements UpdateAssetCommand {
   @Autowired
   private AssetRepository assetRepository;

   @Autowired
   private WarehouseRepository warehouseRepository;

   @Autowired
   private ItemRepository itemRepository;

   @Override
   public Mono<Boolean> execute(UpdateAssetCommandRequest request) {
      return getAsset(request.getAssetNumber())
            .flatMap(asset -> validateAsset(asset,request))
            .flatMap(asset -> updateAsset(asset,request))
            .map(result -> Boolean.TRUE);
   }

   private Mono<Asset> getAsset(String assetNumber){
      return assetRepository.findByAssetNumber(assetNumber)
            .switchIfEmpty(Mono.defer(()->Mono.error(new CommandErrorException("Asset doesn't exist!",HttpStatus.BAD_REQUEST))));
   }

   private Mono<Asset> validateAsset(Asset asset,UpdateAssetCommandRequest request){
      if(!AssetStatus.NORMAL.equals(asset.getStatus())&&
         !AssetStatus.RUSAK_PARAH_BELUM_BAC.equals(asset.getStatus())&&
         !AssetStatus.RUSAK_PARAH_SUDAH_BAC.equals(asset.getStatus())){
         if(!request.getStatus().equals(asset.getStatus())){
            return Mono.defer(()-> Mono.error(new CommandErrorException("Cannot update asset status because status in "+asset.getStatus().name(),
                  HttpStatus.BAD_REQUEST)));
         }else {
            return Mono.defer(()-> Mono.just(asset));
         }
      } else {
         return Mono.defer(()-> Mono.just(asset));
      }
   }

   private Mono<Asset> updateAsset(Asset asset, UpdateAssetCommandRequest request){
      return Mono.zip(itemRepository.findByItemCode(request.getItemCode())
                        .switchIfEmpty(Mono.defer(()->Mono.error(new CommandErrorException("Item  doesn't exist!", HttpStatus.BAD_REQUEST)))),
                  warehouseRepository.findByWarehouseName(request.getLocation()))
                        .switchIfEmpty(Mono.defer(()->Mono.error(new CommandErrorException("Warehouse doesn't exist!", HttpStatus.BAD_REQUEST))))
            .flatMap(tuple -> assetRepository.save(updateAssetDetail(asset,request,tuple)));
   }

   private Asset updateAssetDetail(Asset asset, UpdateAssetCommandRequest request,
                                         Tuple2<Item, Warehouse> tuple){
      asset.setOrganisation(request.getOrganisation());
      asset.setItemCode(tuple.getT1().getItemCode());
      asset.setVendor(request.getVendor());
      asset.setLocation(request.getLocation());
      asset.setWarehouseCode(tuple.getT2().getWarehouseCode());
      asset.setPoNumber(request.getPoNumber());
      asset.setPoIssuedDate(ObjectUtils.isEmpty(request.getPoIssuedDate())?null:new Date(request.getPoIssuedDate()));
      asset.setPrice(ObjectUtils.isEmpty(request.getPrice()) ? 0 : request.getPrice());
      asset.setStatus(request.getStatus());
      asset.setDeliveryDate(ObjectUtils.isEmpty(request.getDeliveryDate())?null:new Date(request.getDeliveryDate()));
      asset.setNotes(request.getNotes());
      asset.setVehiclePlate(StringUtils.isEmpty(request.getVehiclePlate()) ? null : request.getVehiclePlate());
      asset.setNomorRangka(StringUtils.isEmpty(request.getNomorRangka()) ? null : request.getNomorRangka());
      asset.setNomorMesin(StringUtils.isEmpty(request.getNomorMesin()) ?  null : request.getNomorMesin());
      asset.setPurchase(request.getPurchase());
      asset.setLastModifiedDate(new Date());
      asset.setLastModifiedBy(request.getUsername());
      return asset;
   }
}
