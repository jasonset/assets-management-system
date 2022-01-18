package com.gdn.warehouse.assetsmanagement.streaming.listener;

import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.json.helper.JsonHelper;
import com.gdn.warehouse.assetsmanagement.command.SendTransferAssetReminderCommand;
import com.gdn.warehouse.assetsmanagement.command.model.SendTransferAssetReminderCommandRequest;
import com.gdn.warehouse.assetsmanagement.streaming.model.TransferAssetEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class TransferAssetListenerTest {

   @InjectMocks
   private TransferAssetListener transferAssetListener;

   @Mock
   private JsonHelper jsonHelper;

   @Mock
   private CommandExecutor commandExecutor;

   private TransferAssetEvent transferAssetEvent;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      transferAssetEvent = TransferAssetEvent.builder().transferAssetNumber("TA-NUMBER").build();
   }

   @Test
   public void listenTransferAssetEvent() {
      when(jsonHelper.fromJson(anyString(),eq(TransferAssetEvent.class))).thenReturn(transferAssetEvent);
      when(commandExecutor.execute(eq(SendTransferAssetReminderCommand.class),any(SendTransferAssetReminderCommandRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));
      transferAssetListener.listenTransferAssetEvent(toConsumerRecord());
      verify(jsonHelper).fromJson(anyString(),eq(TransferAssetEvent.class));
      verify(commandExecutor).execute(eq(SendTransferAssetReminderCommand.class),any(SendTransferAssetReminderCommandRequest.class));
   }

   private ConsumerRecord<String, String> toConsumerRecord() {
      return new ConsumerRecord<>("topic", 1, 1L, "key", "{}");
   }
}