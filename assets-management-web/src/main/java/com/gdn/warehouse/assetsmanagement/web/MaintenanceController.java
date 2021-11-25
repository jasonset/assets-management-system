package com.gdn.warehouse.assetsmanagement.web;

import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.common.helper.ResponseHelper;
import com.blibli.oss.backend.common.model.request.SortBy;
import com.blibli.oss.backend.common.model.request.SortByDirection;
import com.blibli.oss.backend.common.model.response.Response;
import com.blibli.oss.backend.mandatoryparameter.model.MandatoryParameter;
import com.blibli.oss.backend.mandatoryparameter.swagger.annotation.MandatoryParameterAtQuery;
import com.blibli.oss.backend.reactor.scheduler.SchedulerHelper;
import com.gdn.warehouse.assetsmanagement.command.GetMaintenanceCommand;
import com.gdn.warehouse.assetsmanagement.command.GetMaintenanceDetailCommand;
import com.gdn.warehouse.assetsmanagement.command.GetMaintenanceHistoryCommand;
import com.gdn.warehouse.assetsmanagement.command.RejectMaintenanceCommand;
import com.gdn.warehouse.assetsmanagement.command.UpdateMaintenanceCommand;
import com.gdn.warehouse.assetsmanagement.command.model.GetMaintenanceCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.GetMaintenanceDetailCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.GetMaintenanceHistoryCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.RejectMaintenanceCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.UpdateMaintenanceCommandRequest;
import com.gdn.warehouse.assetsmanagement.helper.util.SortDirectionHelper;
import com.gdn.warehouse.assetsmanagement.properties.AssetsManagementSchedulerProperties;
import com.gdn.warehouse.assetsmanagement.web.model.AssetsManagementApiPath;
import com.gdn.warehouse.assetsmanagement.web.model.request.GetMaintenanceWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.RejectMaintenanceWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.UpdateMaintenanceWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.generic.FilterAndPageRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.sort.GetMaintenanceSortWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetMaintenanceDetailWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetMaintenanceHistoryWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetMaintenanceWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.UpdateMaintenanceWebResponse;
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
@MandatoryParameterAtQuery
@RequestMapping(value = AssetsManagementApiPath.MAINTENANCE_BASE_PATH)
public class MaintenanceController {

   @Autowired
   private CommandExecutor commandExecutor;

   @Autowired
   private SchedulerHelper schedulerHelper;

   @RequestMapping(value = "/_update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<UpdateMaintenanceWebResponse>> updateMaintenance(MandatoryParameter mandatoryParameter,
                                                                         @RequestBody UpdateMaintenanceWebRequest request){
      return commandExecutor.execute(UpdateMaintenanceCommand.class,toUpdateMaintenanceCommandRequest(request,mandatoryParameter.getUsername()))
            .map(ResponseHelper::ok)
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private UpdateMaintenanceCommandRequest toUpdateMaintenanceCommandRequest(UpdateMaintenanceWebRequest request, String username){
      UpdateMaintenanceCommandRequest commandRequest = UpdateMaintenanceCommandRequest.builder().build();
      BeanUtils.copyProperties(request,commandRequest);
      commandRequest.setUsername(username);
      return commandRequest;
   }

   @RequestMapping(value = "/_reject", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<Boolean>> rejectMaintenance(MandatoryParameter mandatoryParameter,
                                                    @RequestBody RejectMaintenanceWebRequest request){
      return commandExecutor.execute(RejectMaintenanceCommand.class,toRejectMaintenanceCommandRequest(request, mandatoryParameter.getUsername()))
            .map(ResponseHelper::ok)
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private RejectMaintenanceCommandRequest toRejectMaintenanceCommandRequest(RejectMaintenanceWebRequest request, String username){
      RejectMaintenanceCommandRequest commandRequest = RejectMaintenanceCommandRequest.builder().build();
      BeanUtils.copyProperties(request,commandRequest);
      commandRequest.setUsername(username);
      return commandRequest;
   }

   @SneakyThrows
   @RequestMapping(value = "/_get-detail/{maintenanceNumber}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<GetMaintenanceDetailWebResponse>> getDetailMaintenance(MandatoryParameter mandatoryParameter,
                                                                               @PathVariable("maintenanceNumber") String maintenanceNumber){
      maintenanceNumber = URLDecoder.decode(maintenanceNumber, StandardCharsets.UTF_8.toString());
      return commandExecutor.execute(GetMaintenanceDetailCommand.class,toGetMaintenanceDetailCommandRequest(maintenanceNumber))
            .map(ResponseHelper::ok)
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private GetMaintenanceDetailCommandRequest toGetMaintenanceDetailCommandRequest(String maintenanceNumber){
      return GetMaintenanceDetailCommandRequest.builder().maintenanceNumber(maintenanceNumber).build();
   }

   @SneakyThrows
   @RequestMapping(value = "/_get-all-history/{maintenanceNumber}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<List<GetMaintenanceHistoryWebResponse>>> getMaintenanceHistory(MandatoryParameter mandatoryParameter,
                                                                                       @PathVariable("maintenanceNumber") String maintenanceNumber){
      maintenanceNumber = URLDecoder.decode(maintenanceNumber, StandardCharsets.UTF_8.toString());
      return commandExecutor.execute(GetMaintenanceHistoryCommand.class,toGetMaintenanceHistoryCommandRequest(maintenanceNumber))
            .map(ResponseHelper::ok)
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private GetMaintenanceHistoryCommandRequest toGetMaintenanceHistoryCommandRequest(String maintenanceNumber){
      return GetMaintenanceHistoryCommandRequest.builder().maintenanceNumber(maintenanceNumber).build();
   }

   @RequestMapping(value = "/_get-all", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<List<GetMaintenanceWebResponse>>> getMaintenances(MandatoryParameter mandatoryParameter,
                                                                          @RequestBody FilterAndPageRequest<GetMaintenanceWebRequest, GetMaintenanceSortWebRequest> request){
      return commandExecutor.execute(GetMaintenanceCommand.class,toGetMaintenanceCommandRequest(request))
            .map(maintenancePair -> ResponseHelper.ok(maintenancePair.getLeft(),maintenancePair.getRight()))
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private GetMaintenanceCommandRequest toGetMaintenanceCommandRequest(FilterAndPageRequest<GetMaintenanceWebRequest, GetMaintenanceSortWebRequest> request){
      return GetMaintenanceCommandRequest.builder()
            .maintenanceNumberFilter(request.getFilters().getMaintenanceNumber())
            .assetNumberFilter(request.getFilters().getAssetNumber())
            .requesterFilter(request.getFilters().getRequester())
            .itemCodeFilter(request.getFilters().getItemCode())
            .locationFilter(request.getFilters().getLocation())
            .statusFilter(request.getFilters().getStatus())
            .tanggalLaporanStartFilter(request.getFilters().getTanggalLaporanStart())
            .tanggalLaporanEndFilter(request.getFilters().getTanggalLaporanEnd())
            .limit(request.getItemPerPage())
            .page(request.getPage())
            .sortBy(getSortByFromRequest(request.getSorts())).build();
   }

   private List<SortBy> getSortByFromRequest(GetMaintenanceSortWebRequest sorts){
      List<SortBy> sortByList= new ArrayList<>();
      if(StringUtils.isNotEmpty(sorts.getMaintenanceNumber())){
         SortBy maintenanceNumberSort = SortBy.builder()
               .direction(SortByDirection.valueOf(SortDirectionHelper.getSortDirection(sorts.getMaintenanceNumber().toUpperCase())))
               .propertyName("maintenanceNumber").build();
         sortByList.add(maintenanceNumberSort);
      }
      if(StringUtils.isNotEmpty(sorts.getTanggalLaporan())){
         SortBy tanggalLaporanSort = SortBy.builder()
               .direction(SortByDirection.valueOf(SortDirectionHelper.getSortDirection(sorts.getTanggalLaporan().toUpperCase())))
               .propertyName("tanggalLaporan").build();
         sortByList.add(tanggalLaporanSort);
      }
      return sortByList;
   }
}
