package com.gdn.warehouse.assetsmanagement.web;


import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.common.model.response.Paging;
import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdn.warehouse.assetsmanagement.command.ApproveTransferAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.CreateTransferAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.DeliveredTransferAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.GetTransferAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.GetTransferAssetDetailCommand;
import com.gdn.warehouse.assetsmanagement.command.GetTransferAssetHistoryCommand;
import com.gdn.warehouse.assetsmanagement.command.OnDeliveryTransferAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.model.ApproveTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.CreateTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.DeliveredTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.GetTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.GetTransferAssetDetailCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.GetTransferAssetHistoryCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.OnDeliveryTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetType;
import com.gdn.warehouse.assetsmanagement.web.model.AssetsManagementApiPath;
import com.gdn.warehouse.assetsmanagement.web.model.request.ApproveTransferAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateTransferAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.DeliveredTransferAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.GetTransferAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.OnDeliveryTransferAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.generic.FilterAndPageRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.sort.GetTransferAssetSortWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetTransferAssetDetailWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetTransferAssetHistoryWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetTransferAssetWebResponse;
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
public class TransferAssetControllerTest {
   @MockBean
   private CommandExecutor commandExecutor;

   @Autowired
   MandatoryParameterProperties mandatoryParameter;

   @Autowired
   private ObjectMapper objectMapper;


   @Value("${local.server.port}")
   private int port;

   private CreateTransferAssetWebRequest createTransferAssetWebRequest;
   private ApproveTransferAssetWebRequest approveTransferAssetWebRequest;
   private DeliveredTransferAssetWebRequest deliveredTransferAssetWebRequest;
   private OnDeliveryTransferAssetWebRequest onDeliveryTransferAssetWebRequest;
   private GetTransferAssetDetailWebResponse getTransferAssetDetailWebResponse;
   private GetTransferAssetHistoryWebResponse getTransferAssetHistoryWebResponse;
   private FilterAndPageRequest<GetTransferAssetWebRequest, GetTransferAssetSortWebRequest> request;
   private TransferAsset transferAsset;
   private Paging paging;
   private Pair<List<GetTransferAssetWebResponse>,Paging> pair;
   private GetTransferAssetWebResponse getTransferAssetWebResponse;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      RestAssured.port = port;

      createTransferAssetWebRequest = CreateTransferAssetWebRequest.builder().build();
      approveTransferAssetWebRequest = ApproveTransferAssetWebRequest.builder().build();
      deliveredTransferAssetWebRequest = DeliveredTransferAssetWebRequest.builder().build();
      onDeliveryTransferAssetWebRequest = OnDeliveryTransferAssetWebRequest.builder().build();
      getTransferAssetDetailWebResponse = GetTransferAssetDetailWebResponse.builder().build();
      getTransferAssetHistoryWebResponse = GetTransferAssetHistoryWebResponse.builder().build();
      request = new FilterAndPageRequest<>(new GetTransferAssetWebRequest(),
            GetTransferAssetSortWebRequest.builder().transferAssetNumber("TA-NUMBER").build(),1,1);
      transferAsset = TransferAsset.builder()
            .transferAssetNumber("TA-NUMBER")
            .assetNumbers(Collections.singletonList("ASSET-NUMBER"))
            .arrivalDate(new Date())
            .itemCode("ITEM-CODE")
            .origin("ORIGIN")
            .destination("DESTINATION")
            .status(TransferAssetStatus.PENDING)
            .referenceNumber("REFERENCE")
            .transferAssetType(TransferAssetType.MOVE)
            .notes("NOTES").build();
      getTransferAssetWebResponse = GetTransferAssetWebResponse.builder().build();
      paging = Paging.builder().build();
      pair = Pair.of(Arrays.asList(getTransferAssetWebResponse),paging);
   }

   @Test
   public void createTransferAsset() throws Exception{
      when(commandExecutor.execute(eq(CreateTransferAssetCommand.class),any(CreateTransferAssetCommandRequest.class)))
            .thenReturn(Mono.just("TA-CODE"));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body(createTransferAssetWebRequest)
            .queryParam("requestId", 1001)
            .post(AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_create")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(CreateTransferAssetCommand.class),any(CreateTransferAssetCommandRequest.class));
   }

   @Test
   public void approveTransferAsset() throws Exception{
      when(commandExecutor.execute(eq(ApproveTransferAssetCommand.class),any(ApproveTransferAssetCommandRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body(approveTransferAssetWebRequest)
            .queryParam("requestId", 1001)
            .post(AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_approve")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(ApproveTransferAssetCommand.class),any(ApproveTransferAssetCommandRequest.class));
   }

   @Test
   public void deliveredTransferAsset() throws Exception{
      when(commandExecutor.execute(eq(DeliveredTransferAssetCommand.class),any(DeliveredTransferAssetCommandRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body(deliveredTransferAssetWebRequest)
            .queryParam("requestId", 1001)
            .post(AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_delivered")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(DeliveredTransferAssetCommand.class),any(DeliveredTransferAssetCommandRequest.class));
   }

   @Test
   public void onDeliveryTransferAsset() throws Exception{
      when(commandExecutor.execute(eq(OnDeliveryTransferAssetCommand.class),any(OnDeliveryTransferAssetCommandRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body(onDeliveryTransferAssetWebRequest)
            .queryParam("requestId", 1001)
            .post(AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_on-delivery")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(OnDeliveryTransferAssetCommand.class),any(OnDeliveryTransferAssetCommandRequest.class));
   }

   @Test
   public void getDetail() {
      when(commandExecutor.execute(eq(GetTransferAssetDetailCommand.class),any(GetTransferAssetDetailCommandRequest.class)))
            .thenReturn(Mono.just(getTransferAssetDetailWebResponse));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body("")
            .queryParam("requestId", 1001)
            .get(AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_get-detail"+"/transferAssetNumber")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(GetTransferAssetDetailCommand.class),any(GetTransferAssetDetailCommandRequest.class));
   }

   @Test
   public void getAllHistory() {
      when(commandExecutor.execute(eq(GetTransferAssetHistoryCommand.class),any(GetTransferAssetHistoryCommandRequest.class)))
            .thenReturn(Mono.just(Arrays.asList(getTransferAssetHistoryWebResponse)));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body("")
            .queryParam("requestId", 1001)
            .get(AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_get-all-history"+"/transferAssetNumber")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(GetTransferAssetHistoryCommand.class),any(GetTransferAssetHistoryCommandRequest.class));
   }

   @Test
   public void getAll() throws JsonProcessingException {
      when(commandExecutor.execute(eq(GetTransferAssetCommand.class),any(GetTransferAssetCommandRequest.class)))
            .thenReturn(Mono.just(pair));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body(objectMapper.writeValueAsString(request))
            .queryParam("requestId", 1001)
            .post(AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_get-all")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(GetTransferAssetCommand.class),any(GetTransferAssetCommandRequest.class));
   }
}