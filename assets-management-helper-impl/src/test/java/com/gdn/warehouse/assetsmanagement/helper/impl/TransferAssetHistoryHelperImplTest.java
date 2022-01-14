package com.gdn.warehouse.assetsmanagement.helper.impl;

import com.gdn.warehouse.assetsmanagement.entity.TransferAssetHistory;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetType;
import com.gdn.warehouse.assetsmanagement.helper.model.TransferAssetHistoryHelperRequest;
import com.gdn.warehouse.assetsmanagement.repository.TransferAssetHistoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransferAssetHistoryHelperImplTest {

   @InjectMocks
   private TransferAssetHistoryHelperImpl helper;

   @Mock
   private TransferAssetHistoryRepository transferAssetHistoryRepository;

   private TransferAssetHistoryHelperRequest request;
   private TransferAssetHistory transferAssetHistory;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      request = TransferAssetHistoryHelperRequest.builder()
            .transferAssetNumber("NUMBER")
            .transferAssetStatus(TransferAssetStatus.APPROVED)
            .transferAssetType(TransferAssetType.MOVE)
            .updatedDate(new Date())
            .updatedBy("NAME").build();

      transferAssetHistory = TransferAssetHistory.builder().build();
   }

   @Test
   public void createTransferAssetHistory() {
      when(transferAssetHistoryRepository.save(any(TransferAssetHistory.class))).thenReturn(Mono.just(transferAssetHistory));
      helper.createTransferAssetHistory(request).block();
      verify(transferAssetHistoryRepository).save(any(TransferAssetHistory.class));
   }
}