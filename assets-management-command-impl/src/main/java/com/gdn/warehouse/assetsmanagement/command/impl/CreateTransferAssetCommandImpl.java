package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.CreateTransferAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.model.CreateTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.SystemParam;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import com.gdn.warehouse.assetsmanagement.enums.Identity;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetStatus;
import com.gdn.warehouse.assetsmanagement.helper.AssetValidatorHelper;
import com.gdn.warehouse.assetsmanagement.helper.GenerateSequenceHelper;
import com.gdn.warehouse.assetsmanagement.helper.SendEmailHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.SendEmailHelperRequest;
import com.gdn.warehouse.assetsmanagement.properties.StringConstants;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.SystemParamRepository;
import com.gdn.warehouse.assetsmanagement.repository.TransferAssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.WarehouseRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CreateTransferAssetCommandImpl implements CreateTransferAssetCommand {

   @Autowired
   private TransferAssetRepository transferAssetRepository;

   @Autowired
   private GenerateSequenceHelper generateSequenceHelper;

   @Autowired
   private SendEmailHelper sendEmailHelper;

   @Autowired
   private WarehouseRepository warehouseRepository;

   @Autowired
   private AssetValidatorHelper assetValidatorHelper;

   @Autowired
   private AssetRepository assetRepository;

   @Autowired
   private SystemParamRepository systemParamRepository;

   @Autowired
   private ItemRepository itemRepository;

   @Override
   public Mono<String> execute(CreateTransferAssetCommandRequest request) {
      List<String> assetNumbers = request.getAssetNumbers().stream().map(String::trim).distinct().collect(Collectors.toList());
      return assetValidatorHelper.validateAssetFromRequest(assetNumbers)
            .flatMap(assets ->  Mono.zip(createTransferAsset(request,assets.get(0).getLocation(),assets,assetNumbers),
                  systemParamRepository.findByKey(StringConstants.BASE_PATH_UI),
                  itemRepository.findByItemCode(assets.get(0).getItemCode())))
            .doOnSuccess(tuple3 -> {
               sendEmailHelper.sendEmail(toSendEmailHelperRequestUser(tuple3));
               sendEmailHelper.sendEmail(toSendEmailHelperRequestWarehouseManagerOrigin(tuple3));
            })
            .map(tuple3 -> tuple3.getT1().getTransferAssetNumber())
            .onErrorMap(error -> new CommandErrorException(error.getMessage(), HttpStatus.BAD_REQUEST));
      //TODO kirim email ke warehouse manager dan user
   }

   private Mono<TransferAsset> createTransferAsset(CreateTransferAssetCommandRequest request, String origin,
                                                   List<Asset> assetList, List<String> assetNumbers){
      //tuple1 = transferAssetNumber, tuple2 = warehouseOrigin, tuple3 = warehouseDestination
      if (origin.equals(request.getDestination())){
         return Mono.defer(()->Mono.error(new CommandErrorException("Transfer Asset can't be created for the same origin and destination!",HttpStatus.BAD_REQUEST)));
      }
      return Mono.zip(generateSequenceHelper.generateDocumentNumber(DocumentType.TRANSFER_ASSET)
                        .switchIfEmpty(Mono.defer(()-> Mono.error(new CommandErrorException("Failed to Generate Document Number for Transfer Asset",HttpStatus.INTERNAL_SERVER_ERROR)))),
            warehouseRepository.findByWarehouseName(origin)
                  .switchIfEmpty(Mono.defer(()-> Mono.error(new CommandErrorException("Warehouse "+origin+" does not exist!",HttpStatus.BAD_REQUEST)))),
            warehouseRepository.findByWarehouseName(request.getDestination())
                  .switchIfEmpty(Mono.defer(()-> Mono.error(new CommandErrorException("Warehouse "+request.getDestination()+" does not exist! or Please Input Correct Location!",HttpStatus.BAD_REQUEST)))))
            .flatMap(tuple -> transferAssetRepository.save(TransferAsset.builder()
                  .transferAssetNumber(tuple.getT1())
                  .assetNumbers(assetNumbers)
                  .itemCode(assetList.get(0).getItemCode())
                  .origin(origin)
                  .destination(request.getDestination())
                  .status(TransferAssetStatus.PENDING)
                  .notes(request.getNotes())
                  .arrivalDate(null)
                  .deliveryDate(null)
                  .transferAssetType(request.getTransferAssetType())
                  .originWarehouseManagerEmail(tuple.getT2().getEmail())
                  .destinationWarehouseManagerEmail(tuple.getT3().getEmail())
                  .createdBy(request.getUsername())
                  .createdDate(new Date())
                  .lastModifiedBy(request.getUsername())
                  .lastModifiedDate(new Date()).build())
                  .doOnSuccess(transferAsset -> updateAssets(transferAsset.getAssetNumbers())));
   }

   private void updateAssets(List<String> assetNumbers){
      assetRepository.findByAssetNumberIn(assetNumbers)
            .map(asset -> {
               asset.setStatus(AssetStatus.PENDING_TRANSFER);
               return asset;
            }).collectList()
            .flatMap(assets -> assetRepository.saveAll(assets).collectList()).subscribe();
   }

   private SendEmailHelperRequest toSendEmailHelperRequestUser(Tuple3<TransferAsset, SystemParam, Item> tuple3){
      return SendEmailHelperRequest.builder()
            .mailTemplateId("EMAIL_ASSETS_MANAGEMENT_TRANSFER_ASSET_USER")
            .mailSubject("Approval Notification for Transfer Asset")
            .fromEmail(StringConstants.SENDER_EMAIL_ASSETS_MANAGEMENT)
//            .toEmail(StringConstants.USER_EMAIL)
            .toEmail("jason.setiadi@gdn-commerce.com;")
            .identifierKey(StringConstants.TRANSFER_ASSET_NUMBER)
            .identifierValue(tuple3.getT1().getTransferAssetNumber())
            .emailVariables(constructVariableForTemplate(tuple3,Identity.USER))
            .build();
}

   private SendEmailHelperRequest toSendEmailHelperRequestWarehouseManagerOrigin(Tuple3<TransferAsset, SystemParam, Item> tuple3){
      return SendEmailHelperRequest.builder()
            .mailTemplateId("EMAIL_ASSETS_MANAGEMENT_TRANSFER_ASSET_WH_MANAGER_ORIGIN")
            .mailSubject("Approval Request for Transfer Asset")
            .fromEmail(StringConstants.SENDER_EMAIL_ASSETS_MANAGEMENT)
//            .toEmail(transferAsset.getOriginWarehouseManagerEmail())
            .toEmail("jason.setiadi@gdn-commerce.com;")
            .identifierKey(StringConstants.TRANSFER_ASSET_NUMBER)
            .identifierValue(tuple3.getT1().getTransferAssetNumber())
            .emailVariables(constructVariableForTemplate(tuple3,Identity.WAREHOUSE_MANAGER))
            .build();
   }

   @SneakyThrows
   private Map<String, Object> constructVariableForTemplate(Tuple3<TransferAsset, SystemParam, Item> tuple3, Identity identity) {
      String assetNumbers = String.join(", ",tuple3.getT1().getAssetNumbers());
      Map<String, Object> variables = new HashMap<>();
      variables.put("transferAssetNumber",tuple3.getT1().getTransferAssetNumber());
      variables.put("itemName",tuple3.getT3().getItemName());
      variables.put("assetNumbers",assetNumbers);
      variables.put("assetQuantity",tuple3.getT1().getAssetNumbers().size());
      variables.put("origin",tuple3.getT1().getOrigin());
      variables.put("destination",tuple3.getT1().getDestination());
      variables.put("notes", StringUtils.isEmpty(tuple3.getT1().getNotes())?"-":tuple3.getT1().getNotes());
      if(Identity.WAREHOUSE_MANAGER.equals(identity)){
         String encodedTransferAssetNumber = URLEncoder.encode(tuple3.getT1().getTransferAssetNumber(), StandardCharsets.UTF_8.toString());
         variables.put("linkApproval",tuple3.getT2().getValue()+StringConstants.DETAIL_TA_PATH+encodedTransferAssetNumber);
      }
      return variables;
   }
}
