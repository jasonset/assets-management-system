package com.gdn.warehouse.assetsmanagement.properties;

import java.util.Arrays;
import java.util.List;

public interface StringConstants {
   String BLIBLI_TD = "BLIBLI-TD";
   String SENDER_EMAIL_ASSETS_MANAGEMENT = "AssetsManagement@gdn-commerce.com";
   List<String> USER_EMAIL_LIST = Arrays.asList("ridha.ghassini@gdn-commerce.com","jessica.suradi@gdn-commerce.com",
         "enrico.ligawirady@gdn-commerce.com","hardi.sumali@gdn-commerce.com");
   String USER_EMAIL = "ridha.ghassini@gdn-commerce.com;jessica.suradi@gdn-commerce.com;" +
         "enrico.ligawirady@gdn-commerce.com;hardi.sumali@gdn-commerce.com";
   String DELIMITER = ";";
   String ASSET_NUMBER = "assetNumber";
   String MAINTENANCE_NUMBER = "maintenanceNumber";
   String MAINTENANCE_REMINDER_NUMBER = "maintenanceReminderNumber";
   String TRANSFER_ASSET_NUMBER = "transferAssetNumber";
   String DEFAULT_SORT_DIRECTION = "ASC";
   String ONGOING = "ongoing";
   String HISTORY = "history";
   String BASE_PATH_UI = "BASE_PATH_UI";
   String DETAIL_MT_PATH = "/stockholm-ui/view/maintenance/request-detail?maintenanceNumber=";
   String DETAIL_TA_PATH = "/stockholm-ui/view/transfer-asset/pending-detail?transferAssetNumber=";
}
