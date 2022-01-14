package com.gdn.warehouse.assetsmanagement.streaming.listener;

import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.json.helper.JsonHelper;
import com.gdn.warehouse.assetsmanagement.streaming.model.AssetsManagementTopics;
import com.gdn.warehouse.assetsmanagement.streaming.model.TransferAssetEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransferAssetListener {

   @Autowired
   private JsonHelper jsonHelper;

   @Autowired
   private CommandExecutor commandExecutor;

   @KafkaListener(topics = AssetsManagementTopics.TRANSFER_ASSET_DURATION)
   public void listenTransferAssetEvent(ConsumerRecord<String,String> record){
      TransferAssetEvent transferAssetEvent = jsonHelper.fromJson(record.value(), TransferAssetEvent.class);
      log.info("Listen Transfer Asset with Transfer Asset Number: " + transferAssetEvent.getTransferAssetNumber());
      commandExecutor.execute()
   }
}
