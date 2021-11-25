package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.UpdateMaintenanceReminderScheduleCommand;
import com.gdn.warehouse.assetsmanagement.command.model.UpdateMaintenanceReminderScheduleCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Maintenance;
import com.gdn.warehouse.assetsmanagement.entity.MaintenanceReminder;
import com.gdn.warehouse.assetsmanagement.entity.Schedule;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import com.gdn.warehouse.assetsmanagement.enums.MaintenanceStatus;
import com.gdn.warehouse.assetsmanagement.helper.GenerateSequenceHelper;
import com.gdn.warehouse.assetsmanagement.helper.ScheduleHelper;
import com.gdn.warehouse.assetsmanagement.helper.SchedulerPlatformHelper;
import com.gdn.warehouse.assetsmanagement.helper.SendEmailHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.CreateScheduleHelperRequest;
import com.gdn.warehouse.assetsmanagement.helper.model.SendEmailHelperRequest;
import com.gdn.warehouse.assetsmanagement.properties.StringConstants;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.MaintenanceReminderRepository;
import com.gdn.warehouse.assetsmanagement.repository.MaintenanceRepository;
import com.gdn.warehouse.assetsmanagement.repository.ScheduleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class UpdateMaintenanceReminderScheduleCommandImpl implements UpdateMaintenanceReminderScheduleCommand {
   private final String commaDelimiter = ", ";

   private MaintenanceReminderRepository maintenanceReminderRepository;
   private ScheduleRepository scheduleRepository;
   private ScheduleHelper scheduleHelper;
   private SchedulerPlatformHelper schedulerPlatformHelper;
   private GenerateSequenceHelper generateSequenceHelper;
   private MaintenanceRepository maintenanceRepository;
   private SendEmailHelper sendEmailHelper;
   private AssetRepository assetRepository;
   private ItemRepository itemRepository;


   @Override
   public Mono<Boolean> execute(UpdateMaintenanceReminderScheduleCommandRequest request) {
      return Mono.zip(maintenanceReminderRepository.findByMaintenanceReminderNumber(request.getMaintenanceReminderNumber()),
                  scheduleRepository.findByIdentifier(request.getMaintenanceReminderNumber()))
            .flatMap(tuple -> {
               Date newDate = DateUtils.addMonths(tuple.getT1().getScheduledDate(), tuple.getT2().getInterval());
               return updateSchedule(tuple.getT2(), newDate)
                     .doOnSuccess(schedule -> schedulerPlatformHelper.sendToSchedulerPlatform(schedule))
                     .flatMap(schedule -> {
                        tuple.getT1().setScheduledDate(newDate);
                        tuple.getT1().setPreviousExecutionTime(new Date());
                        tuple.getT1().setLastModifiedBy("SYSTEM");
                        tuple.getT1().setLastModifiedDate(new Date());
                        return maintenanceReminderRepository.save(tuple.getT1());
                     }).flatMap(maintenanceReminder -> assetRepository.findByAssetNumberIn(maintenanceReminder.getAssetNumbers()).collectList()
                                 .flatMap(assets -> {
                                    boolean allNormal = assets.stream().allMatch(asset -> AssetStatus.NORMAL.equals(asset.getStatus()));
                                    if(allNormal){
                                       return createMaintenanceFromReminder(maintenanceReminder)
                                             .flatMap(maintenance -> itemRepository.findByItemCode(maintenanceReminder.getItemCode())
                                                   .doOnSuccess(item -> sendEmailHelper
                                                         .sendEmail(toSendEmailHelperRequestUser(maintenanceReminder, maintenance.getMaintenanceNumber(),item.getItemName()))))
                                             .flatMap(result->updateAssetStatus(assets));
                                    }else {
                                       return Mono.defer(()-> Mono.just(assets))
                                             .flatMap(assets1 -> itemRepository.findByItemCode(maintenanceReminder.getItemCode())
                                                         .doOnSuccess(item -> sendEmailHelper
                                                               .sendEmail(toSendEmailHelperRequestUserMaintenanceNotCreated(maintenanceReminder,item.getItemName()))))
                                             .map(result->assets);
                                    }
                                 }));
            }).map(assets -> Boolean.TRUE);
   }

   private Mono<Schedule> updateSchedule(Schedule schedule, Date nextSchedule) {
      return scheduleHelper.saveSchedule(CreateScheduleHelperRequest.builder()
            .identifier(schedule.getIdentifier())
            .nextSchedule(nextSchedule)
            .payload(schedule.getPayload())
            .timeUnit(schedule.getTimeUnit())
            .interval(schedule.getInterval())
            .lastExecutionTime(new Date()).build());
   }

   private Mono<Maintenance> createMaintenanceFromReminder(MaintenanceReminder maintenanceReminder) {
      return generateSequenceHelper.generateDocumentNumber(DocumentType.MAINTENANCE)
            .flatMap(maintenanceNumber -> maintenanceRepository.save(Maintenance.builder()
                  .maintenanceNumber(maintenanceNumber)
                  .location(maintenanceReminder.getAssetLocation())
                  .requester("SCHEDULED BY SYSTEM")
                  .assetNumbers(maintenanceReminder.getAssetNumbers())
                  .tanggalKerusakan(null)
                  .deskripsiKerusakan(null)
                  .tanggalLaporan(null)
                  .itemCode(maintenanceReminder.getItemCode())
                  .status(MaintenanceStatus.SCHEDULED)
//                  .poNumber(maintenanceReminder.getAssetPoNumber())
//                  .poIssuedDate(maintenanceReminder.getAssetPoIssuedDate())
                        .createdBy("SYSTEM")
                        .createdDate(new Date())
                        .lastModifiedBy("SYSTEM")
                        .lastModifiedDate(new Date())
                  .build()));
   }

   private SendEmailHelperRequest toSendEmailHelperRequestUser(MaintenanceReminder maintenanceReminder, String maintenanceNumber,
                                                               String itemName) {
      String emailList = String.join(StringConstants.DELIMITER, maintenanceReminder.getEmailList());
      return SendEmailHelperRequest.builder()
            //TODO mailTemplateId
            .mailTemplateId("EMAIL_ASSETS_MANAGEMENT_MAINTENANCE_REMINDER")
            .mailSubject("Maintenance Reminder")
            .fromEmail(StringConstants.SENDER_EMAIL_ASSETS_MANAGEMENT)
            .toEmail(emailList)
            .identifierKey(StringConstants.MAINTENANCE_NUMBER)
            .identifierValue(maintenanceNumber)
            .emailVariables(constructVariableForTemplate(maintenanceReminder,maintenanceNumber,itemName))
            .build();
   }

   private Map<String, Object> constructVariableForTemplate(MaintenanceReminder maintenanceReminder, String maintenanceNumber,
                                                            String itemName) {
      String assetNumbers = String.join(commaDelimiter,maintenanceReminder.getAssetNumbers());
      Date newDate = new Date();
      LocalDate date = newDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      LocalDate nextDate = maintenanceReminder.getScheduledDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      String dateStr = date.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));
      String nextDateStr = nextDate.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));
      Map<String, Object> variables = new HashMap<>();
      variables.put("maintenanceNumber",maintenanceNumber);
      variables.put("itemName",itemName);
      variables.put("assetNumbers",assetNumbers);
      variables.put("assetQuantity",maintenanceReminder.getAssetNumbers().size());
      variables.put("location",maintenanceReminder.getAssetLocation());
      variables.put("scheduledDate",dateStr);
      variables.put("nextScheduledDate",nextDateStr);
      variables.put("notes","-");
      return variables;
   }

   private Mono<List<Asset>> updateAssetStatus(List<Asset> assets){
      assets.forEach(asset -> asset.setStatus(AssetStatus.SCHEDULED_MAINTENANCE));
      return assetRepository.saveAll(assets).collectList();
   }

   private SendEmailHelperRequest toSendEmailHelperRequestUserMaintenanceNotCreated(MaintenanceReminder maintenanceReminder, String itemName) {
      String emailList = String.join(StringConstants.DELIMITER, maintenanceReminder.getEmailList());
      return SendEmailHelperRequest.builder()
            //TODO mailTemplateId
            .mailTemplateId("EMAIL_ASSETS_MANAGEMENT_MAINTENANCE_REMINDER")
            .mailSubject("Maintenance Reminder")
            .fromEmail(StringConstants.SENDER_EMAIL_ASSETS_MANAGEMENT)
            .toEmail(emailList)
            .identifierKey(StringConstants.MAINTENANCE_REMINDER_NUMBER)
            .identifierValue(maintenanceReminder.getMaintenanceReminderNumber())
            .emailVariables(constructVariableForTemplateMaintenanceNotCreated(maintenanceReminder,itemName))
            .build();
   }

   private Map<String, Object> constructVariableForTemplateMaintenanceNotCreated(MaintenanceReminder maintenanceReminder, String itemName) {
      String assetNumbers = String.join(", ",maintenanceReminder.getAssetNumbers());
      Date newDate = new Date();
      LocalDate date = newDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      LocalDate nextDate = maintenanceReminder.getScheduledDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      String dateStr = date.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));
      String nextDateStr = nextDate.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));
      Map<String, Object> variables = new HashMap<>();
      variables.put("maintenanceNumber","-");
      variables.put("itemName",itemName);
      variables.put("assetNumbers",assetNumbers);
      variables.put("assetQuantity",maintenanceReminder.getAssetNumbers().size());
      variables.put("location",maintenanceReminder.getAssetLocation());
      variables.put("scheduledDate",dateStr);
      variables.put("nextScheduledDate",nextDateStr);
      variables.put("notes","Maintenance tidak terbuat secara otomatis karena Asset tidak dalam status NORMAL");
      return variables;
   }
}
