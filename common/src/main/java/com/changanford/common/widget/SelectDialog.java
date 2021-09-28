package com.changanford.common.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.changanford.common.R;
import com.changanford.common.bean.DialogBottomBean;
import com.changanford.common.util.UViewHolder;

import java.util.List;

/**
 * 选择对话框 底部弹出
 */

public class SelectDialog extends Dialog implements OnClickListener {
    private SelectDialogListener mListener;
    private Activity mActivity;
    private TextView mMBtn_Cancel;
    private TextView mTv_Title;
    private List<DialogBottomBean> mName;
    private String mTitle;
    private boolean mUseCustomColor = false;
    private int mFirstItemColor;
    private int mOtherItemColor;

    private int gridType = 1;//竖  1  横 2

    private RvDialogAdapter rvDialogAdapter;

    public interface SelectDialogListener {
        void onItemClick(View view, int position, DialogBottomBean bottomBean);
    }

    /**
     * 取消事件监听接口
     */
    private SelectDialogCancelListener mCancelListener;

    public interface SelectDialogCancelListener {
        void onCancelClick(View v);
    }

    public SelectDialog(Activity activity, int theme, List<DialogBottomBean> names,
                        SelectDialogListener listener) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        this.mName = names;

        setCanceledOnTouchOutside(true);
    }

    /**
     * @param activity       调用弹出菜单的activity
     * @param theme          主题
     * @param listener       菜单项单击事件
     * @param cancelListener 取消事件
     * @param names          菜单项名称
     */
    public SelectDialog(Activity activity, int theme, SelectDialogListener listener,
                        SelectDialogCancelListener cancelListener, List<DialogBottomBean> names) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        mCancelListener = cancelListener;
        this.mName = names;

        // 设置是否点击外围不解散
        setCanceledOnTouchOutside(false);
    }

    /**
     * @param activity 调用弹出菜单的activity
     * @param theme    主题
     * @param listener 菜单项单击事件
     * @param names    菜单项名称
     * @param title    菜单标题文字
     */
    public SelectDialog(Activity activity, int theme, List<DialogBottomBean> names, String title,
                        SelectDialogListener listener) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        this.mName = names;
        mTitle = title;

        // 设置是否点击外围可解散
        setCanceledOnTouchOutside(true);
    }

    /**
     * @param activity 调用弹出菜单的activity
     * @param theme    主题
     * @param listener 菜单项单击事件
     * @param names    菜单项名称
     * @param title    菜单标题文字
     */
    public SelectDialog(Activity activity, int theme, List<DialogBottomBean> names, String title,
                        int gridType, SelectDialogListener listener) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        this.mName = names;
        this.mTitle = title;
        this.gridType = gridType;

        // 设置是否点击外围可解散
        setCanceledOnTouchOutside(true);
    }

    public SelectDialog(Activity activity, int theme, SelectDialogListener listener,
                        SelectDialogCancelListener cancelListener, List<DialogBottomBean> names, String title) {
        super(activity, theme);
        mActivity = activity;
        mListener = listener;
        mCancelListener = cancelListener;
        this.mName = names;
        mTitle = title;

        // 设置是否点击外围可解散
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.view_dialog_select, null);
        setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        Window window = getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = mActivity.getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = LayoutParams.MATCH_PARENT;
        wl.height = LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        onWindowAttributesChanged(wl);

        initViews();
    }

    private void initViews() {
        rvDialogAdapter = new RvDialogAdapter(mName);
        RecyclerView dialogList = findViewById(R.id.dialog_list);
        LinearLayout layout = findViewById(R.id.dialog_layout);

        if (gridType == 2) {//默认三 如果有其他要求 再自行修改
            dialogList.setLayoutManager(new GridLayoutManager(mActivity, 3));
            layout.setBackgroundResource(R.drawable.bg_dialog_bottom_share);
        } else {
            dialogList.setLayoutManager(new LinearLayoutManager(mActivity));
        }
        dialogList.setAdapter(rvDialogAdapter);

        mMBtn_Cancel = findViewById(R.id.mBtn_Cancel);
        mTv_Title = findViewById(R.id.mTv_Title);

        mMBtn_Cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCancelListener != null) {
                    mCancelListener.onCancelClick(v);
                }
                dismiss();
            }
        });

        if (!TextUtils.isEmpty(mTitle) && mTv_Title != null) {
            mTv_Title.setVisibility(View.VISIBLE);
            mTv_Title.setText(mTitle);
        } else {
            mTv_Title.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    class RvDialogAdapter extends RecyclerView.Adapter<UViewHolder> {

        private List<DialogBottomBean> dialogBottomBeans;
        private LayoutInflater layoutInflater;

        public RvDialogAdapter(List<DialogBottomBean> dialogBottomBeans) {
            this.dialogBottomBeans = dialogBottomBeans;
            this.layoutInflater = mActivity.getLayoutInflater();
        }

        @NonNull
        @Override
        public UViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView;
            if (viewType == 1) {
                itemView = layoutInflater.inflate(R.layout.view_dialog_item, null);
                return new UViewHolder(itemView);
            } else if (viewType == 2) {
                itemView = layoutInflater.inflate(R.layout.view_dialog_grid_item, null);
                return new UViewHolder(itemView);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull UViewHolder holder, final int position) {
            final DialogBottomBean dialogBottomBean = mName.get(position);
            if (dialogBottomBean.getTypeViewId() == 1) {
                TextView dialogItemButton = (TextView) holder.getView(R.id.dialog_item_bt);
                dialogItemButton.setText(dialogBottomBean.getTitle());
                if (!mUseCustomColor) {
                    mFirstItemColor = mActivity.getResources().getColor(R.color.text_color_08);
                    mOtherItemColor = mActivity.getResources().getColor(R.color.text_color_08);
                }
                if (1 == mName.size()) {
                    dialogItemButton.setTextColor(mFirstItemColor);
                    dialogItemButton.setBackgroundResource(R.drawable.dialog_item_bg_only);
                } else if (position == 0) {
                    dialogItemButton.setTextColor(mFirstItemColor);
                    dialogItemButton.setBackgroundResource(R.drawable.select_dialog_item_bg_top);
                } else if (position == mName.size() - 1) {
                    dialogItemButton.setTextColor(mOtherItemColor);
                    dialogItemButton.setBackgroundResource(R.drawable.select_dialog_item_bg_buttom);
                } else {
                    dialogItemButton.setTextColor(mOtherItemColor);
                    dialogItemButton.setBackgroundResource(R.drawable.select_dialog_item_bg_center);
                }
                dialogItemButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onItemClick(view, position, dialogBottomBean);
                        dismiss();
                    }
                });
            } else if (dialogBottomBean.getTypeViewId() == 2) {
                TextView title = (TextView) holder.getView(R.id.item_grid_title);
                title.setText(dialogBottomBean.getTitle());
                ImageView icon = (ImageView) holder.getView(R.id.item_grid_icon);
                icon.setImageResource(dialogBottomBean.getResId());
                holder.getView(R.id.item_grid_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onItemClick(view, position, dialogBottomBean);
                        dismiss();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return null == dialogBottomBeans ? 0 : dialogBottomBeans.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mName.get(position).getTypeViewId();
        }
    }

    public void notifyData(List<DialogBottomBean> dialogBottomBeans) {
        this.mName = dialogBottomBeans;
        if (null != rvDialogAdapter) {
            rvDialogAdapter.notifyDataSetChanged();
        }
        if (!mActivity.isFinishing()) {
            show();
        }
    }

    /**
     * 设置列表项的文本颜色
     */
    public void setItemColor(int firstItemColor, int otherItemColor) {
        mFirstItemColor = firstItemColor;
        mOtherItemColor = otherItemColor;
        mUseCustomColor = true;
    }
}
