package com.gdn.warehouse.assetsmanagement.web;

import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.common.model.response.Paging;
import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdn.warehouse.assetsmanagement.command.CancelMaintenanceReminderCommand;
import com.gdn.warehouse.assetsmanagement.command.CreateMaintenanceReminderCommand;
import com.gdn.warehouse.assetsmanagement.command.GetMaintenanceReminderCommand;
import com.gdn.warehouse.assetsmanagement.command.model.CancelMaintenanceReminderCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.CreateMaintenanceReminderCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.GetMaintenanceReminderCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.MaintenanceReminder;
import com.gdn.warehouse.assetsmanagement.web.model.AssetsManagementApiPath;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateMaintenanceReminderWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.GetMaintenanceReminderWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.generic.FilterAndPageRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.sort.GetMaintenanceReminderSortWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetMaintenanceReminderWebResponse;
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
import java.util.Date;
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
public class MaintenanceReminderControllerTest {

   @MockBean
   private CommandExecutor commandExecutor;

   @Autowired
   MandatoryParameterProperties mandatoryParameter;

   @Autowired
   private ObjectMapper objectMapper;

   @Value("${local.server.port}")
   private int port;

   private CreateMaintenanceReminderWebRequest createMaintenanceReminderWebRequest;
   private FilterAndPageRequest<GetMaintenanceReminderWebRequest, GetMaintenanceReminderSortWebRequest> request;
   private MaintenanceReminder maintenanceReminder;
   private GetMaintenanceReminderWebResponse getMaintenanceReminderWebResponse;
   private Paging paging;
   private Pair<List<GetMaintenanceReminderWebResponse>,Paging> pair;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      RestAssured.port = port;

      createMaintenanceReminderWebRequest = CreateMaintenanceReminderWebRequest.builder().build();
      request = new FilterAndPageRequest<>(new GetMaintenanceReminderWebRequest(),
            GetMaintenanceReminderSortWebRequest.builder().maintenanceReminderNumber("ASC")
                  .scheduledDate("ASC").build(),1,1);
      maintenanceReminder = MaintenanceReminder.builder().maintenanceReminderNumber("NUMBER")
            .assetNumbers(Arrays.asList("NUMBER")).itemCode("CODE")
            .emailList(Arrays.asList("EMAIL")).scheduledDate(new Date()).interval(1).build();
      getMaintenanceReminderWebResponse = GetMaintenanceReminderWebResponse.builder().build();
      paging = Paging.builder().build();
      pair = Pair.of(Arrays.asList(getMaintenanceReminderWebResponse),paging);
   }

   @Test
   public void createMaintenanceReminder() {
      when(commandExecutor.execute(eq(CreateMaintenanceReminderCommand.class),any(CreateMaintenanceReminderCommandRequest.class)))
                  .thenReturn(Mono.just("MR-CODE"));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body(createMaintenanceReminderWebRequest)
            .queryParam("requestId", 1001)
            .post(AssetsManagementApiPath.MAINTENANCE_REMINDER_BASE_PATH+"/_create")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(CreateMaintenanceReminderCommand.class),any(CreateMaintenanceReminderCommandRequest.class));
   }

   @Test
   public void cancelMaintenanceReminder() {
      when(commandExecutor.execute(eq(CancelMaintenanceReminderCommand.class),any(CancelMaintenanceReminderCommandRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body("")
            .queryParam("requestId", 1001)
            .get(AssetsManagementApiPath.MAINTENANCE_REMINDER_BASE_PATH+"/_cancel"+"/maintenanceReminderNumber")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(CancelMaintenanceReminderCommand.class),any(CancelMaintenanceReminderCommandRequest.class));
   }

   @Test
   public void getAll() throws JsonProcessingException {
      when(commandExecutor.execute(eq(GetMaintenanceReminderCommand.class),any(GetMaintenanceReminderCommandRequest.class)))
            .thenReturn(Mono.just(pair));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body(objectMapper.writeValueAsString(request))
            .queryParam("requestId", 1001)
            .post(AssetsManagementApiPath.MAINTENANCE_REMINDER_BASE_PATH+"/_get-all")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(GetMaintenanceReminderCommand.class),any(GetMaintenanceReminderCommandRequest.class));
   }
}