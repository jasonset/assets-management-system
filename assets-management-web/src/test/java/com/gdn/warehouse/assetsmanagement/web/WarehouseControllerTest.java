package com.gdn.warehouse.assetsmanagement.web;

import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.common.model.response.Paging;
import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdn.warehouse.assetsmanagement.command.GetAllWarehouseCommand;
import com.gdn.warehouse.assetsmanagement.command.model.GetAllWarehouseCommandRequest;
import com.gdn.warehouse.assetsmanagement.web.model.AssetsManagementApiPath;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetWarehouseWebResponse;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfiguration.class)
public class WarehouseControllerTest {

   @MockBean
   private CommandExecutor commandExecutor;

   @Autowired
   MandatoryParameterProperties mandatoryParameter;

   @Autowired
   private ObjectMapper objectMapper;

   @Value("${local.server.port}")
   private int port;

   private GetWarehouseWebResponse getWarehouseWebResponse;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      RestAssured.port = port;

      getWarehouseWebResponse = GetWarehouseWebResponse.builder().code("CODE").name("NAME").build();
   }

   @Test
   public void getAll() {
      when(commandExecutor.execute(eq(GetAllWarehouseCommand.class),any(GetAllWarehouseCommandRequest.class)))
            .thenReturn(Mono.just(Pair.of(Arrays.asList(getWarehouseWebResponse), Paging.builder().build())));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .queryParam("requestId", 1001)
            .get(AssetsManagementApiPath.WAREHOUSE_BASE_PATH+"/_get-all")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(GetAllWarehouseCommand.class),any(GetAllWarehouseCommandRequest.class));
   }
}