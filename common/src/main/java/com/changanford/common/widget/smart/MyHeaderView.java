package com.changanford.common.widget.smart;

import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.changanford.common.R;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.util.SmartUtil;

public class MyHeaderView extends LinearLayout implements RefreshHeader {
    private AnimatorImageView imageView;

    public MyHeaderView(Context context) {
        super(context);
        initview(context);
    }

    public MyHeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }

    public MyHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initview(context);
    }


    @NonNull
    @Override
    public View getView() {
        return this;
    }

    public void initview(Context context){
        setGravity(Gravity.CENTER);
         imageView = new AnimatorImageView(context);
        addView(imageView);
        setMinimumHeight(SmartUtil.dp2px(60));
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
//        imageView.animator(false);
        imageView.setImageResource(R.mipmap.refreshtitlenomale);
        return 100;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        switch (newState) {
            case None:
                imageView.setImageResource(R.mipmap.refreshtitlenomale);
                break;
            case PullDownToRefresh:
                imageView.setImageResource(R.mipmap.refreshtitlenomale);
                break;
            case Refreshing:
                imageView.setImageResource(R.drawable.animation_listpicitem);
                imageView.animator(true);
                break;
            case ReleaseToRefresh:
                imageView.setImageResource(R.mipmap.refreshtitleset);
//                Vibrator vibrator = (Vibrator)getContext().getSystemService(getContext().VIBRATOR_SERVICE);
//                vibrator.vibrate(100);
                break;
        }
    }
}
