package com.changanford.home.search.adapter;

import android.widget.TextView;
import androidx.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.changanford.home.R;
import com.changanford.home.bean.SearchKeyBean;

import java.util.List;


public class SearchHotAdapter extends BaseQuickAdapter<SearchKeyBean, BaseViewHolder> {

    public SearchHotAdapter(@Nullable List<SearchKeyBean> data) {
        super(R.layout.item_common_search_hot, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchKeyBean item) {
        TextView tvLabel = helper.getView(R.id.label);
        tvLabel.setText(item.getKeyword());
    }
}
