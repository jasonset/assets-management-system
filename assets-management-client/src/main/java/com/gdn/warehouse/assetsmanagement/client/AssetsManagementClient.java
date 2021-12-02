package com.gdn.warehouse.assetsmanagement.client;

import com.blibli.oss.backend.common.model.response.Response;
import com.fasterxml.jackson.core.type.TypeReference;
import com.gdn.common.client.GdnRestClientConfiguration;
import com.gdn.common.util.GdnUUIDHelper;
import com.gdn.common.web.client.GdnBaseRestCrudClient;
import com.gdn.warehouse.assetsmanagement.web.model.AssetsManagementApiPath;
import com.gdn.warehouse.assetsmanagement.web.model.request.ApproveMaintenanceRequestWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.ApproveTransferAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateItemWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateMaintenanceReminderWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateMaintenanceRequestWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.CreateTransferAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.DeliveredTransferAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.GetAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.GetItemWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.GetMaintenanceReminderWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.GetMaintenanceWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.GetTransferAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.OnDeliveryTransferAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.RejectMaintenanceWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.UpdateAssetWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.UpdateItemWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.UpdateMaintenanceReminderWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.UpdateMaintenanceWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.generic.FilterAndPageRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.sort.GetAssetSortWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.sort.GetItemSortWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.sort.GetMaintenanceReminderSortWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.sort.GetMaintenanceSortWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.request.sort.GetTransferAssetSortWebRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetAssetDetailWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetAssetWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetItemWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetMaintenanceDetailWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetMaintenanceHistoryWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetMaintenanceReminderWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetMaintenanceWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetTransferAssetDetailWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetTransferAssetHistoryWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetTransferAssetWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetWarehouseWebResponse;
import com.gdn.warehouse.assetsmanagement.web.model.response.UpdateMaintenanceWebResponse;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetsManagementClient extends GdnBaseRestCrudClient {
   private static final String JSON_TYPE = "application/json";

   public AssetsManagementClient(GdnRestClientConfiguration clientConfig, String contextPath){
      super(clientConfig);
      this.setContextPath(contextPath);
   }

   public AssetsManagementClient(String username, String password, String host, Integer port,
                            String clientId, String channelId, String storeId, String contextPath) {
      super(username, password, host, port, clientId, channelId, storeId, contextPath);
   }

   private URI generateURI(String path, String requestId,
                           Map<String, String> additionalParameterMap, String username)
         throws Exception {
      String location = this.getContextPath() + path;
      return this.getHttpClientHelper().getURI(
            this.getClientConfig().getHost(),
            this.getClientConfig().getPort(),
            location,
            this.getMandatoryParameter(
                  this.getDefaultRequestIdValue(requestId), username),
            additionalParameterMap);
   }

   private String getDefaultRequestIdValue(String requestId) {
      if ((requestId == null) || (requestId.trim().length() == 0)) {
         return GdnUUIDHelper.generateUUID();
      }
      return requestId;
   }

   public Response<String> createAsset(String requestId, String username, CreateAssetWebRequest request) throws Exception{
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.ASSET_BASE_PATH+"/_create", requestId,
            additionalParameterMap,username);
      return this.invokePostType(uri, request, CreateAssetWebRequest.class, JSON_TYPE,
            new TypeReference<Response<String>>() {});
   }

   public Response<Boolean> updateAsset(String requestId, String username, UpdateAssetWebRequest request) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.ASSET_BASE_PATH+"/_update", requestId,
            additionalParameterMap,username);
      return this.invokePostType(uri, request, UpdateAssetWebRequest.class, JSON_TYPE,
            new TypeReference<Response<Boolean>>() {});
   }

   public Response<GetAssetDetailWebResponse> getDetailAsset(String requestId, String username, String assetNumber) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.ASSET_BASE_PATH+"/_get-detail/"+assetNumber, requestId,
            additionalParameterMap,username);
      return this.invokeGetType(uri, new TypeReference<Response<GetAssetDetailWebResponse>>() {});
   }

   public Response<List<GetAssetWebResponse>> getAllAsset(String requestId, String username,
                                                          FilterAndPageRequest<GetAssetWebRequest, GetAssetSortWebRequest> request) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.ASSET_BASE_PATH+"/_get-all",requestId,
            additionalParameterMap, username);
      return this.invokePostType(uri, request, FilterAndPageRequest.class, JSON_TYPE,
            new TypeReference<Response<List<GetAssetWebResponse>>>() {});
   }

   public Response<String> createTransferAsset(String requestId, String username,
                                               CreateTransferAssetWebRequest request) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_create",requestId,
            additionalParameterMap,username);
      return this.invokePostType(uri, request, CreateTransferAssetWebRequest.class, JSON_TYPE,
            new TypeReference<Response<String>>() {});
   }

   public Response<Boolean> approveTransferAsset(String requestId, String username,
                                                 ApproveTransferAssetWebRequest request) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_approve",requestId,
            additionalParameterMap,username);
      return this.invokePostType(uri, request, ApproveTransferAssetWebRequest.class, JSON_TYPE,
            new TypeReference<Response<Boolean>>() {});
   }

   public Response<Boolean> onDeliveryTransferAsset(String requestId, String username, OnDeliveryTransferAssetWebRequest request) throws Exception{
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_on-delivery",requestId,
            additionalParameterMap,username);
      return this.invokePostType(uri, request, OnDeliveryTransferAssetWebRequest.class, JSON_TYPE,
            new TypeReference<Response<Boolean>>() {});
   }

   public Response<Boolean> deliveredTransferAsset(String requestId, String username,
                                                 DeliveredTransferAssetWebRequest request) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_delivered",requestId,
            additionalParameterMap,username);
      return this.invokePostType(uri, request, DeliveredTransferAssetWebRequest.class, JSON_TYPE,
            new TypeReference<Response<Boolean>>() {});
   }

   public Response<GetTransferAssetDetailWebResponse> getDetailTransferAsset(String requestId, String username, String transferAssetNumber) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_get-detail/"+transferAssetNumber, requestId,
            additionalParameterMap,username);
      return this.invokeGetType(uri, new TypeReference<Response<GetTransferAssetDetailWebResponse>>() {});
   }

   public Response<List<GetTransferAssetHistoryWebResponse>> getAllHistoryTransferAsset(String requestId, String username, String transferAssetNumber) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_get-all-history/"+transferAssetNumber, requestId,
            additionalParameterMap,username);
      return this.invokeGetType(uri, new TypeReference<Response<List<GetTransferAssetHistoryWebResponse>>>() {});
   }

   public Response<List<GetTransferAssetWebResponse>> getAllTransferAsset(String requestId, String username,
                                                                          FilterAndPageRequest<GetTransferAssetWebRequest, GetTransferAssetSortWebRequest> request) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.TRANSFER_ASSET_BASE_PATH+"/_get-all",requestId,
            additionalParameterMap, username);
      return this.invokePostType(uri, request, FilterAndPageRequest.class, JSON_TYPE,
            new TypeReference<Response<List<GetTransferAssetWebResponse>>>() {});
   }

   public Response<String> createMaintenanceRequest(String requestId, String username,
                                                    CreateMaintenanceRequestWebRequest request) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.MAINTENANCE_REQUEST_BASE_PATH+"/_create",requestId,
            additionalParameterMap,username);
      return this.invokePostType(uri, request, CreateMaintenanceRequestWebRequest.class, JSON_TYPE,
            new TypeReference<Response<String>>() {});
   }

   public Response<Boolean> approveMaintenanceRequest(String requestId, String username,
                                              ApproveMaintenanceRequestWebRequest request) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.MAINTENANCE_REQUEST_BASE_PATH+"/_approve",requestId,
            additionalParameterMap,username);
      return this.invokePostType(uri, request, ApproveMaintenanceRequestWebRequest.class, JSON_TYPE,
            new TypeReference<Response<Boolean>>() {});
   }

   public Response<UpdateMaintenanceWebResponse> updateMaintenance(String requestId, String username,
                                                                   UpdateMaintenanceWebRequest request) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.MAINTENANCE_BASE_PATH+"/_update",requestId,
            additionalParameterMap,username);
      return this.invokePostType(uri, request, UpdateMaintenanceWebRequest.class, JSON_TYPE,
            new TypeReference<Response<UpdateMaintenanceWebResponse>>() {});
   }

   public Response<Boolean> rejectMaintenance(String requestId, String username,
                                              RejectMaintenanceWebRequest request) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.MAINTENANCE_BASE_PATH+"/_reject",requestId,
            additionalParameterMap,username);
      return this.invokePostType(uri, request, RejectMaintenanceWebRequest.class, JSON_TYPE,
            new TypeReference<Response<Boolean>>() {});
   }

   public Response<GetMaintenanceDetailWebResponse> getDetailMaintenance(String requestId, String username, String maintenanceNumber) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.MAINTENANCE_BASE_PATH+"/_get-detail/"+maintenanceNumber, requestId,
            additionalParameterMap,username);
      return this.invokeGetType(uri, new TypeReference<Response<GetMaintenanceDetailWebResponse>>() {});
   }

   public Response<List<GetMaintenanceHistoryWebResponse>> getAllHistoryMaintenance(String requestId, String username, String maintenanceNumber) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.MAINTENANCE_BASE_PATH+"/_get-all-history/"+maintenanceNumber, requestId,
            additionalParameterMap,username);
      return this.invokeGetType(uri, new TypeReference<Response<List<GetMaintenanceHistoryWebResponse>>>() {});
   }

   public Response<List<GetMaintenanceWebResponse>> getAllMaintenance(String requestId, String username,
                                                                      FilterAndPageRequest<GetMaintenanceWebRequest, GetMaintenanceSortWebRequest> request) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.MAINTENANCE_BASE_PATH+"/_get-all",requestId,
            additionalParameterMap, username);
      return this.invokePostType(uri, request, FilterAndPageRequest.class, JSON_TYPE,
            new TypeReference<Response<List<GetMaintenanceWebResponse>>>() {});
   }

   public Response<String> createMaintenanceReminder(String requestId, String username,
                                                     CreateMaintenanceReminderWebRequest request) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.MAINTENANCE_REMINDER_BASE_PATH+"/_create",requestId,
            additionalParameterMap,username);
      return this.invokePostType(uri, request, CreateMaintenanceReminderWebRequest.class, JSON_TYPE,
            new TypeReference<Response<String>>() {});
   }

   public Response<Boolean> updateMaintenanceReminder(String requestId, String username,
                                                     UpdateMaintenanceReminderWebRequest request) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.MAINTENANCE_REMINDER_BASE_PATH+"/_update",requestId,
            additionalParameterMap,username);
      return this.invokePostType(uri, request, UpdateMaintenanceReminderWebRequest.class, JSON_TYPE,
            new TypeReference<Response<Boolean>>() {});
   }

   public Response<Boolean> cancelMaintenanceReminder(String requestId, String username,
                                              String maintenanceReminderNumber) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.MAINTENANCE_REMINDER_BASE_PATH+"/_cancel/"+maintenanceReminderNumber,requestId,
            additionalParameterMap,username);
      return this.invokeGetType(uri, new TypeReference<Response<Boolean>>() {});
   }

   public Response<List<GetMaintenanceReminderWebResponse>> getAllMaintenanceReminder(String requestId, String username,
                                                                                      FilterAndPageRequest<GetMaintenanceReminderWebRequest, GetMaintenanceReminderSortWebRequest> request) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.MAINTENANCE_REMINDER_BASE_PATH+"/_get-all",requestId,
            additionalParameterMap, username);
      return this.invokePostType(uri, request, FilterAndPageRequest.class, JSON_TYPE,
            new TypeReference<Response<List<GetMaintenanceReminderWebResponse>>>() {});
   }

   public Response<List<GetWarehouseWebResponse>> getAllWarehouse(String requestId, String username) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.WAREHOUSE_BASE_PATH+"/_get-all",requestId,
            additionalParameterMap,username);
      return this.invokeGetType(uri, new TypeReference<Response<List<GetWarehouseWebResponse>>>() {});
   }

   public Response<List<GetItemWebResponse>> getAllItem(String requestId, String username) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.ITEM_BASE_PATH+"/_get-all",requestId,
            additionalParameterMap,username);
      return this.invokeGetType(uri, new TypeReference<Response<List<GetItemWebResponse>>>() {});
   }

   public Response<List<GetItemWebResponse>> getAllItemWithFilter(String requestId, String username,
                                                                  FilterAndPageRequest<GetItemWebRequest, GetItemSortWebRequest> request) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.ITEM_BASE_PATH+"/_get-all-item",requestId,
            additionalParameterMap,username);
      return this.invokePostType(uri, request, FilterAndPageRequest.class, JSON_TYPE,
            new TypeReference<Response<List<GetItemWebResponse>>>() {});
   }

   public Response<String> createItem(String requestId, String username, CreateItemWebRequest request) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.ITEM_BASE_PATH+"/_create",requestId,
            additionalParameterMap,username);
      return this.invokePostType(uri, request, CreateItemWebRequest.class, JSON_TYPE,
            new TypeReference<Response<String>>() {});
   }

   public Response<String> updateItem(String requestId, String username, UpdateItemWebRequest request) throws Exception {
      Map<String, String> additionalParameterMap = new HashMap<String, String>();
      URI uri = this.generateURI(AssetsManagementApiPath.ITEM_BASE_PATH+"/_update",requestId,
            additionalParameterMap,username);
      return this.invokePostType(uri, request, UpdateItemWebRequest.class, JSON_TYPE,
            new TypeReference<Response<String>>() {});
   }
}
