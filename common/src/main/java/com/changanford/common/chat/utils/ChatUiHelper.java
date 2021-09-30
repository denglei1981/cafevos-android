package com.changanford.common.chat.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.changanford.common.chat.widget.StateButton;


/**
 * 文件名：ChatUiHelper
 * 创建者: zcy
 * 创建日期：2020/10/13 10:41
 * 描述: TODO
 * 修改描述：TODO
 */
public class ChatUiHelper {

    private static final String SHARE_PREFERENCE_NAME = "com.chat.demo.ui";
    private static final String SHARE_PREFERENCE_TAG = "soft_input_height";

    private Activity mActivity;
    private EditText mInputEditText;//输入布局
    private ImageView mAddBtn;//加号布局
    private StateButton mSendBtn;//发送
    private View mBottomLayout;//底部功能布局
    private LinearLayout mContentLayout;//内容布局

    private InputMethodManager mInputManager;
    private SharedPreferences mSp;
    private Handler handler;


    public static ChatUiHelper with(Activity activity) {
        ChatUiHelper chatUiHelper = new ChatUiHelper(activity);
        chatUiHelper.mInputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        chatUiHelper.mSp = activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        chatUiHelper.handler = new Handler();
        return chatUiHelper;
    }

    private ChatUiHelper(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 绑定输入EditText
     *
     * @param inputEditText
     * @return
     */
    @SuppressLint("ClickableViewAccessibility")
    public ChatUiHelper bindInputEditText(final EditText inputEditText) {
        this.mInputEditText = inputEditText;
//        mInputEditText.requestFocus();

        mInputEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && mBottomLayout.isShown()) {
                    lockContentHeight();
                    //按下 功能布局显示隐藏功能布局
                    hideBottomLayout(1000);
                    mInputEditText.post(new Runnable() {
                        @Override
                        public void run() {
                            unlockContentHeightDelayed();
                        }
                    });
                }
                return false;
            }
        });

        mInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mInputEditText.getText().toString().trim().length() > 0) {
                    //显示发送按钮
                    mSendBtn.setVisibility(View.VISIBLE);
                    mAddBtn.setVisibility(View.INVISIBLE);
                } else {
                    mSendBtn.setVisibility(View.INVISIBLE);
                    mAddBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return this;
    }

    /**
     * 绑定加号布局
     *
     * @param addBtn
     * @return
     */
    public ChatUiHelper bindAddView(ImageView addBtn) {
        this.mAddBtn = addBtn;
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputEditText.clearFocus();
                if (isSoftInputShown()) {//已显示软件盘
                    hideSoftInput();//隐藏软键盘
                    lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                    showBottomLayout(100);//先显示底部功能布局
                    unlockContentHeightDelayed();//释放高度
                } else {
                    if (!mBottomLayout.isShown()) {
                        lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                        showBottomLayout(300);
                        unlockContentHeightDelayed();//释放高度
                    } else {
                        lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                        showSoftInput();
                        hideBottomLayout(200);
                        unlockContentHeightDelayed();//释放高度
                    }
                }
            }
        });
        return this;
    }

    /**
     * 绑定发送按钮
     *
     * @param sendBtn
     * @return
     */
    public ChatUiHelper bindSendView(StateButton sendBtn) {
        this.mSendBtn = sendBtn;
        return this;
    }

    /**
     * 绑定底部Add功能布局
     *
     * @return
     */
    public ChatUiHelper bindBottomLayout(View bottomLayout) {
        this.mBottomLayout = bottomLayout;
        return this;
    }


    /**
     * 绑定内容布局
     *
     * @param contentLayout
     * @return
     */
    public ChatUiHelper bindContentLayout(LinearLayout contentLayout) {
        this.mContentLayout = contentLayout;
        return this;
    }

    /**
     * 显示底部功能布局
     */
    private void showBottomLayout(int delayMillis) {
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = mSp.getInt(SHARE_PREFERENCE_TAG, dip2Px(270));
        }
        mBottomLayout.getLayoutParams().height = softInputHeight;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBottomLayout.setVisibility(View.VISIBLE);
            }
        }, delayMillis);
    }


    /**
     * 隐藏底部布局
     */
    public void hideBottomLayout(int delayMillis) {
        if (mBottomLayout.isShown()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBottomLayout.setVisibility(View.GONE);
                }
            }, delayMillis);
        }
    }

    /**
     * 是否显示软件盘
     *
     * @return
     */
    public boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    public int dip2Px(int dip) {
        float density = mActivity.getApplicationContext().getResources().getDisplayMetrics().density;
        int px = (int) (dip * density + 0.5f);
        return px;
    }


    /**
     * 隐藏软件盘
     */
    public void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(mInputEditText.getWindowToken(), 0);
    }


    /**
     * 获取软件盘的高度
     *
     * @return
     */
    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        /*  *
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。*/
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        //计算软件盘的高度
        int softInputHeight = screenHeight - r.bottom;

        if (isNavigationBarExist(mActivity)) {
            softInputHeight = softInputHeight - getNavigationHeight(mActivity);
        }
        //存一份到本地
        if (softInputHeight > 0) {
            mSp.edit().putInt(SHARE_PREFERENCE_TAG, softInputHeight).apply();
        }
        return softInputHeight;
    }

    /**
     * 显示键盘
     */
    public void showSoftInput() {
        mInputEditText.requestFocus();
        mInputEditText.post(new Runnable() {
            @Override
            public void run() {
                mInputManager.showSoftInput(mInputEditText, 0);
            }
        });
    }

    /**
     * 锁定内容高度，防止跳闪
     */
    private void lockContentHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentLayout.getLayoutParams();
        params.height = mContentLayout.getHeight();
        params.weight = 0F;
    }

    /**
     * 释放被锁定的内容高度
     */
    public void unlockContentHeightDelayed() {
        mInputEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) mContentLayout.getLayoutParams()).weight = 1.0F;
            }
        }, 200L);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }


    private static final String NAVIGATION = "navigationBarBackground";

    // 该方法需要在View完全被绘制出来之后调用，否则判断不了
    //在比如 onWindowFocusChanged（）方法中可以得到正确的结果
    public boolean isNavigationBarExist(@NonNull Activity activity) {
        ViewGroup vp = (ViewGroup) activity.getWindow().getDecorView();
        if (vp != null) {
            for (int i = 0; i < vp.getChildCount(); i++) {
                vp.getChildAt(i).getContext().getPackageName();
                if (vp.getChildAt(i).getId() != View.NO_ID &&
                        NAVIGATION.equals(activity.getResources().getResourceEntryName(vp.getChildAt(i).getId()))) {
                    return true;
                }
            }
        }
        return false;
    }


    public int getNavigationHeight(Context activity) {
        if (activity == null) {
            return 0;
        }
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        int height = 0;
        if (resourceId > 0) {
            //获取NavigationBar的高度
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }
}
