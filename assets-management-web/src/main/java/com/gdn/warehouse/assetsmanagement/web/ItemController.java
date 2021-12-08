package com.gdn.warehouse.assetsmanagement.web;

import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.common.helper.ResponseHelper;
import com.blibli.oss.backend.common.model.request.SortBy;
import com.blibli.oss.backend.common.model.request.SortByDirection;
import com.blibli.oss.backend.common.model.response.Response;
import com.blibli.oss.backend.mandatoryparameter.model.MandatoryParameter;
import com.blibli.oss.backend.mandatoryparameter.swagger.annotation.MandatoryParameterAtQuery;
import com.blibli.oss.backend.reactor.scheduler.SchedulerHelper;
import com.gdn.warehouse.assetsmanagement.command.CreateItemCommand;
import com.gdn.warehouse.assetsmanagement.command.GetAllItemCommand;
import com.gdn.warehouse.assetsmanagement.command.GetAllItemWithFilterCommand;
import com.gdn.warehouse.assetsmanagement.command.UpdateItemCommand;
import com.gdn.warehouse.assetsmanagement.command.model.CreateItemCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.GetAllItemCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.GetAllItemWithFilterCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.UpdateItemCommandRequest;
import com.gdn.warehouse.assetsmanagement.helper.util.SortDirectionHelper;
import com.gdn.warehouse.assetsmanagement.properties.AssetsManagementSchedulerProperties;
import com.gdn.warehouse.assetsmanagement.web.model.AssetsManagementApiPath;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateItemWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.GetItemWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.UpdateItemWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.generic.FilterAndPageRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.sort.GetItemSortWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetItemWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.ItemResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = AssetsManagementApiPath.ITEM_BASE_PATH)
@MandatoryParameterAtQuery
public class ItemController {
   @Autowired
   private CommandExecutor commandExecutor;

   @Autowired
   private SchedulerHelper schedulerHelper;

   @GetMapping(value = "/_get-all", produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<List<GetItemWebResponse>>> getAllItem(MandatoryParameter mandatoryParameter){
      return commandExecutor.execute(GetAllItemCommand.class,toGetAllItemCommandRequest())
            .map(dataPagingPair -> ResponseHelper.ok(dataPagingPair.getLeft(),dataPagingPair.getRight()))
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private GetAllItemCommandRequest toGetAllItemCommandRequest(){
      return GetAllItemCommandRequest.builder()
            .limit(null)
            .page(null)
            .sortBy("itemName")
            .sortOrder("asc").build();
   }

   private List<GetItemWebResponse> toGetItemWebResponses(List<ItemResponse> itemResponses){
      return itemResponses.stream().map(this::toGetItemWebResponse).collect(Collectors.toList());
   }

   private GetItemWebResponse toGetItemWebResponse(ItemResponse itemResponse){
      return GetItemWebResponse.builder()
            .code(itemResponse.getCode())
            .name(itemResponse.getName())
            .category(itemResponse.getCategory())
            .build();
   }

   @PostMapping(value = "/_get-all-item", produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<List<GetItemWebResponse>>> getAllItemWithFilter(MandatoryParameter mandatoryParameter,
                                                                         @RequestBody FilterAndPageRequest<GetItemWebRequest, GetItemSortWebRequest> request){
      return commandExecutor.execute(GetAllItemWithFilterCommand.class,toGetAllItemWithFilterCommandRequest(request))
            .map(dataPagingPair -> ResponseHelper.ok(toGetItemWebResponses(dataPagingPair.getLeft()),dataPagingPair.getRight()))
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private GetAllItemWithFilterCommandRequest toGetAllItemWithFilterCommandRequest(FilterAndPageRequest<GetItemWebRequest, GetItemSortWebRequest> request){
      return GetAllItemWithFilterCommandRequest.builder()
            .codeFilter(request.getFilters().getCode())
            .nameFilter(request.getFilters().getName())
            .categoryFilter(request.getFilters().getCategory())
            .limit(request.getItemPerPage())
            .page(request.getPage())
            .sortBy(getSortByFromRequest(request.getSorts())).build();
   }

   private List<SortBy> getSortByFromRequest(GetItemSortWebRequest sorts){
      List<SortBy> sortByList= new ArrayList<>();
      if(StringUtils.isNotEmpty(sorts.getCode())){
         SortBy itemCodeSort = SortBy.builder()
               .direction(SortByDirection.valueOf(SortDirectionHelper.getSortDirection(sorts.getCode().toUpperCase())))
               .propertyName("itemCode").build();
         sortByList.add(itemCodeSort);
      }
      if(StringUtils.isNotEmpty(sorts.getName())){
         SortBy itemNameSort = SortBy.builder()
               .direction(SortByDirection.valueOf(SortDirectionHelper.getSortDirection(sorts.getName().toUpperCase())))
               .propertyName("itemName").build();
         sortByList.add(itemNameSort);
      }
      return sortByList;
   }

   @PostMapping(value = "/_create", produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<String>> createItem(MandatoryParameter mandatoryParameter,
                                             @RequestBody CreateItemWebRequest request) throws Exception{
      return commandExecutor.execute(CreateItemCommand.class, toCreateItemCommandRequest(request, mandatoryParameter.getUsername()))
            .map(ResponseHelper::ok)
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private CreateItemCommandRequest toCreateItemCommandRequest(CreateItemWebRequest request, String username){
      return CreateItemCommandRequest.builder()
            .itemName(request.getItemName())
            .category(request.getCategory())
            .username(username).build();
   }

   @PostMapping(value = "/_update",produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<String>> updateItem(MandatoryParameter mandatoryParameter, @RequestBody UpdateItemWebRequest request) throws Exception{
      return commandExecutor.execute(UpdateItemCommand.class, toUpdateItemCommandRequest(request, mandatoryParameter.getUsername()))
            .map(ResponseHelper::ok)
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private UpdateItemCommandRequest toUpdateItemCommandRequest(UpdateItemWebRequest request, String username){
      return UpdateItemCommandRequest.builder()
            .itemCode(request.getItemCode())
            .itemName(request.getItemName())
            .category(request.getCategory())
            .username(username).build();
   }
}
