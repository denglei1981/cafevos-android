package com.changanford.common.sharelib.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.changanford.common.R;
import com.changanford.common.sharelib.bean.IMediaObject;
import com.changanford.common.sharelib.util.PlamForm;
import com.changanford.common.ui.dialog.AlertDialog;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;

import java.util.List;


/**
 * 分享控件
 * Created by lihongjun on 2018/1/11.
 */

public class ShareDialog<T extends IMediaObject> extends Dialog implements View.OnClickListener {

    private final String TAG = "ShareDialog";
    private List<T> mPlamforms; // 分享平台集合 集成 微信 微博 QQ
    private TextView mTvWxMoment; // 微信朋友圈
    private TextView mTvWxChat; // 微信好友
    private TextView mTvWeiBo; // 微博
    private TextView mTvQQ; // QQ
    private TextView mTvQQZoom; //qq空间
    private TextView tvclose; //关闭
    private int mLayoutResId; //默认布局
    private View mCustomView; // 默认分享dialog自定义布局
    private float mDimAmount = 0.3f; // 对话框的蒙层清晰度
    private OnPlamFormClickListener mPlamFormClickListener;
    private int type;
    private LinearLayout llbuttom;
    private LinearLayout ll_jbs;
    private LinearLayout ll_act;
    private LinearLayout ll_deleteact;
    private LinearLayout ll_jubao;
    private LinearLayout ll_unlike;
    private LinearLayout ll_jj;
    private LinearLayout ll_bj;
    private LinearLayout ll_sc;
    private LinearLayout ll_pb;
    private TextView jj_tv;
    private boolean showpictureshare;
    private TextView btnhaibao;
    private TextView tv_tag;
    private TextView btn_copy;
    Context context;
    public ShareDialog(@NonNull Context context, int type, boolean ShowPictureShare) {
        super(context, R.style.BottomDialog_Animation);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        //布局位于状态栏下方
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        //全屏
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        //隐藏导航栏
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                if (Build.VERSION.SDK_INT >= 19) {
                    uiOptions |= 0x00001000;
                } else {
                    uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                }
                getWindow().getDecorView().setSystemUiVisibility(uiOptions);
            }
        });
        this.type = type;
        this.context = context;
        this.showpictureshare = ShowPictureShare;
        mLayoutResId = R.layout.share_dialog_share_view;
    }


    /**
     * 自定义分享View
     *
     * @param customView
     */
    public void setCustomView(View customView) {
        mCustomView = customView;
    }

    /**
     * 设置对话框的蒙层清晰度
     *
     * @param dimAmount
     */
    public void setDimAmount(float dimAmount) {
        mDimAmount = dimAmount;
    }

    /**
     * 设置平台
     *
     * @param plamforms
     */
    public void setPlamforms(List<T> plamforms) {
        mPlamforms = plamforms;
    }

    /**
     * 设置平台点击回调
     *
     * @param plamFormClickListener 设置分享平台点击回调
     */
    public void setPlamFormClickListener(OnPlamFormClickListener plamFormClickListener) {
        mPlamFormClickListener = plamFormClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mCustomView == null) {
            setContentView(mLayoutResId);
        } else {
            setContentView(mCustomView);
        }

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth(); //设置dialog的宽度为当前手机屏幕的宽度
        p.dimAmount = mDimAmount;
        getWindow().setAttributes(p);
        this.getWindow().setGravity(Gravity.BOTTOM);
        this.setCanceledOnTouchOutside(true);
        this.setCancelable(true);
        initView();
        initData();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        llbuttom = findViewById(R.id.ll_buttom);
        ll_act = findViewById(R.id.ll_act);
        ll_jbs = findViewById(R.id.ll_jbs);
        ll_deleteact = findViewById(R.id.ll_deleteact);
        ll_jubao = findViewById(R.id.ll_jubao);
        ll_unlike = findViewById(R.id.ll_unlike);
        ll_jj = findViewById(R.id.ll_jj);
        jj_tv = findViewById(R.id.jj_tv);
        ll_bj = findViewById(R.id.ll_bj);
        ll_sc = findViewById(R.id.ll_sc);
        ll_pb = findViewById(R.id.ll_pb);
        tv_tag = findViewById(R.id.tv_tag);
        btnhaibao = findViewById(R.id.btn_haibao);
        btn_copy = findViewById(R.id.btn_copy);
        if (showpictureshare) {
            tv_tag.setVisibility(View.GONE);
            btnhaibao.setVisibility(View.VISIBLE);
        } else {
            tv_tag.setVisibility(View.VISIBLE);
            btnhaibao.setVisibility(View.GONE);
        }
        switch (type) {
            case 0:  //不显示分享底部布局
                llbuttom.setVisibility(View.GONE);
                break;
            case 1: //显示举报不喜欢功能
                llbuttom.setVisibility(View.VISIBLE);
                ll_act.setVisibility(View.VISIBLE);
                break;
            case 2: //显示结束发布
                llbuttom.setVisibility(View.VISIBLE);
                ll_deleteact.setVisibility(View.VISIBLE);
                break;
            case 3://显示加精、编辑、删除
                llbuttom.setVisibility(View.VISIBLE);
                ll_jbs.setVisibility(View.VISIBLE);
                ll_bj.setVisibility(View.VISIBLE);
                break;
            case 4://显示举报、不喜欢、屏蔽
                llbuttom.setVisibility(View.VISIBLE);
                ll_act.setVisibility(View.VISIBLE);
                ll_pb.setVisibility(View.INVISIBLE);
                break;
            case 5://显示加精、删除
                llbuttom.setVisibility(View.VISIBLE);
                ll_jbs.setVisibility(View.VISIBLE);
                break;
        }
        mTvWxMoment = findViewById(R.id.btn_share_wechat_moments);
        mTvWxChat = findViewById(R.id.btn_share_wechat);
        mTvWeiBo = findViewById(R.id.btn_share_weibo);
        mTvQQ = findViewById(R.id.btn_share_qq);
        mTvQQZoom = findViewById(R.id.btn_share_qqzoom);
        tvclose = findViewById(R.id.tvclose);
        mTvWxMoment.setOnClickListener(this);
        mTvWxChat.setOnClickListener(this);
        mTvWeiBo.setOnClickListener(this);
        mTvQQ.setOnClickListener(this);
        mTvQQZoom.setOnClickListener(this);
        tvclose.setOnClickListener(this);
        ll_deleteact.setOnClickListener(this);
        ll_jubao.setOnClickListener(this);
        ll_unlike.setOnClickListener(this);
        ll_jj.setOnClickListener(this);
        ll_bj.setOnClickListener(this);
        ll_sc.setOnClickListener(this);
        ll_pb.setOnClickListener(this);
        btnhaibao.setOnClickListener(this);
        btn_copy.setOnClickListener(this);
        switch (is_good) {
            case 1://加精
                jj_tv.setText("已加精");
                break;
            case 2://不加精
                jj_tv.setText("申请加精");
                break;
            case 3://审核中
                jj_tv.setText("已申请");
                break;
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 如果未设置平台
        if (mPlamforms == null || mPlamforms.size() == 0) {
            try {
                throw new Exception("请设置分享平台");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        for (T t : mPlamforms) {
            checkPlamforVisibility(t.getPlamform());
        }
    }

    /**
     * 检测平台是否应该显示
     *
     * @param plamform 分享平台
     */
    private void checkPlamforVisibility(int plamform) {
        switch (plamform) {
            case PlamForm.WX_MOUMENT:
                mTvWxMoment.setVisibility(View.VISIBLE);
                break;
            case PlamForm.WX_CHAT:
                mTvWxChat.setVisibility(View.VISIBLE);
                break;
            case PlamForm.SINA:
                mTvWeiBo.setVisibility(View.VISIBLE);
                break;
            case PlamForm.QQ:
                mTvQQ.setVisibility(View.VISIBLE);
                break;
            case PlamForm.QQZOOM:
                mTvQQZoom.setVisibility(View.VISIBLE);
                break;

        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (mPlamFormClickListener == null) {
            new IllegalArgumentException("mPlamFormClickListener can not be null");
            return;
        }
        //平台类型
        int plamformType = -1;
        int i = v.getId();
        if (i == R.id.btn_share_wechat_moments) {
            plamformType = PlamForm.WX_MOUMENT;
//            BuriedUtil.getInstant().click_sharecircle();

        } else if (i == R.id.btn_share_wechat) {
            plamformType = PlamForm.WX_CHAT;
//            BuriedUtil.getInstant().click_sharewechat();
        } else if (i == R.id.btn_share_weibo) {
            plamformType = PlamForm.SINA;
//            BuriedUtil.getInstant().click_shareweibo();

        } else if (i == R.id.btn_share_qq) {
            plamformType = PlamForm.QQ;
//            BuriedUtil.getInstant().click_shareqq();

        } else if (i == R.id.btn_share_qqzoom) {
            plamformType = PlamForm.QQZOOM;
//            BuriedUtil.getInstant().click_shareqzone();

        } else if (i == R.id.ll_deleteact) {
            plamformType = PlamForm.DELETEACT;
        } else if (i == R.id.ll_jubao) {
            plamformType = PlamForm.JUBAO;
            dismiss();
        } else if (i == R.id.ll_unlike) {
            plamformType = PlamForm.UNLIKE;
            dismiss();
        } else if (i == R.id.ll_jj) {
            plamformType = PlamForm.JJ;
            dismiss();
        } else if (i == R.id.ll_bj) {

            SoulPermission.getInstance().checkAndRequestPermissions(Permissions.build(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), new CheckRequestPermissionsListener() {
                @Override
                public void onAllPermissionOk(Permission[] allPermissions) {
                    mPlamFormClickListener.onPlamFormClick(v, PlamForm.BJ);
                    dismiss();
                }

                @Override
                public void onPermissionDenied(Permission[] refusedPermissions) {
                    //去设置页
                    new AlertDialog(context).builder()
                            .setTitle("提示")
                            .setMsg("您禁止了存储权限,无法使用编辑功能请到设置中心打开")
                            .setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SoulPermission.getInstance().goPermissionSettings();
                        }
                    }).show();

                }
            });

            return;

        } else if (i == R.id.ll_sc) {
            plamformType = PlamForm.SC;
            dismiss();
        } else if (i == R.id.ll_pb) {
            plamformType = PlamForm.PB;
            dismiss();
        } else if (i == R.id.btn_haibao) {
            plamformType = PlamForm.HAIBAO;
        } else if (i == R.id.btn_copy) {
            plamformType = PlamForm.COPY;
//            BuriedUtil.getInstant().click_sharecopy();

        } else if (i == R.id.tvclose) {
            dismiss();
        }
        mPlamFormClickListener.onPlamFormClick(v, plamformType);
    }


    /**
     * 点击分享平台回调
     */
    public interface OnPlamFormClickListener {

        /**
         * @param view     点击的View
         * @param plamForm 点击的平台
         */
        void onPlamFormClick(View view, int plamForm);
    }

    private int is_good = 2;

    /**
     * 加精状态
     */
    public void goodJJ(int is_good) {
        this.is_good = is_good;
    }
}
