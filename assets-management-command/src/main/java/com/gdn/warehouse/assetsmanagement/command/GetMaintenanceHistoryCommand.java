package com.gdn.warehouse.assetsmanagement.command;

import com.blibli.oss.backend.command.Command;
import com.gdn.warehouse.assetsmanagement.command.helper.CommandHelper;
import com.gdn.warehouse.assetsmanagement.command.model.GetMaintenanceHistoryCommandRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetMaintenanceHistoryWebResponse;

import java.util.List;

public interface GetMaintenanceHistoryCommand extends Command<GetMaintenanceHistoryCommandRequest, List<GetMaintenanceHistoryWebResponse>>,
      CommandHelper {
}
