package com.gdn.warehouse.assetsmanagement.web;

import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.common.helper.ResponseHelper;
import com.blibli.oss.backend.common.model.response.Response;
import com.blibli.oss.backend.mandatoryparameter.model.MandatoryParameter;
import com.blibli.oss.backend.mandatoryparameter.swagger.annotation.MandatoryParameterAtQuery;
import com.blibli.oss.backend.reactor.scheduler.SchedulerHelper;
import com.gdn.warehouse.assetsmanagement.command.GetAllWarehouseCommand;
import com.gdn.warehouse.assetsmanagement.command.model.GetAllWarehouseCommandRequest;
import com.gdn.warehouse.assetsmanagement.properties.AssetsManagementSchedulerProperties;
import com.gdn.warehouse.assetsmanagement.web.model.AssetsManagementApiPath;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetWarehouseWebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(value = AssetsManagementApiPath.WAREHOUSE_BASE_PATH)
@MandatoryParameterAtQuery
public class WarehouseController {
   @Autowired
   private CommandExecutor commandExecutor;

   @Autowired
   private SchedulerHelper schedulerHelper;

   @RequestMapping(value = "/_get-all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
   private Mono<Response<List<GetWarehouseWebResponse>>> getAllWarehouse(MandatoryParameter mandatoryParameter){
      return commandExecutor.execute(GetAllWarehouseCommand.class,toGetAllWarehouseCommandRequest())
            .map(dataPagingPair -> ResponseHelper.ok(dataPagingPair.getLeft(),dataPagingPair.getRight()))
            .subscribeOn(schedulerHelper.of(AssetsManagementSchedulerProperties.SCHEDULER_NAME));
   }

   private GetAllWarehouseCommandRequest toGetAllWarehouseCommandRequest(){
      return GetAllWarehouseCommandRequest.builder()
            .limit(null)
            .page(null)
            .sortBy("warehouseName")
            .sortOrder("asc").build();
   }


}
