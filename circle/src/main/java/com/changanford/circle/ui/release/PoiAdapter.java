package com.changanford.circle.ui.release;

import com.baidu.mapapi.search.core.PoiInfo;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.changanford.circle.R;

import org.jetbrains.annotations.NotNull;

public class PoiAdapter extends BaseQuickAdapter<PoiInfo, BaseViewHolder> {
    public PoiAdapter() {
        super(R.layout.poi_item);

    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, PoiInfo poiInfo) {
        baseViewHolder.setText(R.id.tv_name,poiInfo.name);
        baseViewHolder.setText(R.id.tv_address,poiInfo.address);
    }
}
