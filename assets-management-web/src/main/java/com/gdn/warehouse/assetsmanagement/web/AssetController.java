package com.gdn.warehouse.assetsmanagement.web;

import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.common.helper.ResponseHelper;
import com.blibli.oss.backend.common.model.request.SortBy;
import com.blibli.oss.backend.common.model.request.SortByDirection;
import com.blibli.oss.backend.common.model.response.Response;
import com.blibli.oss.backend.mandatoryparameter.model.MandatoryParameter;
import com.blibli.oss.backend.mandatoryparameter.swagger.annotation.MandatoryParameterAtQuery;
import com.blibli.oss.backend.reactor.scheduler.SchedulerHelper;
import com.gdn.warehouse.assetsmanagement.command.CreateAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.GetAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.GetAssetDetailCommand;
import com.gdn.warehouse.assetsmanagement.command.UpdateAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.model.CreateAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.GetAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.GetAssetDetailCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.UpdateAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.helper.util.SortDirectionHelper;
import com.gdn.warehouse.assetsmanagement.properties.AssetsManagementSchedulerProperties;
import com.gdn.warehouse.assetsmanagement.web.model.AssetsManagementApiPath;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.GetAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.UpdateAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.generic.FilterAndPageRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.sort.GetAssetSortWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetAssetDetailWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetAssetWebResponse;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = AssetsManagementApiPath.ASSET_BASE_PATH)
@MandatoryParameterAtQuery
public class AssetController {

   @Autowired
   private CommandExecutor commandExecutor;

   @Autowired
   private SchedulerHelper schedulerHelper;

   @RequestMapping(value = "/_create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<String>> createAsset(MandatoryParameter mandatoryParameter,
                                              @RequestBody CreateAssetWebRequest request) throws Exception {
      return commandExecutor.execute(CreateAssetCommand.class,toCreateAssetCommandRequest(request, mandatoryParameter.getUsername()))
            .map(ResponseHelper::ok)
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private CreateAssetCommandRequest toCreateAssetCommandRequest(CreateAssetWebRequest request,String username) throws Exception {
      CreateAssetCommandRequest commandRequest = CreateAssetCommandRequest.builder().build();
      BeanUtils.copyProperties(request,commandRequest);
      commandRequest.setUsername(username);
      return commandRequest;
   }

   @RequestMapping(value = "/_update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<Boolean>> updateAsset(MandatoryParameter mandatoryParameter,
                                              @RequestBody UpdateAssetWebRequest request) throws Exception {
      return commandExecutor.execute(UpdateAssetCommand.class,toUpdateAssetCommandRequest(request, mandatoryParameter.getUsername()))
            .map(ResponseHelper::ok)
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private UpdateAssetCommandRequest toUpdateAssetCommandRequest(UpdateAssetWebRequest request,String username) throws Exception {
      UpdateAssetCommandRequest commandRequest = UpdateAssetCommandRequest.builder().build();
      BeanUtils.copyProperties(request,commandRequest);
      commandRequest.setUsername(username);
      return commandRequest;
   }

   @SneakyThrows
   @RequestMapping(value = "/_get-detail/{assetNumber}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<GetAssetDetailWebResponse>> getAssetDetail(MandatoryParameter mandatoryParameter, @PathVariable("assetNumber") String assetNumber) {
      assetNumber = URLDecoder.decode(assetNumber, StandardCharsets.UTF_8.toString());
      return commandExecutor.execute(GetAssetDetailCommand.class,toGetAssetDetailCommandRequest(assetNumber))
            .map(ResponseHelper::ok)
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private GetAssetDetailCommandRequest toGetAssetDetailCommandRequest(String assetNumber){
      return GetAssetDetailCommandRequest.builder().assetNumber(assetNumber).build();
   }

   @RequestMapping(value = "/_get-all", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<List<GetAssetWebResponse>>> getAssets(MandatoryParameter mandatoryParameter,
         @RequestBody FilterAndPageRequest<GetAssetWebRequest, GetAssetSortWebRequest> request){
      return commandExecutor.execute(GetAssetCommand.class,toGetAssetCommandRequest(request))
            .map(assetPair -> ResponseHelper.ok(assetPair.getLeft(),assetPair.getRight()))
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private GetAssetCommandRequest toGetAssetCommandRequest(FilterAndPageRequest<GetAssetWebRequest,GetAssetSortWebRequest> request){
      return GetAssetCommandRequest.builder()
            .assetNumberFilter(request.getFilters().getAssetNumber())
            .organisationFilter(request.getFilters().getOrganisation())
            .vendorFilter(request.getFilters().getVendor())
            .itemCodeFilter(request.getFilters().getItemCode())
            .locationFilter(request.getFilters().getLocation())
            .statusFilter(request.getFilters().getStatus())
            .categoryFilter(request.getFilters().getCategory())
            .limit(request.getItemPerPage())
            .page(request.getPage())
            .sortBy(getSortByFromRequest(request.getSorts())).build();
   }

   private List<SortBy> getSortByFromRequest(GetAssetSortWebRequest sorts){
      List<SortBy> sortByList= new ArrayList<>();
      if(StringUtils.isNotEmpty(sorts.getAssetNumber())){
         SortBy assetNumberSort = SortBy.builder()
               .direction(SortByDirection.valueOf(SortDirectionHelper.getSortDirection(sorts.getAssetNumber().toUpperCase())))
               .propertyName("assetNumber").build();
         sortByList.add(assetNumberSort);
      }
      return sortByList;
   }
}
