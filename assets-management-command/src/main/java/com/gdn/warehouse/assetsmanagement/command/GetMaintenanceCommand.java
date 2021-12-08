package com.gdn.warehouse.assetsmanagement.command;

import com.blibli.oss.backend.command.Command;
import com.blibli.oss.backend.common.model.response.Paging;
import com.gdn.warehouse.assetsmanagement.command.helper.CommandHelper;
import com.gdn.warehouse.assetsmanagement.command.model.GetMaintenanceCommandRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetMaintenanceWebResponse;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface GetMaintenanceCommand extends Command<GetMaintenanceCommandRequest, Pair<List<GetMaintenanceWebResponse>, Paging>> ,
      CommandHelper {}
