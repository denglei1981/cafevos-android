package com.changanford.circle.utils;

import android.content.Context;

import com.google.android.flexbox.FlexLine;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.List;

/**
 * Author lcw
 * Time on 2022/9/5
 * Purpose
 */
public class FlexboxLayoutManagerCustom extends FlexboxLayoutManager {
    private int maxLines;

    public FlexboxLayoutManagerCustom(Context context, int maxLines) {
        super(context);
        this.maxLines = maxLines ;
    }

    @Override
    public int getMaxLine() {
        return super.getMaxLine();
    }

    /**
     * 这里限制了最大行数，多出部分被以 subList 方式截掉
     */
    @Override
    public List<FlexLine> getFlexLinesInternal() {
        List<FlexLine> flexLines = super.getFlexLinesInternal();
        int size = flexLines.size();
        if (maxLines > 0 && size > maxLines) {
            flexLines.subList(maxLines, size).clear();
        }
        return flexLines;
    }
}
