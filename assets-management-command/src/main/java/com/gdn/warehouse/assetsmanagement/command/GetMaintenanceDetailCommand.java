package com.gdn.warehouse.assetsmanagement.command;

import com.blibli.oss.backend.command.Command;
import com.gdn.warehouse.assetsmanagement.command.helper.CommandHelper;
import com.gdn.warehouse.assetsmanagement.command.model.GetMaintenanceDetailCommandRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetMaintenanceDetailWebResponse;

public interface GetMaintenanceDetailCommand extends Command<GetMaintenanceDetailCommandRequest, GetMaintenanceDetailWebResponse>,
      CommandHelper {}
