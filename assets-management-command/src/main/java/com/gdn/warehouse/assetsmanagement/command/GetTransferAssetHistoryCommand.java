package com.gdn.warehouse.assetsmanagement.command;

import com.blibli.oss.backend.command.Command;
import com.gdn.warehouse.assetsmanagement.command.helper.CommandHelper;
import com.gdn.warehouse.assetsmanagement.command.model.GetTransferAssetHistoryCommandRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetTransferAssetHistoryWebResponse;

import java.util.List;

public interface GetTransferAssetHistoryCommand extends Command<GetTransferAssetHistoryCommandRequest, List<GetTransferAssetHistoryWebResponse>>
      , CommandHelper {
}
