package com.changanford.home.search.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.changanford.home.R;
import com.changanford.home.search.data.SearchData;

import java.util.List;


public class SearchUserResultAdapter extends BaseQuickAdapter<SearchData, BaseViewHolder> {

    public SearchUserResultAdapter(@Nullable List<SearchData> data) {
        super(R.layout.item_search_result_user, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchData item) {

    }
}
