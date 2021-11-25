package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.GetTransferAssetHistoryCommand;
import com.gdn.warehouse.assetsmanagement.command.model.GetTransferAssetHistoryCommandRequest;
import com.gdn.warehouse.assetsmanagement.repository.TransferAssetHistoryRepository;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetTransferAssetHistoryWebResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class GetTransferAssetHistoryCommandImpl implements GetTransferAssetHistoryCommand {
   @Autowired
   private TransferAssetHistoryRepository transferAssetHistoryRepository;

   @Override
   public Mono<List<GetTransferAssetHistoryWebResponse>> execute(GetTransferAssetHistoryCommandRequest request) {
      return transferAssetHistoryRepository.findByTransferAssetNumberOrderByUpdatedDateAsc(request.getTransferAssetNumber())
            .flatMap(transferAssetHistory -> {
               GetTransferAssetHistoryWebResponse response = GetTransferAssetHistoryWebResponse.builder().build();
               BeanUtils.copyProperties(transferAssetHistory,response);
               response.setStatus(transferAssetHistory.getTransferAssetStatus().name());
               return Mono.just(response);
            }).collectList();
   }
}
