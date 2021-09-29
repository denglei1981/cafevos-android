package com.changanford.home.acts.adapter;

import android.widget.TextView;

import androidx.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.changanford.home.R;
import com.changanford.home.search.data.SearchData;
import java.util.List;

public class UnitSelectAdapter extends BaseQuickAdapter<SearchData, BaseViewHolder> {

    public int clickPostion = 0;

    public UnitSelectAdapter(@Nullable List<SearchData> data) {
        super(R.layout.item_home_desc_type, data);
    }
    @Override
    protected void convert(BaseViewHolder helper, SearchData item) {
        TextView tvLabel = helper.getView(R.id.tv_sex);
          tvLabel.setText("种花家");

    }
}
