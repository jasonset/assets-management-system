package com.gdn.warehouse.assetsmanagement.helper.util;

import com.gdn.warehouse.assetsmanagement.properties.StringConstants;

public class SortDirectionHelper {

   private static final String DES = "DES";
   private static final String DESC = "DESC";
   private static final String ASC = "ASC";

   public static String getSortDirection(String sortDirection) {
      switch (sortDirection.toUpperCase()) {
         case ASC:
            return ASC;
         case DES:
         case DESC:
            return DESC;
         default:
            return StringConstants.DEFAULT_SORT_DIRECTION.toUpperCase();
      }
   }
}
