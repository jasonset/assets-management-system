package com.gdn.warehouse.assetsmanagement.web;

import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.common.helper.ResponseHelper;
import com.blibli.oss.backend.common.model.request.SortBy;
import com.blibli.oss.backend.common.model.request.SortByDirection;
import com.blibli.oss.backend.common.model.response.Response;
import com.blibli.oss.backend.mandatoryparameter.model.MandatoryParameter;
import com.blibli.oss.backend.mandatoryparameter.swagger.annotation.MandatoryParameterAtQuery;
import com.blibli.oss.backend.reactor.scheduler.SchedulerHelper;
import com.gdn.warehouse.assetsmanagement.command.ApproveTransferAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.CreateTransferAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.DeliveredTransferAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.GetTransferAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.GetTransferAssetDetailCommand;
import com.gdn.warehouse.assetsmanagement.command.GetTransferAssetHistoryCommand;
import com.gdn.warehouse.assetsmanagement.command.OnDeliveryTransferAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.model.ApproveTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.CreateTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.DeliveredTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.GetTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.GetTransferAssetDetailCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.GetTransferAssetHistoryCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.OnDeliveryTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.helper.util.SortDirectionHelper;
import com.gdn.warehouse.assetsmanagement.properties.AssetsManagementSchedulerProperties;
import com.gdn.warehouse.assetsmanagement.web.model.AssetsManagementApiPath;
import com.gdn.warehouse.assetsmanagement.web.model.request.ApproveTransferAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateTransferAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.DeliveredTransferAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.GetTransferAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.OnDeliveryTransferAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.generic.FilterAndPageRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.sort.GetTransferAssetSortWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetTransferAssetDetailWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetTransferAssetHistoryWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetTransferAssetWebResponse;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@MandatoryParameterAtQuery
@RequestMapping(value = AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH)
public class TransferAssetController {
   @Autowired
   private CommandExecutor commandExecutor;

   @Autowired
   private SchedulerHelper schedulerHelper;

   @PostMapping(value = "/_create", produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<String>> createTransferAsset(MandatoryParameter mandatoryParameter, @RequestBody CreateTransferAssetWebRequest request) {
      return commandExecutor.execute(CreateTransferAssetCommand.class,toCreateTransferAssetCommandRequest(request,mandatoryParameter.getUsername()))
            .map(ResponseHelper::ok)
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private CreateTransferAssetCommandRequest toCreateTransferAssetCommandRequest(CreateTransferAssetWebRequest request, String username){
      CreateTransferAssetCommandRequest commandRequest = CreateTransferAssetCommandRequest.builder().build();
      BeanUtils.copyProperties(request,commandRequest);
      commandRequest.setUsername(username);
      return commandRequest;
   }

   @PostMapping(value = "/_approve", produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<Boolean>> approveTransferAsset(MandatoryParameter mandatoryParameter, @RequestBody ApproveTransferAssetWebRequest request) {
      return commandExecutor.execute(ApproveTransferAssetCommand.class,toApproveTransferAssetCommandRequest(request, mandatoryParameter.getUsername()))
            .map(ResponseHelper::ok)
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private ApproveTransferAssetCommandRequest toApproveTransferAssetCommandRequest(ApproveTransferAssetWebRequest request, String username){
      ApproveTransferAssetCommandRequest commandRequest = ApproveTransferAssetCommandRequest.builder().build();
      BeanUtils.copyProperties(request,commandRequest);
      commandRequest.setUsername(username);
      return commandRequest;
   }

   @PostMapping(value = "/_delivered", produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<Boolean>> deliveredTransferAsset(MandatoryParameter mandatoryParameter, @RequestBody DeliveredTransferAssetWebRequest request) {
      return commandExecutor.execute(DeliveredTransferAssetCommand.class,toDeliveredTransferAssetCommandRequest(request, mandatoryParameter.getUsername()))
            .map(ResponseHelper::ok)
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private DeliveredTransferAssetCommandRequest toDeliveredTransferAssetCommandRequest(DeliveredTransferAssetWebRequest request,String username){
      DeliveredTransferAssetCommandRequest commandRequest = DeliveredTransferAssetCommandRequest.builder().build();
      BeanUtils.copyProperties(request,commandRequest);
      commandRequest.setUsername(username);
      return commandRequest;
   }

   @PostMapping(value = "/_on-delivery", produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<Boolean>> onDeliveryTransferAsset(MandatoryParameter mandatoryParameter, @RequestBody OnDeliveryTransferAssetWebRequest request) {
      return commandExecutor.execute(OnDeliveryTransferAssetCommand.class,toOnDeliveryTransferAssetCommandRequest(request, mandatoryParameter.getUsername()))
            .map(ResponseHelper::ok)
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private OnDeliveryTransferAssetCommandRequest toOnDeliveryTransferAssetCommandRequest(OnDeliveryTransferAssetWebRequest request, String username){
      OnDeliveryTransferAssetCommandRequest commandRequest = OnDeliveryTransferAssetCommandRequest.builder().build();
      BeanUtils.copyProperties(request,commandRequest);
      commandRequest.setUsername(username);
      return commandRequest;
   }

   @SneakyThrows
   @GetMapping(value = "/_get-detail/{transferAssetNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<GetTransferAssetDetailWebResponse>> getDetailTransferAsset(MandatoryParameter mandatoryParameter,
                                                                                   @PathVariable("transferAssetNumber") String transferAssetNumber) {
      transferAssetNumber = URLDecoder.decode(transferAssetNumber, StandardCharsets.UTF_8.toString());
      return commandExecutor.execute(GetTransferAssetDetailCommand.class,toGetTransferAssetDetailCommandRequest(transferAssetNumber))
            .map(ResponseHelper::ok)
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private GetTransferAssetDetailCommandRequest toGetTransferAssetDetailCommandRequest(String transferAssetNumber){
      return GetTransferAssetDetailCommandRequest.builder().transferAssetNumber(transferAssetNumber).build();
   }

   @PostMapping(value = "/_get-all", produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<List<GetTransferAssetWebResponse>>> getTransferAssets(MandatoryParameter mandatoryParameter,
                                                                              @RequestBody FilterAndPageRequest<GetTransferAssetWebRequest, GetTransferAssetSortWebRequest> request){
      return commandExecutor.execute(GetTransferAssetCommand.class,toGetTransferAssetCommandRequest(request))
            .map(transferAssetPair -> ResponseHelper.ok(transferAssetPair.getLeft(),transferAssetPair.getRight()))
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private GetTransferAssetCommandRequest toGetTransferAssetCommandRequest(FilterAndPageRequest<GetTransferAssetWebRequest,GetTransferAssetSortWebRequest> request){
      return GetTransferAssetCommandRequest.builder()
            .transferAssetNumberFilter(request.getFilters().getTransferAssetNumber())
            .assetNumberFilter(request.getFilters().getAssetNumber())
            .originFilter(request.getFilters().getOrigin())
            .destinationFilter(request.getFilters().getDestination())
            .itemCodeFilter(request.getFilters().getItemCode())
            .statusFilter(request.getFilters().getStatus())
            .transferAssetTypeFilter(request.getFilters().getTransferAssetType())
            .referenceNumberFilter(request.getFilters().getReferenceNumber())
            .limit(request.getItemPerPage())
            .page(request.getPage())
            .sortBy(getSortByFromRequest(request.getSorts())).build();
   }

   private List<SortBy> getSortByFromRequest(GetTransferAssetSortWebRequest sorts){
      List<SortBy> sortByList= new ArrayList<>();
      if(StringUtils.isNotEmpty(sorts.getTransferAssetNumber())){
         SortBy assetNumberSort = SortBy.builder()
               .direction(SortByDirection.valueOf(SortDirectionHelper.getSortDirection(sorts.getTransferAssetNumber().toUpperCase())))
               .propertyName("transferAssetNumber").build();
         sortByList.add(assetNumberSort);
      }
      return sortByList;
   }

   @SneakyThrows
   @GetMapping(value = "/_get-all-history/{transferAssetNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<List<GetTransferAssetHistoryWebResponse>>> getTransferAssetHistory(MandatoryParameter mandatoryParameter,
                                                                                           @PathVariable("transferAssetNumber") String transferAssetNumber){
      transferAssetNumber = URLDecoder.decode(transferAssetNumber, StandardCharsets.UTF_8.toString());
      return commandExecutor.execute(GetTransferAssetHistoryCommand.class,toGetTransferAssetHistoryCommandRequest(transferAssetNumber))
            .map(ResponseHelper::ok)
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private GetTransferAssetHistoryCommandRequest toGetTransferAssetHistoryCommandRequest(String transferAssetNumber){
      return GetTransferAssetHistoryCommandRequest.builder().transferAssetNumber(transferAssetNumber).build();
   }
}
