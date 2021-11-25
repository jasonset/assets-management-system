package com.gdn.warehouse.assetsmanagement.streaming.listener;

import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.json.helper.JsonHelper;
import com.gdn.warehouse.assetsmanagement.command.UpdateMaintenanceReminderScheduleCommand;
import com.gdn.warehouse.assetsmanagement.command.model.UpdateMaintenanceReminderScheduleCommandRequest;
import com.gdn.warehouse.assetsmanagement.streaming.model.AssetsManagementTopics;
import com.gdn.warehouse.assetsmanagement.streaming.model.MaintenanceReminderEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MaintenanceReminderListener {

   @Autowired
   private JsonHelper jsonHelper;

   @Autowired
   private CommandExecutor commandExecutor;

   @KafkaListener(topics = AssetsManagementTopics.SCHEDULED_MAINTENANCE_REMINDER)
   public void listenMaintenanceReminderEvent(ConsumerRecord<String, String> record){
      MaintenanceReminderEvent maintenanceReminderEvent = jsonHelper.fromJson(record.value(), MaintenanceReminderEvent.class);
      log.info("Listen Maintenance Reminder with Maintenance Reminder Number: " + maintenanceReminderEvent.getMaintenanceReminderNumber());
      commandExecutor.execute(UpdateMaintenanceReminderScheduleCommand.class,toUpdateMaintenanceReminderScheduleCommandRequest(maintenanceReminderEvent))
            .doOnSuccess(value -> log.info("Success updating and sending maintenance reminder for {}",maintenanceReminderEvent.getMaintenanceReminderNumber()))
            .doOnError(error -> log.error("Failed updating and sending maintenance reminder for {}",maintenanceReminderEvent.getMaintenanceReminderNumber()))
            .block();
   }

   private UpdateMaintenanceReminderScheduleCommandRequest toUpdateMaintenanceReminderScheduleCommandRequest(MaintenanceReminderEvent event){
      UpdateMaintenanceReminderScheduleCommandRequest commandRequest = UpdateMaintenanceReminderScheduleCommandRequest.builder().build();
      BeanUtils.copyProperties(event,commandRequest);
      return commandRequest;
   }
}
