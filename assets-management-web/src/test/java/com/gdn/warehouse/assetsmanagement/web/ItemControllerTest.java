package com.gdn.warehouse.assetsmanagement.web;

import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.common.model.response.Paging;
import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdn.warehouse.assetsmanagement.command.CreateItemCommand;
import com.gdn.warehouse.assetsmanagement.command.GetAllItemCommand;
import com.gdn.warehouse.assetsmanagement.command.GetAllItemWithFilterCommand;
import com.gdn.warehouse.assetsmanagement.command.UpdateItemCommand;
import com.gdn.warehouse.assetsmanagement.command.model.CreateItemCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.GetAllItemCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.GetAllItemWithFilterCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.UpdateItemCommandRequest;
import com.gdn.warehouse.assetsmanagement.web.model.AssetsManagementApiPath;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateItemWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.GetItemWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.UpdateItemWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.generic.FilterAndPageRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.sort.GetItemSortWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetItemWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.ItemResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfiguration.class)
public class ItemControllerTest {

   @MockBean
   private CommandExecutor commandExecutor;

   @Autowired
   MandatoryParameterProperties mandatoryParameter;

   @Autowired
   private ObjectMapper objectMapper;

   @Value("${local.server.port}")
   private int port;

   private GetItemWebResponse getItemWebResponse;
   private ItemResponse itemResponse;
   private FilterAndPageRequest<GetItemWebRequest, GetItemSortWebRequest> request;
   private CreateItemWebRequest createItemWebRequest;
   private UpdateItemWebRequest updateItemWebRequest;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      RestAssured.port = port;

      getItemWebResponse = GetItemWebResponse.builder().code("CODE").name("NAME").build();
      itemResponse = ItemResponse.builder().code("CODE").name("NAME").build();
      request = new FilterAndPageRequest<>(new GetItemWebRequest(),
            GetItemSortWebRequest.builder().code("ASC").name("DESC").build(),1,1);
      createItemWebRequest = CreateItemWebRequest.builder().build();
      updateItemWebRequest = UpdateItemWebRequest.builder().build();
   }

   @Test
   public void getAll() {
      when(commandExecutor.execute(eq(GetAllItemCommand.class),any(GetAllItemCommandRequest.class)))
            .thenReturn(Mono.just(Pair.of(Arrays.asList(getItemWebResponse), Paging.builder().build())));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .queryParam("requestId", 1001)
            .get(AssetsManagementApiPath.ITEM_BASE_PATH+"/_get-all")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(GetAllItemCommand.class),any(GetAllItemCommandRequest.class));
   }

   @Test
   public void getAllItemWithFilter() throws JsonProcessingException {
      when(commandExecutor.execute(eq(GetAllItemWithFilterCommand.class),any(GetAllItemWithFilterCommandRequest.class)))
            .thenReturn(Mono.just(Pair.of(Arrays.asList(itemResponse), Paging.builder().build())));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .queryParam("requestId", 1001)
            .body(objectMapper.writeValueAsString(request))
            .post(AssetsManagementApiPath.ITEM_BASE_PATH+"/_get-all-item")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(GetAllItemWithFilterCommand.class),any(GetAllItemWithFilterCommandRequest.class));
   }

   @Test
   public void create() {
      when(commandExecutor.execute(eq(CreateItemCommand.class),any(CreateItemCommandRequest.class)))
            .thenReturn(Mono.just("CODE"));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .queryParam("requestId", 1001)
            .body(createItemWebRequest)
            .post(AssetsManagementApiPath.ITEM_BASE_PATH+"/_create")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(CreateItemCommand.class),any(CreateItemCommandRequest.class));
   }

   @Test
   public void update() {
      when(commandExecutor.execute(eq(UpdateItemCommand.class),any(UpdateItemCommandRequest.class)))
            .thenReturn(Mono.just("CODE"));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .queryParam("requestId", 1001)
            .body(updateItemWebRequest)
            .post(AssetsManagementApiPath.ITEM_BASE_PATH+"/_update")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(UpdateItemCommand.class),any(UpdateItemCommandRequest.class));
   }
}