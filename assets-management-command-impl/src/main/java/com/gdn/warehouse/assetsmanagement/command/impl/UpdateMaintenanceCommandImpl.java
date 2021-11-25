package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.UpdateMaintenanceCommand;
import com.gdn.warehouse.assetsmanagement.command.model.UpdateMaintenanceCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Maintenance;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.Identity;
import com.gdn.warehouse.assetsmanagement.enums.MaintenanceStatus;
import com.gdn.warehouse.assetsmanagement.helper.MaintenanceHistoryHelper;
import com.gdn.warehouse.assetsmanagement.helper.SendEmailHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.MaintenanceHistoryHelperRequest;
import com.gdn.warehouse.assetsmanagement.helper.model.SendEmailHelperRequest;
import com.gdn.warehouse.assetsmanagement.properties.StringConstants;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.MaintenanceRepository;
import com.gdn.warehouse.assetsmanagement.web.model.response.UpdateMaintenanceWebResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.ObjectUtils;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class UpdateMaintenanceCommandImpl implements UpdateMaintenanceCommand {

   @Autowired
   private MaintenanceRepository maintenanceRepository;

   @Autowired
   private AssetRepository assetRepository;

   @Autowired
   private MaintenanceHistoryHelper maintenanceHistoryHelper;

   @Autowired
   private SendEmailHelper sendEmailHelper;

   @Autowired
   private ItemRepository itemRepository;

   @Override
   public Mono<UpdateMaintenanceWebResponse> execute(UpdateMaintenanceCommandRequest request) {
      return maintenanceRepository.findByMaintenanceNumber(request.getMaintenanceNumber())
            .switchIfEmpty(Mono.defer(()->Mono.error(new CommandErrorException("Maintenance doesn't exist!", HttpStatus.BAD_REQUEST))))
            .filter(maintenance -> !MaintenanceStatus.DONE.equals(maintenance.getStatus()))
            .switchIfEmpty(Mono.defer(()->Mono.error(new CommandErrorException("Maintenance with status DONE can't be updated!",HttpStatus.BAD_REQUEST))))
            .flatMap(maintenance -> {
               Boolean isNewStatus = Boolean.FALSE;
               MaintenanceStatus newStatus = updateStatus(maintenance,request);
               if(!newStatus.equals(maintenance.getStatus())){
                  maintenance.setStatus(updateStatus(maintenance,request));
                  isNewStatus = Boolean.TRUE;
               }
               maintenance.setMaintenanceFee(request.getMaintenanceFee());
               maintenance.setTanggalKerusakan(new Date(request.getTanggalKerusakan()));
               maintenance.setDeskripsiKerusakan(request.getDeskripsiKerusakan());
               maintenance.setQuoSubmit(ObjectUtils.isEmpty(request.getQuoSubmit())?null:new Date(request.getQuoSubmit()));
               maintenance.setPoSubmit(ObjectUtils.isEmpty(request.getPoSubmit())?null:new Date(request.getPoSubmit()));
               maintenance.setPoApproved(ObjectUtils.isEmpty(request.getPoApproved())?null:new Date(request.getPoApproved()));
               maintenance.setTanggalService(ObjectUtils.isEmpty(request.getTanggalService())?null:new Date(request.getTanggalService()));
               maintenance.setTanggalNormal(ObjectUtils.isEmpty(request.getTanggalNormal())?null:new Date(request.getTanggalNormal()));
               maintenance.setNotes(ObjectUtils.isEmpty(request.getNotes())?null:request.getNotes());
               maintenance.setLastModifiedBy(request.getUsername());
               maintenance.setLastModifiedDate(new Date());
               Boolean finalIsNewStatus = isNewStatus;
               return maintenanceRepository.save(maintenance)
                     .flatMap(maintenance1 -> {
                        if ((maintenance1.getStatus().equals(MaintenanceStatus.ON_MAINTENANCE)||
                        maintenance1.getStatus().equals(MaintenanceStatus.DONE))&&BooleanUtils.isTrue(finalIsNewStatus)){
                           return assetRepository.findByAssetNumberIn(maintenance1.getAssetNumbers()).collectList()
                                 .flatMap(assets -> {
                                    if (MaintenanceStatus.ON_MAINTENANCE.equals(maintenance1.getStatus())){
                                       assets.forEach(asset -> asset.setStatus(AssetStatus.ON_MAINTENANCE));
                                    }else {
                                       assets.forEach(asset -> asset.setStatus(AssetStatus.NORMAL));
                                    }
                                    return assetRepository.saveAll(assets).collectList()
                                          .flatMap(assetList -> itemRepository.findByItemCode(maintenance1.getItemCode())
                                                .doOnSuccess(item -> {
                                                      sendEmailHelper.sendEmail(toSendEmailHelperRequestUser(maintenance1,item.getItemName()));
                                                      sendEmailHelper.sendEmail(toSendEmailHelperRequestWarehouseManager(maintenance1,item.getItemName()));
                                                }).flatMap(item -> maintenanceHistoryHelper.createMaintenanceHistory(toMaintenanceHistoryHelperRequest(maintenance1)))
                                                .flatMap(result -> mono(()->maintenance1)));
                                 });
                        }else {
                           return mono(()->maintenance1);
                        }
                     });
            }).map(maintenance -> UpdateMaintenanceWebResponse.builder()
                  .maintenanceNumber(maintenance.getMaintenanceNumber())
                  .updatedStatus(maintenance.getStatus().name()).build());
   }

   private MaintenanceStatus updateStatus(Maintenance maintenance, UpdateMaintenanceCommandRequest request){
      if(!Objects.isNull(request.getTanggalService()) &&
            Objects.isNull(request.getTanggalNormal())){
         return MaintenanceStatus.ON_MAINTENANCE;
      }else if (!Objects.isNull(request.getTanggalService()) &&
            !Objects.isNull(request.getTanggalNormal())){
         return MaintenanceStatus.DONE;
      }else{
         return maintenance.getStatus();
      }
   }

   private MaintenanceHistoryHelperRequest toMaintenanceHistoryHelperRequest(Maintenance maintenance){
      return MaintenanceHistoryHelperRequest.builder().maintenanceNumber(maintenance.getMaintenanceNumber())
            .maintenanceStatus(maintenance.getStatus()).updatedDate(new Date())
            .updatedBy(maintenance.getLastModifiedBy()).build();
   }

   //TODO email user
   //TODO email wh manager
   private SendEmailHelperRequest toSendEmailHelperRequestWarehouseManager(Maintenance maintenance, String itemName){
      return SendEmailHelperRequest.builder()
            .mailTemplateId("EMAIL_ASSETS_MANAGEMENT_MAINTENANCE_UPDATE")
            .mailSubject("Update Notification for Asset Maintenance")
            .fromEmail(StringConstants.SENDER_EMAIL_ASSETS_MANAGEMENT)
            //email warehouse
            .toEmail(maintenance.getWarehouseManagerEmail())
            .identifierKey(StringConstants.MAINTENANCE_NUMBER)
            .identifierValue(maintenance.getMaintenanceNumber())
            .emailVariables(constructVariableForTemplate(maintenance, Identity.WAREHOUSE_MANAGER,itemName))
            .build();
   }

   private SendEmailHelperRequest toSendEmailHelperRequestUser(Maintenance maintenance, String itemName){
      return SendEmailHelperRequest.builder()
            .mailTemplateId("EMAIL_ASSETS_MANAGEMENT_MAINTENANCE_UPDATE")
            .mailSubject("Update Notification for Asset Maintenance")
            .fromEmail(StringConstants.SENDER_EMAIL_ASSETS_MANAGEMENT)
            .toEmail("jason.setiadi@gdn-commerce.com;")
//            .toEmail(StringConstants.USER_EMAIL)
            .identifierKey(StringConstants.MAINTENANCE_NUMBER)
            .identifierValue(maintenance.getMaintenanceNumber())
            .emailVariables(constructVariableForTemplate(maintenance,Identity.USER,itemName))
            .build();
   }

   @SneakyThrows
   private Map<String, Object> constructVariableForTemplate(Maintenance maintenance, Identity identity, String itemName) {
      String assetNumbers = String.join(", ",maintenance.getAssetNumbers());
      Map<String, Object> variables = new HashMap<>();
      variables.put("itemName",itemName);
      variables.put("assetNumbers",assetNumbers);
      variables.put("assetQuantity",maintenance.getAssetNumbers().size());
      variables.put("location",maintenance.getLocation());
      variables.put("deskripsiKerusakan",maintenance.getDeskripsiKerusakan());
      if(Identity.WAREHOUSE_MANAGER.equals(identity)){
         variables.put("receiver","Managers");
      }
      if(Identity.USER.equals(identity)){
         variables.put("receiver","All");
      }
      variables.put("status",maintenance.getStatus().name());
      variables.put("maintenanceNumber",maintenance.getMaintenanceNumber());
      return variables;
   }
}
