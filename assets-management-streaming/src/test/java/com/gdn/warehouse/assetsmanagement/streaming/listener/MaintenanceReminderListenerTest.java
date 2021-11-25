package com.gdn.warehouse.assetsmanagement.streaming.listener;

import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.json.helper.JsonHelper;
import com.gdn.warehouse.assetsmanagement.command.UpdateMaintenanceReminderScheduleCommand;
import com.gdn.warehouse.assetsmanagement.command.model.UpdateMaintenanceReminderScheduleCommandRequest;
import com.gdn.warehouse.assetsmanagement.streaming.model.MaintenanceReminderEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;

public class MaintenanceReminderListenerTest {

   @InjectMocks
   private MaintenanceReminderListener maintenanceReminderListener;

   @Mock
   private JsonHelper jsonHelper;

   @Mock
   private CommandExecutor commandExecutor;

   private MaintenanceReminderEvent maintenanceReminderEvent;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      maintenanceReminderEvent = MaintenanceReminderEvent.builder().maintenanceReminderNumber("MR-NUMBER").build();
   }

   @Test
   public void listenMaintenanceReminderEvent() {
      when(jsonHelper.fromJson(anyString(),eq(MaintenanceReminderEvent.class))).thenReturn(maintenanceReminderEvent);
      when(commandExecutor.execute(eq(UpdateMaintenanceReminderScheduleCommand.class),
            any(UpdateMaintenanceReminderScheduleCommandRequest.class))).thenReturn(Mono.just(Boolean.TRUE));
      maintenanceReminderListener.listenMaintenanceReminderEvent(toConsumerRecord());
      verify(jsonHelper).fromJson(anyString(),eq(MaintenanceReminderEvent.class));
      verify(commandExecutor).execute(eq(UpdateMaintenanceReminderScheduleCommand.class),
            any(UpdateMaintenanceReminderScheduleCommandRequest.class));
   }

   private ConsumerRecord<String, String> toConsumerRecord() {
      return new ConsumerRecord<>("topic", 1, 1L, "key", "{}");
   }
}