package com.gdn.warehouse.assetsmanagement.command;

import com.blibli.oss.backend.command.Command;
import com.gdn.warehouse.assetsmanagement.command.helper.CommandHelper;
import com.gdn.warehouse.assetsmanagement.command.model.UpdateMaintenanceCommandRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.UpdateMaintenanceWebResponse;

public interface UpdateMaintenanceCommand extends Command<UpdateMaintenanceCommandRequest, UpdateMaintenanceWebResponse>, CommandHelper {
}
