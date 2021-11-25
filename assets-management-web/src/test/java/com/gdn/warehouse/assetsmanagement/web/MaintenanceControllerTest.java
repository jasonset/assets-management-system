package com.gdn.warehouse.assetsmanagement.web;

import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.common.model.response.Paging;
import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.gdn.warehouse.assetsmanagement.entity.Maintenance;
import com.gdn.warehouse.assetsmanagement.enums.MaintenanceStatus;
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
import io.restassured.RestAssured;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfiguration.class)
public class MaintenanceControllerTest {

   @MockBean
   private CommandExecutor commandExecutor;

   @Autowired
   MandatoryParameterProperties mandatoryParameter;

   @Autowired
   private ObjectMapper objectMapper;

   @Value("${local.server.port}")
   private int port;

   private UpdateMaintenanceWebRequest updateMaintenanceWebRequest;
   private RejectMaintenanceWebRequest rejectMaintenanceWebRequest;
   private FilterAndPageRequest<GetMaintenanceWebRequest, GetMaintenanceSortWebRequest> request;
   private Maintenance maintenance;
   private GetMaintenanceHistoryWebResponse getMaintenanceHistoryWebResponse;
   private GetMaintenanceDetailWebResponse getMaintenanceDetailWebResponse;
   private UpdateMaintenanceWebResponse updateMaintenanceWebResponse;
   private GetMaintenanceWebResponse getMaintenanceWebResponse;
   private Paging paging;
   private Pair<List<GetMaintenanceWebResponse>,Paging> pair;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      RestAssured.port = port;

      updateMaintenanceWebRequest = UpdateMaintenanceWebRequest.builder().build();
      rejectMaintenanceWebRequest = RejectMaintenanceWebRequest.builder().build();
      request = new FilterAndPageRequest<>(new GetMaintenanceWebRequest(),
            GetMaintenanceSortWebRequest.builder().maintenanceNumber("ASC").tanggalLaporan("ASC").build(),1,1);
      maintenance = Maintenance.builder()
            .maintenanceNumber("MAINTENANCE-NUMBER").assetNumbers(Arrays.asList("ASSET-NUMBER"))
            .status(MaintenanceStatus.ON_MAINTENANCE).requester("USERNAME")
            .location("LOCATION").itemCode("ITEM-CODE").build();
      getMaintenanceHistoryWebResponse = GetMaintenanceHistoryWebResponse.builder().build();
      getMaintenanceDetailWebResponse = GetMaintenanceDetailWebResponse.builder().build();
      updateMaintenanceWebResponse = UpdateMaintenanceWebResponse.builder().build();
      getMaintenanceWebResponse = GetMaintenanceWebResponse.builder().build();
      paging = Paging.builder().build();
      pair = Pair.of(Arrays.asList(getMaintenanceWebResponse),paging);
   }

   @Test
   public void updateMaintenance() {
      when(commandExecutor.execute(eq(UpdateMaintenanceCommand.class),any(UpdateMaintenanceCommandRequest.class)))
            .thenReturn(Mono.just(updateMaintenanceWebResponse));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body(updateMaintenanceWebRequest)
            .queryParam("requestId", 1001)
            .post(AssetsManagementApiPath.MAINTENANCE_BASE_PATH+"/_update")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(UpdateMaintenanceCommand.class),any(UpdateMaintenanceCommandRequest.class));
   }

   @Test
   public void rejectMaintenance() {
      when(commandExecutor.execute(eq(RejectMaintenanceCommand.class),any(RejectMaintenanceCommandRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body(rejectMaintenanceWebRequest)
            .queryParam("requestId", 1001)
            .post(AssetsManagementApiPath.MAINTENANCE_BASE_PATH+"/_reject")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(RejectMaintenanceCommand.class),any(RejectMaintenanceCommandRequest.class));
   }

   @Test
   public void getAll() throws JsonProcessingException {
      when(commandExecutor.execute(eq(GetMaintenanceCommand.class),any(GetMaintenanceCommandRequest.class)))
            .thenReturn(Mono.just(pair));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body(objectMapper.writeValueAsString(request))
            .queryParam("requestId", 1001)
            .post(AssetsManagementApiPath.MAINTENANCE_BASE_PATH+"/_get-all")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(GetMaintenanceCommand.class),any(GetMaintenanceCommandRequest.class));
   }

   @Test
   public void getMaintenanceHistory() {
      when(commandExecutor.execute(eq(GetMaintenanceHistoryCommand.class), any(GetMaintenanceHistoryCommandRequest.class)))
            .thenReturn(Mono.just(Collections.singletonList(getMaintenanceHistoryWebResponse)));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body("")
            .queryParam("requestId", 1001)
            .get(AssetsManagementApiPath.MAINTENANCE_BASE_PATH+"/_get-all-history"+"/maintenanceNumber")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(GetMaintenanceHistoryCommand.class), any(GetMaintenanceHistoryCommandRequest.class));
   }

   @Test
   public void getDetail() {
      when(commandExecutor.execute(eq(GetMaintenanceDetailCommand.class),any(GetMaintenanceDetailCommandRequest.class)))
            .thenReturn(Mono.just(getMaintenanceDetailWebResponse));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body("")
            .queryParam("requestId", 1001)
            .get(AssetsManagementApiPath.MAINTENANCE_BASE_PATH+"/_get-detail"+"/maintenanceNumber")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(GetMaintenanceDetailCommand.class),any(GetMaintenanceDetailCommandRequest.class));
   }
}