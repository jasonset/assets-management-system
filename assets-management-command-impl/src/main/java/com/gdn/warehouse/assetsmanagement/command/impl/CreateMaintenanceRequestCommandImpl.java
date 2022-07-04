package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.CreateMaintenanceRequestCommand;
import com.gdn.warehouse.assetsmanagement.command.model.CreateMaintenanceRequestCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.Maintenance;
import com.gdn.warehouse.assetsmanagement.entity.SystemParam;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import com.gdn.warehouse.assetsmanagement.enums.Identity;
import com.gdn.warehouse.assetsmanagement.enums.MaintenanceStatus;
import com.gdn.warehouse.assetsmanagement.helper.AssetValidatorHelper;
import com.gdn.warehouse.assetsmanagement.helper.GenerateSequenceHelper;
import com.gdn.warehouse.assetsmanagement.helper.SendEmailHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.SendEmailHelperRequest;
import com.gdn.warehouse.assetsmanagement.properties.StringConstants;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.MaintenanceRepository;
import com.gdn.warehouse.assetsmanagement.repository.SystemParamRepository;
import com.gdn.warehouse.assetsmanagement.repository.WarehouseRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CreateMaintenanceRequestCommandImpl implements CreateMaintenanceRequestCommand {

   @Autowired
   private MaintenanceRepository maintenanceRepository;

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
   private ItemRepository itemRepository;

   @Autowired
   private SystemParamRepository systemParamRepository;


   @Override
   public Mono<String> execute(CreateMaintenanceRequestCommandRequest request) {
      List<String> assetNumbers = request.getAssetNumbers().stream().map(String::trim).distinct().collect(Collectors.toList());
      return assetValidatorHelper.validateAssetFromRequest(assetNumbers)
            .flatMap(assets -> Mono.zip(createMaintenance(request,assets.get(0),assets,assetNumbers),
                  systemParamRepository.findByKey(StringConstants.BASE_PATH_UI)))
            .doOnSuccess(tuple2 -> {
               sendEmailHelper.sendEmail(toSendEmailHelperRequestWarehouseManager(tuple2));
               sendEmailHelper.sendEmail(toSendEmailHelperRequestUser(tuple2));
               sendEmailHelper.sendEmail(toSendEmailHelperRequestRequester(tuple2,request.getRequesterEmail()));
            }).map(tuple2 -> tuple2.getT1().getLeft().getMaintenanceNumber());
   }

   private Mono<Pair<Maintenance, Item>> createMaintenance(CreateMaintenanceRequestCommandRequest request, Asset asset,
                                                          List<Asset> assetList, List<String> assetNumbers){
      return Mono.zip(getMaintenanceNumber(),
                  itemRepository.findByItemCode(asset.getItemCode())
                        .switchIfEmpty(Mono.defer(()->Mono.error(new CommandErrorException("Item doesn't exist!", HttpStatus.BAD_REQUEST)))),
                  warehouseRepository.findByWarehouseName(asset.getLocation())
                        .switchIfEmpty(Mono.defer(()->Mono.error(new CommandErrorException("Warehouse doesn't exist!", HttpStatus.BAD_REQUEST)))))
            .flatMap(tuple -> maintenanceRepository.save(Maintenance.builder()
                  .maintenanceNumber(tuple.getT1())
                  .requester(request.getRequester())
                  .assetNumbers(assetNumbers)
                  .tanggalKerusakan(request.getTanggalKerusakan())
                  .deskripsiKerusakan(request.getDeskripsiKerusakan())
                  .tanggalLaporan(request.getTanggalLaporan())
                  .itemCode(tuple.getT2().getItemCode())
                  .location(asset.getLocation())
                  .status(MaintenanceStatus.REQUEST_PENDING)
//                        .poNumber(asset.getPoNumber())
//                        .poIssuedDate(tuple.getT1().getPoIssuedDate())
                  .warehouseManagerEmail(tuple.getT3().getEmail())
                  .createdBy(request.getUsername())
                  .createdDate(new Date())
                  .lastModifiedBy(request.getUsername())
                  .lastModifiedDate(new Date()).build())
                        .doOnSuccess(maintenance -> updateAssetStatus(assetList).subscribe())
                        .map(maintenance -> Pair.of(maintenance,tuple.getT2())));
   }

   private Mono<String> getMaintenanceNumber(){
      return generateSequenceHelper.generateDocumentNumber(DocumentType.MAINTENANCE);
   }

   private Mono<List<Asset>> updateAssetStatus(List<Asset> assets){
      return Flux.fromIterable(assets)
                  .map(asset -> {
                     asset.setStatus(AssetStatus.PENDING_MAINTENANCE_REQUEST);
                     return asset;
                  }).collectList()
                  .flatMap(assets1 -> assetRepository.saveAll(assets1).collectList());
   }

   //TODO email user
   //TODO email requester
   //TODO email wh manager
   private SendEmailHelperRequest toSendEmailHelperRequestWarehouseManager(Tuple2<Pair<Maintenance, Item>,SystemParam> tuple2){
      return SendEmailHelperRequest.builder()
            .mailTemplateId("EMAIL_ASSETS_MANAGEMENT_MAINTENANCE_WH_MANAGER")
            .mailSubject("Approval Request for Asset Maintenance")
            .fromEmail(StringConstants.SENDER_EMAIL_ASSETS_MANAGEMENT)
            //email warehouse
            .toEmail(tuple2.getT1().getLeft().getWarehouseManagerEmail())
            .identifierKey(StringConstants.MAINTENANCE_NUMBER)
            .identifierValue(tuple2.getT1().getLeft().getMaintenanceNumber())
            .emailVariables(constructVariableForTemplate(tuple2,Identity.WAREHOUSE_MANAGER))
            .build();
   }

   private SendEmailHelperRequest toSendEmailHelperRequestUser(Tuple2<Pair<Maintenance, Item>,SystemParam> tuple2){
      return SendEmailHelperRequest.builder()
            .mailTemplateId("EMAIL_ASSETS_MANAGEMENT_MAINTENANCE_USER")
            .mailSubject("Approval Notification for Asset Maintenance")
            .fromEmail(StringConstants.SENDER_EMAIL_ASSETS_MANAGEMENT)
            .toEmail("jason.setiadi@gdn-commerce.com;")
//            .toEmail(StringConstants.USER_EMAIL)
            .identifierKey(StringConstants.MAINTENANCE_NUMBER)
            .identifierValue(tuple2.getT1().getLeft().getMaintenanceNumber())
            .emailVariables(constructVariableForTemplate(tuple2,Identity.USER))
            .build();
   }

   private SendEmailHelperRequest toSendEmailHelperRequestRequester(Tuple2<Pair<Maintenance, Item>,SystemParam> tuple2, String requesterEmail){
      return SendEmailHelperRequest.builder()
            .mailTemplateId("EMAIL_ASSETS_MANAGEMENT_MAINTENANCE_REQUESTER")
            .mailSubject("Maintenance Form Submission Notification")
            .fromEmail(StringConstants.SENDER_EMAIL_ASSETS_MANAGEMENT)
            .toEmail(requesterEmail)
            .identifierKey(StringConstants.MAINTENANCE_NUMBER)
            .identifierValue(tuple2.getT1().getLeft().getMaintenanceNumber())
            .emailVariables(constructVariableForTemplate(tuple2,Identity.REQUESTER))
            .build();
   }

   @SneakyThrows
   private Map<String, Object> constructVariableForTemplate(Tuple2<Pair<Maintenance, Item>,SystemParam> tuple2, Identity identity) {
      String assetNumbers = String.join(", ",tuple2.getT1().getLeft().getAssetNumbers());
      Map<String, Object> variables = new HashMap<>();
      if(Identity.USER.equals(identity)||Identity.WAREHOUSE_MANAGER.equals(identity)){
         variables.put("itemName",tuple2.getT1().getRight().getItemName());
         variables.put("assetNumbers",assetNumbers);
         variables.put("assetQuantity",tuple2.getT1().getLeft().getAssetNumbers().size());
         variables.put("location",tuple2.getT1().getLeft().getLocation());
         variables.put("deskripsiKerusakan",tuple2.getT1().getLeft().getDeskripsiKerusakan());
      }
      if(Identity.WAREHOUSE_MANAGER.equals(identity)){
         String encodedMaintenanceNumber = URLEncoder.encode(tuple2.getT1().getLeft().getMaintenanceNumber(), StandardCharsets.UTF_8.toString());
         variables.put("linkApproval",tuple2.getT2().getValue()+StringConstants.DETAIL_MT_PATH+encodedMaintenanceNumber);
      }
      variables.put("maintenanceNumber",tuple2.getT1().getLeft().getMaintenanceNumber());
      return variables;
   }
}
