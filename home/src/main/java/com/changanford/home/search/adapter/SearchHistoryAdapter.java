package com.changanford.home.search.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import com.changanford.home.R;
import com.changanford.home.search.data.SearchData;


import java.util.List;

public class SearchHistoryAdapter extends BaseQuickAdapter<SearchData, BaseViewHolder> {

    public SearchHistoryAdapter(@Nullable List<SearchData> data) {
        super(R.layout.item_common_search_tag, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchData item) {
        TextView tvLabel=helper.getView(R.id.tv_search_tag);
        tvLabel.setText(item.tagName);
        GradientDrawable myGrad = (GradientDrawable) tvLabel.getBackground();
        myGrad.setColor(Color.parseColor("#f6f6f6"));

    }
}
