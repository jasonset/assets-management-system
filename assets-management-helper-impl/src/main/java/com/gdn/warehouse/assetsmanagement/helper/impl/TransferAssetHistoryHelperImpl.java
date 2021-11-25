package com.gdn.warehouse.assetsmanagement.helper.impl;

import com.gdn.warehouse.assetsmanagement.entity.TransferAssetHistory;
import com.gdn.warehouse.assetsmanagement.helper.TransferAssetHistoryHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.TransferAssetHistoryHelperRequest;
import com.gdn.warehouse.assetsmanagement.repository.TransferAssetHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class TransferAssetHistoryHelperImpl implements TransferAssetHistoryHelper {
   @Autowired
   private TransferAssetHistoryRepository transferAssetHistoryRepository;

   @Override
   public Mono<Boolean> createTransferAssetHistory(TransferAssetHistoryHelperRequest request) {
      return transferAssetHistoryRepository.save(TransferAssetHistory.builder()
            .transferAssetNumber(request.getTransferAssetNumber())
            .transferAssetStatus(request.getTransferAssetStatus())
            .transferAssetType(request.getTransferAssetType())
            .updatedBy(request.getUpdatedBy())
            .updatedDate(request.getUpdatedDate()).build())
            .map(result -> Boolean.TRUE);
   }
}
