package com.gdn.warehouse.assetsmanagement.command;

import com.blibli.oss.backend.command.Command;
import com.gdn.warehouse.assetsmanagement.command.helper.CommandHelper;
import com.gdn.warehouse.assetsmanagement.command.model.GetTransferAssetDetailCommandRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetTransferAssetDetailWebResponse;

public interface GetTransferAssetDetailCommand extends Command<GetTransferAssetDetailCommandRequest, GetTransferAssetDetailWebResponse>,
      CommandHelper {
}
