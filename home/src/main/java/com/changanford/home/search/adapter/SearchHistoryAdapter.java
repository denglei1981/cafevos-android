package com.changanford.home.search.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.changanford.home.R;
import com.changanford.home.room.SearchRecordEntity;
import java.util.List;
public class SearchHistoryAdapter extends BaseQuickAdapter<SearchRecordEntity, BaseViewHolder> {

    public boolean isExpand = false;

    public SearchHistoryAdapter(@Nullable List<SearchRecordEntity> data) {
        super(R.layout.item_common_search_tag, data);
    }

    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchRecordEntity item) {
        TextView tvLabel = helper.getView(R.id.tv_search_tag);
        ImageView tvMore = helper.getView(R.id.iv_more_down);
        if (helper.getAdapterPosition() == 7 && !isExpand) {
            tvMore.setVisibility(View.VISIBLE);
            tvLabel.setText("");
        } else {
            tvMore.setVisibility(View.GONE);
            tvLabel.setText(item.getKeyword());
        }
    }
}
