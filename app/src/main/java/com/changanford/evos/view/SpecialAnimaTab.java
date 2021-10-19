package com.changanford.evos.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;


import com.changanford.evos.R;

import me.majiajie.pagerbottomtabstrip.internal.RoundMessageView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;

/**
 * Created by mjj on 2017/6/3
 * 注意：此文件只能使用在首页底部导航栏，因为修改了底部布局，适用于
 */
public class SpecialAnimaTab extends BaseTabItem {
    private ImageView ivyuanshu;
    private ImageView mIcon;
    private final TextView mTitle;
    private final RoundMessageView mMessages;
    private final ImageView mMessage;

    private int mDefaultDrawable;
    private int mCheckedDrawable;

    private Bitmap mDefaultBitmap;
    private Bitmap mCheckedBitmap;
    private int myuanshu = 0x56000001;
    private int mDefaultTextColor = 0x56000000;
    private int mCheckedTextColor = getContext().getResources().getColor(R.color.black);
    boolean mChecked;
    float Yfloat = 0f;
    float Xfloat = 0f;

    public SpecialAnimaTab(Context context) {
        this(context, null);
    }

    public SpecialAnimaTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpecialAnimaTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.special_animo_tab, this, true);
        mIcon = (ImageView) findViewById(R.id.icon);
        mTitle = (TextView) findViewById(R.id.title);
        ivyuanshu = findViewById(R.id.ivyuanshu);
        mMessages = (RoundMessageView) findViewById(R.id.messages);
        mMessage = findViewById(R.id.icon_msg);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        View view = getChildAt(0);
        if (view != null) {
            view.setOnClickListener(l);
        }
    }

    public void setmsgGone() {
        mMessages.setVisibility(View.GONE);
        mMessage.setVisibility(View.GONE);
    }

    public void setmsgVisible() {
        mMessage.setVisibility(View.VISIBLE);
    }

    /**
     * 方便初始化的方法
     *
     * @param drawableRes        默认状态的图标
     * @param checkedDrawableRes 选中状态的图标
     * @param title              标题
     */
    public void initialize(@DrawableRes int drawableRes, @DrawableRes int checkedDrawableRes, String title) {
        mDefaultDrawable = drawableRes;
        mCheckedDrawable = checkedDrawableRes;
        mTitle.setText(title);
    }

    /**
     * 方便初始化的方法
     *
     * @param mDefaultBitmap 默认状态的图标
     * @param mCheckedBitmap 选中状态的图标
     * @param title          标题
     */
    public void initialize(Bitmap mDefaultBitmap, Bitmap mCheckedBitmap, String title) {
        this.mCheckedBitmap = mDefaultBitmap;
        this.mCheckedBitmap = mCheckedBitmap;
        mTitle.setText(title);
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked) {
            ivyuanshu.setImageResource(myuanshu);
            mIcon.setImageResource(mCheckedDrawable);
            mTitle.setTextColor(mCheckedTextColor);
            startAnima(mIcon, ivyuanshu);
        } else {
            mIcon.setImageResource(mDefaultDrawable);
            mTitle.setTextColor(mDefaultTextColor);
            gonAnima(mIcon, ivyuanshu);
        }
        mChecked = checked;
    }

    @Override
    public void setMessageNumber(int number) {
        mMessages.setMessageNumber(number);
    }

    @Override
    public void setHasMessage(boolean hasMessage) {
        mMessages.setHasMessage(hasMessage);
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setDefaultDrawable(Drawable drawable) {

    }

    @Override
    public void setSelectedDrawable(Drawable drawable) {

    }

    @Override
    public String getTitle() {
        return mTitle.getText().toString();
    }

    public void setTextDefaultColor(int color) {
        mDefaultTextColor = color;
    }

    public void setTextDefaultColor(String color) {
        mDefaultTextColor = Color.parseColor(color);
    }

    public void setTextCheckedColor(int color) {
        mCheckedTextColor = color;
    }

    public void setIvyuanshu(int color) {
        myuanshu = color;
    }

    public void setYfloat(float yfloat) {
        Yfloat = yfloat;
    }

    public void setXfloat(float xfloat) {
        Xfloat = xfloat;
    }


    public void setTextCheckedColor(String color) {
        mCheckedTextColor = Color.parseColor(color);
    }

    float jl;
    AnimatorSet set;

    public void startAnima(View view, View view1) {
        view1.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                int x = location[0]; // view距离 屏幕左边的距离（即x轴方向）
                int[] location1 = new int[2];
                view1.getLocationOnScreen(location1);
                int x1 = location1[0];
//                jl = view.getX() - view1.getX() + view1.getWidth() / 2;
                jl = x-x1;
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "translationY", 0f, -10f, 0f);
                Log.d("juli", jl + "");
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(ivyuanshu, "translationY", 0f, Yfloat);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(ivyuanshu, "translationX", 0f, jl+Xfloat);
                ObjectAnimator animator4 = ObjectAnimator.ofFloat(ivyuanshu, "rotation", 0f, -60f, 0f);
                set = new AnimatorSet();
                set.playTogether(animator1, animator3, animator2, animator4);
                set.start();
            }
        });

    }

    public void gonAnima(View view, View view1) {
        mIcon.setTranslationX(0f);
        mIcon.setTranslationY(0f);
        ivyuanshu.setTranslationX(0f);
        ivyuanshu.setTranslationY(0f);
        if (set != null) {
            set.cancel();
        }
    }
}
