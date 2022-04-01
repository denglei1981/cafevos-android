package com.changanford.common.widget.loadmore;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.loadmore.BaseLoadMoreView;
import com.chad.library.adapter.base.loadmore.LoadMoreStatus;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.changanford.common.R;


import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Created by BlingBling on 2016/10/15.
 */

public final class TheHellLoadMoreView extends BaseLoadMoreView {

    public  TextView tvEnd;
    @NotNull
    @Override
    public View getRootView(@NotNull ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.view_load_more, parent, false);
    }

    @NotNull
    @Override
    public View getLoadingView(@NotNull BaseViewHolder holder) {
        return Objects.requireNonNull(holder.findView(R.id.load_more_loading_view));
    }

    @NotNull
    @Override
    public View getLoadComplete(@NotNull BaseViewHolder holder) {
        return Objects.requireNonNull(holder.findView(R.id.load_more_load_complete_view));
    }

    @NotNull
    @Override
    public View getLoadEndView(@NotNull BaseViewHolder holder) {
        View view = holder.findView(R.id.load_more_load_end_view);
        assert view != null;
        tvEnd = view.findViewById(R.id.tv_end);
        return view;
    }

    @NotNull
    @Override
    public View getLoadFailView(@NotNull BaseViewHolder holder) {
        return Objects.requireNonNull(holder.findView(R.id.load_more_load_fail_view));
    }

    @Override
    public void convert(@NonNull BaseViewHolder holder, int position, @NonNull LoadMoreStatus loadMoreStatus) {
        super.convert(holder, position, loadMoreStatus);

    }
}
