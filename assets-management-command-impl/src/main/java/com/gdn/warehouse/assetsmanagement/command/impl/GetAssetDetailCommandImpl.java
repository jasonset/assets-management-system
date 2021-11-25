package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.GetAssetDetailCommand;
import com.gdn.warehouse.assetsmanagement.command.model.GetAssetDetailCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetAssetDetailWebResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class GetAssetDetailCommandImpl implements GetAssetDetailCommand {

   @Autowired
   private AssetRepository assetRepository;

   @Autowired
   private ItemRepository itemRepository;

   @Override
   public Mono<GetAssetDetailWebResponse> execute(GetAssetDetailCommandRequest request) {
      return assetRepository.findByAssetNumber(request.getAssetNumber())
            .switchIfEmpty(Mono.defer(()->Mono.error(new CommandErrorException("Asset doesn't exist!", HttpStatus.BAD_REQUEST))))
            .flatMap(asset -> itemRepository.findByItemCode(asset.getItemCode())
                  .flatMap(item -> {
                     GetAssetDetailWebResponse response = GetAssetDetailWebResponse.builder().build();
                     BeanUtils.copyProperties(asset,response);
                     response.setStatus(asset.getStatus().name());
                     response.setOrganisation(asset.getOrganisation().name());
                     response.setPurchase(asset.getPurchase().name());
                     response.setCategory(asset.getCategory().name());
                     response.setDipinjam(asset.getDipinjam()?"YA":"TIDAK");
                     response.setItemName(item.getItemName());
                     response.setItemCode(item.getItemCode());
                     return Mono.just(response);
                  }));
   }
}
