package com.gdn.warehouse.assetsmanagement.command;

import com.blibli.oss.backend.command.Command;
import com.blibli.oss.backend.common.model.response.Paging;
import com.gdn.warehouse.assetsmanagement.command.helper.CommandHelper;
import com.gdn.warehouse.assetsmanagement.command.model.GetAllItemWithFilterCommandRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.ItemResponse;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface GetAllItemWithFilterCommand extends Command<GetAllItemWithFilterCommandRequest, Pair<List<ItemResponse>, Paging>>, CommandHelper {
}
