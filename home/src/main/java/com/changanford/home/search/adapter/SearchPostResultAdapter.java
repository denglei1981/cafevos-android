package com.changanford.home.search.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.changanford.home.R;
import com.changanford.home.search.data.SearchData;

import java.util.List;


/***
 *
 *  资讯和帖子用同一个。。。
 * */
public class SearchPostResultAdapter extends BaseQuickAdapter<SearchData, BaseViewHolder> {

    public SearchPostResultAdapter(@Nullable List<SearchData> data) {
        super(R.layout.item_search_result_post, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchData item) {

    }
}
