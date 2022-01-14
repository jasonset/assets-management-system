package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.GetTransferAssetHistoryCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.TransferAssetHistory;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetType;
import com.gdn.warehouse.assetsmanagement.repository.TransferAssetHistoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetTransferAssetHistoryCommandImplTest {
   @InjectMocks
   private GetTransferAssetHistoryCommandImpl command;

   @Mock
   private TransferAssetHistoryRepository transferAssetHistoryRepository;

   private TransferAssetHistory transferAssetHistory;
   private GetTransferAssetHistoryCommandRequest commandRequest;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      transferAssetHistory = TransferAssetHistory.builder().transferAssetType(TransferAssetType.MOVE)
            .transferAssetStatus(TransferAssetStatus.APPROVED).build();
      commandRequest = GetTransferAssetHistoryCommandRequest.builder()
            .transferAssetNumber("TA-NUMBER").build();
   }

   @Test
   public void execute() {
      when(transferAssetHistoryRepository.findByTransferAssetNumberOrderByUpdatedDateAsc(anyString()))
            .thenReturn(Flux.just(transferAssetHistory));
      command.execute(commandRequest).block();
      verify(transferAssetHistoryRepository).findByTransferAssetNumberOrderByUpdatedDateAsc(anyString());
   }
}