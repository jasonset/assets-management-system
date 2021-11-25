package com.gdn.warehouse.assetsmanagement.web;

import com.gdn.warehouse.assetsmanagement.properties.AssetsManagementSchedulerProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.scheduler.Scheduler;

public class TestConfiguration {

   @MockBean
   @Qualifier(AssetsManagementSchedulerProperties.SCHEDULER_NAME)
   private Scheduler assetsManagementScheduler;
}
