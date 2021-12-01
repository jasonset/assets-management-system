package com.gdn.warehouse.assetsmanagement.client;

import com.gdn.common.client.GdnRestClientConfiguration;
import com.gdn.common.util.GdnHttpClientHelper;
import com.gdn.common.web.param.MandatoryRequestParam;
import com.gdn.warehouse.assetsmanagement.properties.TestConstants;
import com.gdn.warehouse.assetsmanagement.web.model.AssetsManagementApiPath;
import com.gdn.warehouse.assetsmanagement.web.model.request.ApproveMaintenanceRequestWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.ApproveTransferAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateItemWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateMaintenanceReminderWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateMaintenanceRequestWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateTransferAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.DeliveredTransferAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.OnDeliveryTransferAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.RejectMaintenanceWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.UpdateAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.UpdateItemWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.UpdateMaintenanceReminderWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.UpdateMaintenanceWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.generic.FilterAndPageRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.verify;

public class AssetsManagementClientTest {
   private static final String USERNAME = "username";
   private static final String PASSWORD = "password";
   private static final int PORT = 8080;
   private static final String HOST = "localhost";
   private static final String CONTEXT_PATH = "assets-management";
   private static final String CHANNEL_ID = "channel-id";
   private static final String STORE_ID = "store-id";
   private static final int CONNECTION_TIMEOUT_IN_MS =100 ;
   private static final String REQUEST_ID = "request-id" ;
   private static final String CLIENT_ID = "client-id";

   @InjectMocks
   private AssetsManagementClient assetsManagementClient;

   @Mock
   private GdnHttpClientHelper gdnHttpClientHelper;

   private MandatoryRequestParam mandatoryRequestParam;

   @Before
   public void setUp() throws Exception {
      initMocks(this);
      GdnRestClientConfiguration clientConfig =
            new GdnRestClientConfiguration(USERNAME, PASSWORD, HOST, PORT, CLIENT_ID, CHANNEL_ID, STORE_ID);
      clientConfig.setConnectionTimeoutInMs(CONNECTION_TIMEOUT_IN_MS);

      this.assetsManagementClient = new AssetsManagementClient(clientConfig,CONTEXT_PATH);
      ReflectionTestUtils.setField(this.assetsManagementClient,"httpClientHelper", this.gdnHttpClientHelper,
            GdnHttpClientHelper.class);
      this.mandatoryRequestParam = MandatoryRequestParam.generateMandatoryRequestParam(STORE_ID, CHANNEL_ID,
            CLIENT_ID, REQUEST_ID);
      this.mandatoryRequestParam.setAuthenticator(USERNAME);
      this.mandatoryRequestParam.setUsername(USERNAME);
   }

   @Test
   public void createAsset() throws Exception {
      CreateAssetWebRequest request = CreateAssetWebRequest.builder().build();
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.ASSET_BASE_PATH+"/_create";
      assetsManagementClient.createAsset(REQUEST_ID,USERNAME,request);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void createAsset_requestId_null() throws Exception {
      CreateAssetWebRequest request = CreateAssetWebRequest.builder().build();
      String uri = AssetsManagementApiPath.ASSET_BASE_PATH+"/_create";
      assetsManagementClient.createAsset(null,USERNAME,request);
      // Can not verify as request id will be random. This is only for test coverage.
   }

   @Test
   public void updateAsset() throws Exception {
      UpdateAssetWebRequest request = UpdateAssetWebRequest.builder().build();
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.ASSET_BASE_PATH+"/_update";
      assetsManagementClient.updateAsset(REQUEST_ID,USERNAME,request);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void getDetailAsset() throws Exception {
      String assetNumber = TestConstants.ASSET_NUMBER;
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.ASSET_BASE_PATH+"/_get-detail/"+assetNumber;
      assetsManagementClient.getDetailAsset(REQUEST_ID,USERNAME,assetNumber);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void getAllAsset() throws Exception {
      FilterAndPageRequest request = FilterAndPageRequest.builder().build();
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.ASSET_BASE_PATH+"/_get-all";
      assetsManagementClient.getAllAsset(REQUEST_ID,USERNAME,request);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void createTransferAsset() throws Exception {
      CreateTransferAssetWebRequest request = CreateTransferAssetWebRequest.builder().build();
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_create";
      assetsManagementClient.createTransferAsset(REQUEST_ID,USERNAME,request);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void approveTransferAsset() throws Exception {
      ApproveTransferAssetWebRequest request = ApproveTransferAssetWebRequest.builder().build();
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_approve";
      assetsManagementClient.approveTransferAsset(REQUEST_ID,USERNAME,request);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void onDeliveryTransferAsset() throws Exception {
      OnDeliveryTransferAssetWebRequest request = OnDeliveryTransferAssetWebRequest.builder().build();
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_on-delivery";
      assetsManagementClient.onDeliveryTransferAsset(REQUEST_ID,USERNAME,request);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void deliveredTransferAsset() throws Exception {
      DeliveredTransferAssetWebRequest request = DeliveredTransferAssetWebRequest.builder().build();
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_delivered";
      assetsManagementClient.deliveredTransferAsset(REQUEST_ID,USERNAME,request);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void getDetailTransferAsset() throws Exception {
      String transferAssetNumber = TestConstants.TRANSFER_ASSET_NUMBER;
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_get-detail/"+transferAssetNumber;
      assetsManagementClient.getDetailTransferAsset(REQUEST_ID,USERNAME,transferAssetNumber);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void getTransferAssetHistory() throws Exception {
      String transferAssetNumber = TestConstants.TRANSFER_ASSET_NUMBER;
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_get-all-history/"+transferAssetNumber;
      assetsManagementClient.getAllHistoryTransferAsset(REQUEST_ID,USERNAME,transferAssetNumber);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void getAllTransferAsset() throws Exception {
      FilterAndPageRequest request = FilterAndPageRequest.builder().build();
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_get-all";
      assetsManagementClient.getAllTransferAsset(REQUEST_ID,USERNAME,request);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void createMaintenanceRequest() throws Exception {
      CreateMaintenanceRequestWebRequest request = CreateMaintenanceRequestWebRequest.builder().build();
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.MAINTENANCE_REQUEST_BASE_PATH+"/_create";
      assetsManagementClient.createMaintenanceRequest(REQUEST_ID,USERNAME,request);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void approveMaintenanceRequest() throws Exception {
      ApproveMaintenanceRequestWebRequest request = ApproveMaintenanceRequestWebRequest.builder().build();
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.MAINTENANCE_REQUEST_BASE_PATH+"/_approve";
      assetsManagementClient.approveMaintenanceRequest(REQUEST_ID,USERNAME,request);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void updateMaintenance() throws Exception {
      UpdateMaintenanceWebRequest request = UpdateMaintenanceWebRequest.builder().build();
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.MAINTENANCE_BASE_PATH+"/_update";
      assetsManagementClient.updateMaintenance(REQUEST_ID,USERNAME,request);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void rejectMaintenance() throws Exception {
      RejectMaintenanceWebRequest request = RejectMaintenanceWebRequest.builder().build();
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.MAINTENANCE_BASE_PATH+"/_reject";
      assetsManagementClient.rejectMaintenance(REQUEST_ID,USERNAME,request);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void getDetailMaintenance() throws Exception {
      String maintenanceNumber = TestConstants.MAINTENANCE_NUMBER;
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.MAINTENANCE_BASE_PATH+"/_get-detail/"+maintenanceNumber;
      assetsManagementClient.getDetailMaintenance(REQUEST_ID,USERNAME,maintenanceNumber);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void getAllHistoryMaintenance() throws Exception {
      String maintenanceNumber = TestConstants.MAINTENANCE_NUMBER;
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.MAINTENANCE_BASE_PATH+"/_get-all-history/"+maintenanceNumber;
      assetsManagementClient.getAllHistoryMaintenance(REQUEST_ID,USERNAME,maintenanceNumber);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void getAllMaintenance() throws Exception {
      FilterAndPageRequest request = FilterAndPageRequest.builder().build();
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.MAINTENANCE_BASE_PATH+"/_get-all";
      assetsManagementClient.getAllMaintenance(REQUEST_ID,USERNAME,request);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void createMaintenanceReminder() throws Exception {
      CreateMaintenanceReminderWebRequest request = CreateMaintenanceReminderWebRequest.builder().build();
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.MAINTENANCE_REMINDER_BASE_PATH+"/_create";
      assetsManagementClient.createMaintenanceReminder(REQUEST_ID,USERNAME,request);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void updateMaintenanceReminder() throws Exception {
      UpdateMaintenanceReminderWebRequest request = UpdateMaintenanceReminderWebRequest.builder().build();
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.MAINTENANCE_REMINDER_BASE_PATH+"/_update";
      assetsManagementClient.updateMaintenanceReminder(REQUEST_ID,USERNAME,request);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void cancelMaintenanceReminder() throws Exception {
      String maintenanceReminderNumber = TestConstants.MAINTENANCE_REMINDER_NUMBER;
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.MAINTENANCE_REMINDER_BASE_PATH+"/_cancel/"+maintenanceReminderNumber;
      assetsManagementClient.cancelMaintenanceReminder(REQUEST_ID,USERNAME,maintenanceReminderNumber);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void getAllMaintenanceReminder() throws Exception {
      FilterAndPageRequest request = FilterAndPageRequest.builder().build();
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.MAINTENANCE_REMINDER_BASE_PATH+"/_get-all";
      assetsManagementClient.getAllMaintenanceReminder(REQUEST_ID,USERNAME,request);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void getAllWarehouse() throws Exception {
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.WAREHOUSE_BASE_PATH+"/_get-all";
      assetsManagementClient.getAllWarehouse(REQUEST_ID,USERNAME);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void getAllItem() throws Exception {
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.ITEM_BASE_PATH+"/_get-all";
      assetsManagementClient.getAllItem(REQUEST_ID,USERNAME);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void getAllItemFilter() throws Exception {
      FilterAndPageRequest request = FilterAndPageRequest.builder().build();
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.ITEM_BASE_PATH+"/_get-all-item";
      assetsManagementClient.getAllItemWithFilter(REQUEST_ID,USERNAME,request);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void createItem() throws Exception {
      CreateItemWebRequest request = CreateItemWebRequest.builder().build();
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.ITEM_BASE_PATH+"/_create";
      assetsManagementClient.createItem(REQUEST_ID,USERNAME,request);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }

   @Test
   public void updateItem() throws Exception {
      UpdateItemWebRequest request = UpdateItemWebRequest.builder().build();
      Map<String,String> additionalParameterMap = new HashMap<>();
      String uri = AssetsManagementApiPath.ITEM_BASE_PATH+"/_update";
      assetsManagementClient.updateItem(REQUEST_ID,USERNAME,request);
      verify(this.gdnHttpClientHelper).getURI(HOST,PORT,"/"+CONTEXT_PATH+uri,mandatoryRequestParam,
            additionalParameterMap);
   }
}