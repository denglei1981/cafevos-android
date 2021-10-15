package com.changanford.home.acts.adapter;

import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.changanford.home.R;
import com.changanford.home.data.EnumBean;

import java.util.List;

public class UnitSelectAdapter extends BaseQuickAdapter<EnumBean, BaseViewHolder> {

    public int clickPosition = 0;

    public UnitSelectAdapter(@Nullable List<EnumBean> data) {
        super(R.layout.item_home_desc_type, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, EnumBean item) {
        TextView tvLabel = helper.getView(R.id.tv_sex);
        tvLabel.setText(item.getMessage());
        if (clickPosition == helper.getLayoutPosition()) {
            tvLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.blue_tab));
        } else {
            tvLabel.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }

    }
}
