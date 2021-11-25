package com.gdn.warehouse.assetsmanagement.web;

import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.common.helper.ResponseHelper;
import com.blibli.oss.backend.common.model.response.Response;
import com.blibli.oss.backend.mandatoryparameter.model.MandatoryParameter;
import com.blibli.oss.backend.mandatoryparameter.swagger.annotation.MandatoryParameterAtQuery;
import com.blibli.oss.backend.reactor.scheduler.SchedulerHelper;
import com.gdn.warehouse.assetsmanagement.command.ApproveMaintenanceRequestCommand;
import com.gdn.warehouse.assetsmanagement.command.CreateMaintenanceRequestCommand;
import com.gdn.warehouse.assetsmanagement.command.model.ApproveMaintenanceRequestCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.CreateMaintenanceRequestCommandRequest;
import com.gdn.warehouse.assetsmanagement.properties.AssetsManagementSchedulerProperties;
import com.gdn.warehouse.assetsmanagement.web.model.AssetsManagementApiPath;
import com.gdn.warehouse.assetsmanagement.web.model.request.ApproveMaintenanceRequestWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateMaintenanceRequestWebRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Date;

@RestController
@MandatoryParameterAtQuery
@RequestMapping(value = AssetsManagementApiPath.MAINTENANCE_REQUEST_BASE_PATH)
public class MaintenanceRequestController {

   @Autowired
   private CommandExecutor commandExecutor;

   @Autowired
   private SchedulerHelper schedulerHelper;

   @RequestMapping(value = "/_create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<String>> createMaintenanceRequest(MandatoryParameter mandatoryParameter,
                                                          @RequestBody CreateMaintenanceRequestWebRequest request){
      return commandExecutor.execute(CreateMaintenanceRequestCommand.class,toCreateTransferAssetCommandRequest(request,mandatoryParameter.getUsername()))
            .map(ResponseHelper::ok)
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private CreateMaintenanceRequestCommandRequest toCreateTransferAssetCommandRequest(CreateMaintenanceRequestWebRequest request, String username){
      return CreateMaintenanceRequestCommandRequest.builder()
            .assetNumbers(request.getAssetNumbers())
            .requester(request.getRequester())
            .requesterEmail(request.getRequesterEmail())
            .tanggalKerusakan(new Date(request.getTanggalKerusakan()))
            .tanggalLaporan(new Date())
            .deskripsiKerusakan(request.getDeskripsiKerusakan())
            .username(username).build();
   }

   @RequestMapping(value = "/_approve", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   public Mono<Response<Boolean>> approveMaintenanceRequest(MandatoryParameter mandatoryParameter,
                                                            @RequestBody ApproveMaintenanceRequestWebRequest request){
      return commandExecutor.execute(ApproveMaintenanceRequestCommand.class,toApproveMaintenanceRequestCommandRequest(request,mandatoryParameter.getUsername()))
            .map(ResponseHelper::ok)
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private ApproveMaintenanceRequestCommandRequest toApproveMaintenanceRequestCommandRequest(ApproveMaintenanceRequestWebRequest request,
                                                                                             String username){
      ApproveMaintenanceRequestCommandRequest commandRequest = ApproveMaintenanceRequestCommandRequest.builder().build();
      BeanUtils.copyProperties(request,commandRequest);
      commandRequest.setUsername(username);
      return commandRequest;
   }
}
