package com.changanford.common.widget.smart;

import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.changanford.common.MyApp;
import com.changanford.common.R;
import com.changanford.common.util.DisplayUtil;
import com.changanford.common.util.JsonReadUtil;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.util.SmartUtil;

public class MyHeaderViewNew extends LinearLayout implements RefreshHeader {
    private LottieAnimationView imageView;
    private String jsonStr;

    public MyHeaderViewNew(Context context) {
        super(context);
        initview(context);
    }

    public MyHeaderViewNew(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initview(context);
    }

    public MyHeaderViewNew(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initview(context);
    }


    @NonNull
    @Override
    public View getView() {
        return this;
    }

    public void initview(Context context){
        setGravity(Gravity.CENTER_HORIZONTAL);
        imageView = new LottieAnimationView(context);
        addView(imageView);
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.height = DisplayUtil.dip2px(context,100);
        params.width = DisplayUtil.dip2px(context,100);
        imageView.setLayoutParams(params);
        imageView.setScaleX(0.5f);
        imageView.setScaleY(0.5f);
        setMinimumHeight(SmartUtil.dp2px(60));
        jsonStr = JsonReadUtil.getJsonStr(context,"loading.json");
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
        imageView.setAnimationFromJson(jsonStr,"1");
        imageView.pauseAnimation();
        imageView.setVisibility(View.GONE);
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
                imageView.setAnimationFromJson(jsonStr,"1");
                imageView.setVisibility(View.GONE);
                break;
            case PullDownToRefresh:
                imageView.setAnimationFromJson(jsonStr,"1");
                imageView.pauseAnimation();
                imageView.setVisibility(View.VISIBLE);
                break;
            case Refreshing:
                imageView.setAnimationFromJson(jsonStr,"1");
                imageView.setVisibility(View.VISIBLE);
                imageView.playAnimation();
                break;
            case ReleaseToRefresh:
                imageView.setAnimationFromJson(jsonStr,"1");
                imageView.setVisibility(View.VISIBLE);
                break;
        }
    }
}
