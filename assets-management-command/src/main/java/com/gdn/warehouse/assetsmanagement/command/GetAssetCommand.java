package com.gdn.warehouse.assetsmanagement.command;

import com.blibli.oss.backend.command.Command;
import com.blibli.oss.backend.common.model.response.Paging;
import com.gdn.warehouse.assetsmanagement.command.helper.CommandHelper;
import com.gdn.warehouse.assetsmanagement.command.model.GetAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetAssetWebResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GetAssetCommand extends Command<GetAssetCommandRequest, Pair<List<GetAssetWebResponse>, Paging>>,
      CommandHelper {}
