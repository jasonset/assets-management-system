package com.gdn.warehouse.assetsmanagement.web;

import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.common.helper.ResponseHelper;
import com.blibli.oss.backend.common.model.request.SortBy;
import com.blibli.oss.backend.common.model.request.SortByDirection;
import com.blibli.oss.backend.common.model.response.Response;
import com.blibli.oss.backend.mandatoryparameter.model.MandatoryParameter;
import com.blibli.oss.backend.mandatoryparameter.swagger.annotation.MandatoryParameterAtQuery;
import com.blibli.oss.backend.reactor.scheduler.SchedulerHelper;
import com.gdn.warehouse.assetsmanagement.command.CancelMaintenanceReminderCommand;
import com.gdn.warehouse.assetsmanagement.command.CreateMaintenanceReminderCommand;
import com.gdn.warehouse.assetsmanagement.command.GetMaintenanceReminderCommand;
import com.gdn.warehouse.assetsmanagement.command.model.CancelMaintenanceReminderCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.CreateMaintenanceReminderCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.GetMaintenanceReminderCommandRequest;
import com.gdn.warehouse.assetsmanagement.helper.util.SortDirectionHelper;
import com.gdn.warehouse.assetsmanagement.properties.AssetsManagementSchedulerProperties;
import com.gdn.warehouse.assetsmanagement.web.model.AssetsManagementApiPath;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateMaintenanceReminderWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.GetMaintenanceReminderWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.generic.FilterAndPageRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.sort.GetMaintenanceReminderSortWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetMaintenanceReminderWebResponse;
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
@RequestMapping(value = AssetsManagementApiPath.MAINTENANCE_REMINDER_BASE_PATH)
public class MaintenanceReminderController {

   @Autowired
   private CommandExecutor commandExecutor;

   @Autowired
   private SchedulerHelper schedulerHelper;

   @RequestMapping(value = "/_create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<String>> createMaintenanceReminder(MandatoryParameter mandatoryParameter, @RequestBody CreateMaintenanceReminderWebRequest request){
      return commandExecutor.execute(CreateMaintenanceReminderCommand.class,toCreateMaintenanceRequestCommandRequest(request,mandatoryParameter.getUsername()))
            .map(ResponseHelper::ok)
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private CreateMaintenanceReminderCommandRequest toCreateMaintenanceRequestCommandRequest(CreateMaintenanceReminderWebRequest request,String username){
      CreateMaintenanceReminderCommandRequest commandRequest = CreateMaintenanceReminderCommandRequest.builder().build();
      BeanUtils.copyProperties(request,commandRequest);
      commandRequest.setInterval(request.getSchedule());
      commandRequest.setUsername(username);
      return commandRequest;
   }

   @SneakyThrows
   @RequestMapping(value = "/_cancel/{maintenanceReminderNumber}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<Boolean>> cancelMaintenanceReminder(MandatoryParameter mandatoryParameter, @PathVariable("maintenanceReminderNumber") String maintenanceReminderNumber){
      maintenanceReminderNumber = URLDecoder.decode(maintenanceReminderNumber, StandardCharsets.UTF_8.toString());
      return commandExecutor.execute(CancelMaintenanceReminderCommand.class,toCancelMaintenanceReminderCommandRequest(maintenanceReminderNumber,mandatoryParameter.getUsername()))
            .map(ResponseHelper::ok)
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private CancelMaintenanceReminderCommandRequest toCancelMaintenanceReminderCommandRequest(String maintenanceReminderNumber,String username){
      return CancelMaintenanceReminderCommandRequest.builder()
            .maintenanceReminderNumber(maintenanceReminderNumber).username(username).build();
   }

   @RequestMapping(value = "/_get-all", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<List<GetMaintenanceReminderWebResponse>>> getReminders(MandatoryParameter mandatoryParameter,
                                                                               @RequestBody FilterAndPageRequest<GetMaintenanceReminderWebRequest, GetMaintenanceReminderSortWebRequest> request){
      return commandExecutor.execute(GetMaintenanceReminderCommand.class,toGetMaintenanceReminderCommandRequest(request))
            .map(maintenanceReminderPair -> ResponseHelper.ok(maintenanceReminderPair.getLeft(),maintenanceReminderPair.getRight()))
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private GetMaintenanceReminderCommandRequest toGetMaintenanceReminderCommandRequest(FilterAndPageRequest<GetMaintenanceReminderWebRequest, GetMaintenanceReminderSortWebRequest> request){
      return GetMaintenanceReminderCommandRequest.builder()
            .maintenanceReminderNumberFilter(request.getFilters().getMaintenanceReminderNumber())
            .assetNumberFilter(request.getFilters().getAssetNumber())
            .itemCodeFilter(request.getFilters().getItemCode())
            .scheduledDateFilter(request.getFilters().getScheduledDate())
            .intervalFilter(request.getFilters().getInterval())
            .emailFilter(request.getFilters().getEmail())
            .limit(request.getItemPerPage())
            .page(request.getPage())
            .sortBy(getSortByFromRequest(request.getSorts())).build();
   }

   private List<SortBy> getSortByFromRequest(GetMaintenanceReminderSortWebRequest sorts){
      List<SortBy> sortByList= new ArrayList<>();
      if(StringUtils.isNotEmpty(sorts.getMaintenanceReminderNumber())){
         SortBy maintenanceNumberSort = SortBy.builder()
               .direction(SortByDirection.valueOf(SortDirectionHelper.getSortDirection(sorts.getMaintenanceReminderNumber().toUpperCase())))
               .propertyName("maintenanceNumber").build();
         sortByList.add(maintenanceNumberSort);
      }
      if(StringUtils.isNotEmpty(sorts.getScheduledDate())){
         SortBy scheduledDateSort = SortBy.builder()
               .direction(SortByDirection.valueOf(SortDirectionHelper.getSortDirection(sorts.getScheduledDate().toUpperCase())))
               .propertyName("scheduledDate").build();
         sortByList.add(scheduledDateSort);
      }
      return sortByList;
   }
}
