package com.gdn.warehouse.assetsmanagement.web;

import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.gdn.warehouse.assetsmanagement.command.ApproveMaintenanceRequestCommand;
import com.gdn.warehouse.assetsmanagement.command.CreateMaintenanceRequestCommand;
import com.gdn.warehouse.assetsmanagement.command.model.ApproveMaintenanceRequestCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.CreateMaintenanceRequestCommandRequest;
import com.gdn.warehouse.assetsmanagement.web.model.AssetsManagementApiPath;
import com.gdn.warehouse.assetsmanagement.web.model.request.ApproveMaintenanceRequestWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateMaintenanceRequestWebRequest;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfiguration.class)
public class MaintenanceRequestControllerTest {

   @MockBean
   private CommandExecutor commandExecutor;

   @Value("${local.server.port}")
   private int port;

   private CreateMaintenanceRequestWebRequest createMaintenanceRequestWebRequest;
   private ApproveMaintenanceRequestWebRequest approveMaintenanceRequestWebRequest;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      RestAssured.port = port;

      createMaintenanceRequestWebRequest = CreateMaintenanceRequestWebRequest.builder().build();
      approveMaintenanceRequestWebRequest = ApproveMaintenanceRequestWebRequest.builder().build();
   }

   @Test
   public void createMaintenanceRequest() {
      when(commandExecutor.execute(eq(CreateMaintenanceRequestCommand.class),any(CreateMaintenanceRequestCommandRequest.class)))
            .thenReturn(Mono.just("MT-CODE"));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body(createMaintenanceRequestWebRequest)
            .queryParam("requestId", 1001)
            .post(AssetsManagementApiPath.MAINTENANCE_REQUEST_BASE_PATH+"/_create")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(CreateMaintenanceRequestCommand.class),any(CreateMaintenanceRequestCommandRequest.class));
   }

   @Test
   public void approveMaintenanceRequest() {
      when(commandExecutor.execute(eq(ApproveMaintenanceRequestCommand.class),any(ApproveMaintenanceRequestCommandRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body(approveMaintenanceRequestWebRequest)
            .queryParam("requestId", 1001)
            .post(AssetsManagementApiPath.MAINTENANCE_REQUEST_BASE_PATH+"/_approve")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(ApproveMaintenanceRequestCommand.class),any(ApproveMaintenanceRequestCommandRequest.class));
   }
}