package com.changanford.home.search.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.changanford.home.R;
import com.changanford.home.search.data.SearchData;

import java.util.List;


public class SearchActsResultAdapter extends BaseQuickAdapter<SearchData, BaseViewHolder> {

    public SearchActsResultAdapter(@Nullable List<SearchData> data) {
        super(R.layout.item_home_acts, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchData item) {

    }
}
