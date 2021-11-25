package com.gdn.warehouse.assetsmanagement.command;

import com.blibli.oss.backend.command.Command;
import com.gdn.warehouse.assetsmanagement.command.helper.CommandHelper;
import com.gdn.warehouse.assetsmanagement.command.model.GetAssetDetailCommandRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetAssetDetailWebResponse;

public interface GetAssetDetailCommand extends Command<GetAssetDetailCommandRequest, GetAssetDetailWebResponse>,
      CommandHelper {
}
