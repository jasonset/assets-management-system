package com.gdn.warehouse.assetsmanagement.command;

import com.blibli.oss.backend.command.Command;
import com.gdn.warehouse.assetsmanagement.command.helper.CommandHelper;
import com.gdn.warehouse.assetsmanagement.command.model.UpdateMaintenanceReminderCommandRequest;

public interface UpdateMaintenanceReminderCommand extends Command<UpdateMaintenanceReminderCommandRequest,Boolean>, CommandHelper {
}
