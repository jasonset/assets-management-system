package com.gdn.warehouse.assetsmanagement.web;

import com.blibli.oss.backend.command.executor.CommandExecutor;
import com.blibli.oss.backend.common.model.response.Paging;
import com.blibli.oss.backend.mandatoryparameter.swagger.properties.MandatoryParameterProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdn.warehouse.assetsmanagement.command.CreateAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.GetAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.GetAssetDetailCommand;
import com.gdn.warehouse.assetsmanagement.command.UpdateAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.model.CreateAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.GetAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.GetAssetDetailCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.UpdateAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.Organisation;
import com.gdn.warehouse.assetsmanagement.web.model.AssetsManagementApiPath;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.GetAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.UpdateAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.generic.FilterAndPageRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.sort.GetAssetSortWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetAssetDetailWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetAssetWebResponse;
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
public class AssetControllerTest {

   @MockBean
   private CommandExecutor commandExecutor;

   @Autowired
   MandatoryParameterProperties mandatoryParameter;

   @Autowired
   private ObjectMapper objectMapper;

   @Value("${local.server.port}")
   private int port;

   private CreateAssetWebRequest createAssetWebRequest;
   private UpdateAssetWebRequest updateAssetWebRequest;
   private FilterAndPageRequest<GetAssetWebRequest, GetAssetSortWebRequest> request;
   private GetAssetWebResponse getAssetWebResponse;
   private Paging paging;
   private Pair<List<GetAssetWebResponse>, Paging> pair;
   private Asset asset;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      RestAssured.port = port;

      createAssetWebRequest = CreateAssetWebRequest.builder().build();
      updateAssetWebRequest = UpdateAssetWebRequest.builder().build();
      request = new FilterAndPageRequest<>(new GetAssetWebRequest(),
            GetAssetSortWebRequest.builder().assetNumber("ASC").build(),1,1);
      asset = Asset.builder()
            .assetNumber("ASSET-NUMBER").organisation(Organisation.DJARUM).vendor("VENDOR")
            .itemCode("ITEM-CODE").location("LOCATION").status(AssetStatus.NORMAL)
            .build();
      getAssetWebResponse = GetAssetWebResponse.builder().build();
      paging = Paging.builder().build();
      pair = Pair.of(Arrays.asList(getAssetWebResponse),paging);
   }

   @Test
   public void createAsset() throws Exception{
      when(commandExecutor.execute(eq(CreateAssetCommand.class),any(CreateAssetCommandRequest.class)))
            .thenReturn(Mono.just("CODE"));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body(createAssetWebRequest)
            .queryParam("requestId", 1001)
            .post(AssetsManagementApiPath.ASSET_BASE_PATH+"/_create")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(CreateAssetCommand.class),any(CreateAssetCommandRequest.class));
   }

   @Test
   public void updateAsset() {
      when(commandExecutor.execute(eq(UpdateAssetCommand.class),any(UpdateAssetCommandRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body(updateAssetWebRequest)
            .queryParam("requestId", 1001)
            .post(AssetsManagementApiPath.ASSET_BASE_PATH+"/_update")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(UpdateAssetCommand.class),any(UpdateAssetCommandRequest.class));
   }

   @Test
   public void getAll() throws JsonProcessingException {
      when(commandExecutor.execute(eq(GetAssetCommand.class),any(GetAssetCommandRequest.class)))
            .thenReturn(Mono.just(pair));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body(objectMapper.writeValueAsString(request))
            .queryParam("requestId", 1001)
            .post(AssetsManagementApiPath.ASSET_BASE_PATH+"/_get-all")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(GetAssetCommand.class),any(GetAssetCommandRequest.class));
   }

   @Test
   public void getDetail() {
      when(commandExecutor.execute(eq(GetAssetDetailCommand.class),any(GetAssetDetailCommandRequest.class)))
            .thenReturn(Mono.just(GetAssetDetailWebResponse.builder().build()));

      given().header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .body("")
            .queryParam("requestId", 1001)
            .get(AssetsManagementApiPath.ASSET_BASE_PATH+"/_get-detail"+"/assetNumber")
            .then().body("status",equalTo(HttpStatus.OK.name()))
            .statusCode(HttpStatus.OK.value());

      verify(commandExecutor).execute(eq(GetAssetDetailCommand.class),any(GetAssetDetailCommandRequest.class));
   }
}