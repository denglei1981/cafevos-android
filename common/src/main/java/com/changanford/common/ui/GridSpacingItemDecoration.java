package com.changanford.common.ui;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * ================================================
 * 作    者：顾修忠-guxiuzhong@youku.com/gfj19900401@163.com
 * 版    本：
 * 创建日期：2017/2/18-上午1:03
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private int thumbnailsCount;

    public GridSpacingItemDecoration(int space, int thumbnailsCount) {
        this.space = space;
        this.thumbnailsCount = thumbnailsCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // 第一个的前面和最后一个的后面
        int position = parent.getChildAdapterPosition(view);
        outRect.top=space;

        if((position+1)%thumbnailsCount==0){
            outRect.right=0;
        }else {
            outRect.right=10;
        }

    }
}