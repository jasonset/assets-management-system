package com.gdn.warehouse.assetsmanagement.helper;

import com.gdn.warehouse.assetsmanagement.entity.Schedule;

public interface SchedulerPlatformHelper {
   void sendToSchedulerPlatform(Schedule schedule);

   void sendCancellationToSchedulerPlatform(Schedule schedule);
}
