package com.changanford.circle.ui.release;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.changanford.circle.ui.release.widget.ActivityTypeDialog;
import com.changanford.common.basic.BaseActivity;
import com.changanford.common.basic.BaseApplication;
import com.changanford.common.bean.AttributeBean;
import com.changanford.common.bean.DtoBean;
import com.changanford.common.bean.MapReturnBean;
import com.changanford.common.bean.STSBean;
import com.changanford.common.net.CommonResponse;
import com.changanford.common.router.ARouterNavigationKt;
import com.changanford.common.router.path.ARouterCirclePath;
import com.changanford.common.ui.dialog.AlertDialog;
import com.changanford.common.ui.dialog.LoadDialog;
import com.changanford.common.util.AliYunOssUploadOrDownFileConfig;
import com.changanford.common.util.AppUtils;
import com.changanford.common.util.FileUtils;
import com.changanford.common.util.FullyGridLayoutManager;
import com.changanford.common.util.GlideEngine;
import com.changanford.common.util.JumpUtils;
import com.changanford.common.util.PictureUtil;
import com.changanford.common.util.PictureUtils;
import com.changanford.common.util.SoftHideKeyBoardUtil;
import com.changanford.common.util.TimeUtils;
import com.changanford.common.utilext.GlideUtils;
import com.changanford.circle.R;
import com.changanford.circle.databinding.ActivityReleaseBinding;
import com.changanford.circle.ui.release.utils.DragListener;
import com.changanford.circle.ui.release.utils.ParamsUtils;
import com.changanford.circle.ui.release.utils.SolveEditTextScrollClash;
import com.changanford.circle.ui.release.widget.AttrbultPop;
import com.changanford.circle.ui.release.widget.OpenCarcme;
import com.changanford.common.utilext.PermissionPopUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.broadcast.BroadcastAction;
import com.luck.picture.lib.broadcast.BroadcastManager;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnItemClickListener;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.tools.DoubleUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener;
import com.scwang.smart.refresh.layout.util.SmartUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;


/**
 * 发布线下活动
 */
@Route(path = "/circle/releasactivity")
public class ReleaseActivity extends BaseActivity<ActivityReleaseBinding, ReleaseActivityViewModel> {
    private GridPictureAdapter mAdapter;
    private int maxSelectNum = 9;
    private boolean needScaleBig = true;
    private boolean needScaleSmall = true;
    private boolean isUpward;
    public static int CompleCUTBACK = 1202;
    public static int ADDRESSBACK = 1212;
    private final static String TAG = ReleaseActivity.class.getSimpleName();
    List<LocalMedia> selectList;

    OpenCarcme openCarcme;
    private TimePickerView pvTime;
    private TimePickerView pvActTime;
    private TimePickerView pvActEndTime;
    public DtoBean dtoBean = new DtoBean();
    List<DtoBean.ContentImg> ContentImglist = new ArrayList<>();
    List<AttributeBean.AttributeCategoryVos.AttributeListBean> Attributelist = new ArrayList<>();

    List<AttributeBean.AttributeCategoryVos> attributeListBeans = new ArrayList<>();
    Date timebegin;  //活动时间
    List<String> upimgs = null;

    private String CoverImgUrl;  //上传失败需重置的封面
    private List<DtoBean.ContentImg> contentImgs; //上传失败需重置的图片集合
    private ArrayList<String> imglist;

    private String actType = "0";//活动类型 todo

    @Override
    public void initView(Bundle savedInstanceState) {
        AppUtils.setStatusBarHeight(binding.title.barTitleView, this);
        dtoBean.setContentImgList(ContentImglist);
        dtoBean.setAttributes(Attributelist);
        SoftHideKeyBoardUtil.assistActivity(this);
        binding.title.barTvTitle.setText("发布活动");
        binding.title.barTvOther.setVisibility(View.VISIBLE);
        binding.title.barTvOther.setText("发布");
        binding.title.barTvOther.setTextColor(getResources().getColor(R.color.white));
        binding.title.barTvOther.setBackground(getResources().getDrawable(R.drawable.bg_bt_blue_corner39));
        binding.title.barTvOther.setPadding(20,10,20,10);
        binding.tvBushouji.setMaxWidth(ScreenUtils.getWindowWidth(this) / 2);
        binding.title.barTvOther.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etPersonNum.getText().toString())) {
                dtoBean.setActivityTotalCount(-1);
            } else {
                dtoBean.setActivityTotalCount(Integer.valueOf(binding.etPersonNum.getText().toString()));
            }
            isCommit();
        });
        binding.etBiaoti.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.tvNum.setText(s.length() + "/20");
            }
        });
        binding.etFubiaoti.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.tvNumfu.setText(s.length() + "/100");
            }
        });
        binding.etMiaoshu.setOnTouchListener(new SolveEditTextScrollClash(binding.etMiaoshu));
        binding.etMiaoshu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.tvNumcontent.setText(s.length() + "/1000");
            }
        });
        binding.ivFengmian.setOnClickListener(v -> {
            hideInput();
            if (!DoubleUtils.isFastDoubleClick()) {

                openCarcme = new OpenCarcme(ReleaseActivity.this, new OpenCarcme.OpenCarcmeCallBack() {
                    @Override
                    public void onPicBack() {
                        PictureUtils.openGarlly(ReleaseActivity.this, 1, new OnResultCallbackListener<LocalMedia>() {

                            @Override
                            public void onResult(List<LocalMedia> result) {
                                for (LocalMedia media : result) {
                                    GlideUtils.INSTANCE.loadRoundFilePath(PictureUtil.INSTANCE.getFinallyPath(media), binding.ivFengmian);
                                    dtoBean.setCoverImgUrl(PictureUtil.INSTANCE.getFinallyPath(media));
                                    binding.tvFm.setVisibility(View.VISIBLE);
                                    binding.tvFmHint.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancel() {

                            }
                        }, 75, 37);
                    }

                    @Override
                    public void onCarcme() {
                        PictureUtils.opencarcme(ReleaseActivity.this, new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(List<LocalMedia> result) {
                                // 结果回调
                                if (result.size() > 0) {

                                    for (LocalMedia media : result) {

                                        GlideUtils.INSTANCE.loadRoundLocal(PictureUtil.INSTANCE.getFinallyPath(media), binding.ivFengmian, 5, R.mipmap.ic_def_square_img);
                                        dtoBean.setCoverImgUrl(PictureUtil.INSTANCE.getFinallyPath(media));
                                        binding.tvFm.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onCancel() {
                                // 取消
                            }
                        }, 75, 37);
                    }
                });
                openCarcme.showPopupWindow();
            }
        });

        binding.title.barImgBack.setOnClickListener(v -> {
            if (!DoubleUtils.isFastDoubleClick()) {
                new AlertDialog(this).builder().setGone().setTitle("提示").setMsg("你正在编辑活动是否确认离开").setNegativeButton("取消", null).setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).show();
            }
        });
        if (savedInstanceState != null) {
            // 被回收
        } else {
            clearCache();
        }
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this,
                4, GridLayoutManager.VERTICAL, false);
        binding.recycler.setLayoutManager(manager);

        binding.recycler.addItemDecoration(new GridSpacingItemDecoration(4,
                ScreenUtils.dip2px(this, 8), false));
        mAdapter = new GridPictureAdapter(this, onAddPicClickListener);
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList("selectorList") != null) {
            mAdapter.setList(savedInstanceState.getParcelableArrayList("selectorList"));
        }
        mAdapter.setSelectMax(maxSelectNum);
        binding.recycler.setAdapter(mAdapter);
        // 绑定拖拽事件
        mItemTouchHelper.attachToRecyclerView(binding.recycler);

        // 注册广播
        BroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver,
                BroadcastAction.ACTION_DELETE_PREVIEW_POSITION);


        initTimePick();
        initTimePick1();
        initTimePickEND();
        //报名截止时间
        binding.rlBaomingtime.setOnClickListener(v -> {
            if (!DoubleUtils.isFastDoubleClick())
                pvTime.show();
        });
        //活动时间
        binding.rlActtime.setOnClickListener(v -> {
            if (!DoubleUtils.isFastDoubleClick())
                pvActTime.show();
        });
        //活动类型
        binding.tvLeixing.setOnClickListener(v -> {
            if (!DoubleUtils.isFastDoubleClick()) {
                new ActivityTypeDialog(this, new Function1<Integer, Unit>() {
                    @Override
                    public Unit invoke(Integer integer) {
                        actType = integer + "";
                        if (integer==0) {
                            binding.tvLeixing.setText("线下活动");
                            dtoBean.setWonderfulType("1");
                            binding.rlAddress.setVisibility(View.VISIBLE);
                            binding.rlAddressLine.setVisibility(View.VISIBLE);
                        }else {
                            binding.tvLeixing.setText("线上活动");
                            dtoBean.setWonderfulType("0");
                            binding.rlAddress.setVisibility(View.GONE);
                            binding.rlAddressLine.setVisibility(View.GONE);
                        }
                        return null;
                    }
                }).setDefault(Integer.valueOf(actType)).show();
            }
        });
        //活动地点
        binding.rlAddress.setOnClickListener(v -> {

            if (!DoubleUtils.isFastDoubleClick())
                StartBaduMap();
        });

    }

    private void Showattribult(List<AttributeBean.AttributeCategoryVos> attributeListBeans) {
        if (!DoubleUtils.isFastDoubleClick()) {
            AttrbultPop a = new AttrbultPop(this, attributeListBeans, new AttrbultPop.AttrCallBack() {

                @Override
                public void data(HashMap<Integer, HashMap<Integer, AttributeBean.AttributeCategoryVos.AttributeListBean>> object) {
                    List<AttributeBean.AttributeCategoryVos.AttributeListBean> m
                            = new ArrayList<>();

                    for (Integer key : object.keySet()) {
                        for (Integer key1 :object.get(key).keySet()){
                            m.add(object.get(key).get(key1));
                        }
                    }
                    dtoBean.setAttributes(m);
                    String Showstr = "";
                    for (int i = 0; i < attributeListBeans.size(); i++) {
                        for (int j= 0; j< attributeListBeans.get(i).getAttributeList().size();j++) {
                            if (attributeListBeans.get(i).getAttributeList().get(j).getChecktype() == 1) {
                                Showstr += attributeListBeans.get(i).getAttributeList().get(j).getAttributeName() + " ";
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(Showstr)) {
                        binding.tvBushouji.setText(Showstr);
                    } else {
                        binding.tvBushouji.setText(Showstr);
                        binding.tvBushouji.setHint("");
                    }
                }
            });
            a.showPopupWindow();

        }
    }

    /**
     * 发布前判断信息是否已经完成填写
     */
    private void isCommit() {


        dtoBean.setTitle(binding.etBiaoti.getText().toString());
        dtoBean.setContent(binding.etFubiaoti.getText().toString());
        if (ParamsUtils.isactupCommit(dtoBean)) {
            LoadDialog dialog = new LoadDialog(ReleaseActivity.this);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setLoadingText("图片上传中..");
            dialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    upImg(dialog);
                }
            }).start();


        }
    }

    private void upImg(LoadDialog dialog) {
        viewModel.GetOSS(new Function1<CommonResponse<STSBean>, Unit>() {
            @Override
            public Unit invoke(CommonResponse<STSBean> response) {
                if (response.getCode() == 0) {
                    STSBean stsBean = response.getData();
                    if (imglist == null) {
                        imglist = new ArrayList<>();
                    }
                    imglist.add(dtoBean.getCoverImgUrl());
                    for (int i = 0; i < dtoBean.getContentImgList().size(); i++) {
                        imglist.add(dtoBean.getContentImgList().get(i).getContentImgUrl());
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadImgs(imglist, stsBean, 0, dialog);
                        }
                    }).start();
                } else {
                    Log.e("adasf-----", response.getMessage());
                    dialog.dismiss();
                }
                return null;
            }
        });
    }

    private void uploadImgs(List<String> upfiles, STSBean stsBean, int count, LoadDialog dialog) {
        int size = upfiles.size();
        if (upimgs == null) {
            upimgs = new ArrayList<>();
            CoverImgUrl = dtoBean.getCoverImgUrl();
        }
        AliYunOssUploadOrDownFileConfig.getInstance(this).initOss(stsBean.getEndpoint(), stsBean.getAccessKeyId(),
                stsBean.getAccessKeySecret(), stsBean.getSecurityToken());
        String type = upfiles.get(count).substring(upfiles.get(count).lastIndexOf(".") + 1, upfiles.get(count).length());
        String path = stsBean.getTempFilePath() + System.currentTimeMillis() + "." + type;
//        String uploadFilePath = stsBean.getCdn() + path;
        upimgs.add(path);
        AliYunOssUploadOrDownFileConfig.getInstance(this).uploadFile(stsBean.getBucketName(), path, upfiles.get(count), "", 0);
        AliYunOssUploadOrDownFileConfig.getInstance(this).setOnUploadFile(new AliYunOssUploadOrDownFileConfig.OnUploadFile() {
            @Override
            public void onUploadFileSuccess(String info) {
                int scount = count + 1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setTvprogress(scount + "/" + upfiles.size());
                    }
                });
                if (scount == size) {
                    Log.d("上传之后的图片集合", JSON.toJSONString(upimgs));
                    dtoBean.setCoverImgUrl(upimgs.get(0));
                    upimgs.remove(0);
                    for (int i = 0; i < upimgs.size(); i++) {
                        dtoBean.getContentImgList().get(i).setContentImgUrl(upimgs.get(i));
                    }
                    viewModel.CommitACT(dtoBean, new Function1<CommonResponse<Object>, Unit>() {
                        @Override
                        public Unit invoke(CommonResponse<Object> response) {
                            if (response.getCode() == 0) {
                                ToastUtils.s(ReleaseActivity.this, "发布成功");
                                dialog.dismiss();
                                JumpUtils.getInstans().jump(26,"");
//                                Bundle bundle = new Bundle();
//                                bundle.putInt("jumpType", 1);
//                                ARouter.getInstance().build(ARouterMyPath.MineFollowUI).with(bundle).navigation();
                                ReleaseActivity.this.finish();
                            } else {
                                dialog.dismiss();
                                dtoBean.setCoverImgUrl(CoverImgUrl);
                                contentImgs = new ArrayList<>();
                                for (int i = 0; i < mAdapter.getData().size(); i++) {
                                    DtoBean.ContentImg contentImg = new DtoBean.ContentImg();
                                    contentImg.setContentImgUrl(PictureUtil.INSTANCE.getFinallyPath(mAdapter.getData().get(i)));
                                    contentImg.setContentDesc(mAdapter.getData().get(i).getContentDesc());
                                    contentImgs.add(contentImg);
                                }
                                dtoBean.setContentImgList(contentImgs);
                                upimgs.clear();
                                imglist.clear();
                                Log.d("dtobean", JSON.toJSONString(dtoBean));
                                ToastUtils.s(ReleaseActivity.this, response.getMsg());
                            }
                            return null;
                        }
                    });
                }
                uploadImgs(upfiles, stsBean, scount, dialog);
            }

            @Override
            public void onUploadFileFailed(String errCode) {
                dialog.dismiss();
                ToastUtils.s(ReleaseActivity.this, "图片上传失败，请重新上传");
            }

            @Override
            public void onuploadFileprogress(PutObjectRequest request, long currentSize, long totalSize) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        progressDialog.setProgress((int) ((currentSize*100/totalSize)));
                    }
                });
            }

        });


    }

    private Function0<Unit> successPer(){
        startActivityForResult(new Intent(ReleaseActivity.this, MMapActivity.class), ADDRESSBACK);
        return null;
    }

    private Function0<Unit> failPer(){
        new AlertDialog(ReleaseActivity.this).builder()
                .setTitle("提示")
                .setMsg("您已禁止了定位权限，请到设置中心去打开")
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
        return null;
    }

    private void StartBaduMap() {
        Permissions permissions = Permissions.build(Manifest.permission.ACCESS_FINE_LOCATION);
        PermissionPopUtil.INSTANCE.checkPermissionAndPop(permissions, Objects.requireNonNull(successPer()), Objects.requireNonNull(failPer()));
//        SoulPermission.getInstance()
//                .checkAndRequestPermission(
//                        Manifest.permission.ACCESS_FINE_LOCATION,  //if you want do noting or no need all the callbacks you may use SimplePermissionAdapter instead
//                        new CheckRequestPermissionListener() {
//                            @Override
//                            public void onPermissionOk(Permission permission) {
//                                startActivityForResult(new Intent(ReleaseActivity.this, MMapActivity.class), ADDRESSBACK);
//                            }
//
//                            @Override
//                            public void onPermissionDenied(Permission permission) {
//                                new AlertDialog(ReleaseActivity.this).builder()
//                                        .setTitle("提示")
//                                        .setMsg("您已禁止了定位权限，请到设置中心去打开")
//                                        .setNegativeButton("取消", new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//
//                                            }
//                                        }).setPositiveButton("确定", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        SoulPermission.getInstance().goPermissionSettings();
//                                    }
//                                }).show();
//
//                            }
//                        });

    }

    /**
     * 选择报名截止时间
     */
    private void initTimePick() {
        //时间选择器
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.set(2099, 11, 31);
        //正确设置方式 原因：注意事项有说明

        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String format = sdf.format(date);
                dtoBean.setDeadLineTime(TimeUtils.MillisToStr1(date.getTime()));  //报名截止时间
                binding.tvBaomingtime.setText(format);
            }
        })

                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setTitleSize(SmartUtil.dp2px(12))//标题文字大小
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setSubmitColor(getResources().getColor(R.color.black))//确定按钮文字颜色
                .setCancelColor(getResources().getColor(R.color.textgray))//取消按钮文字颜色
                .setTitleBgColor(getResources().getColor(R.color.color_withe))//标题背景颜色 Night mode
                .setBgColor(Color.WHITE)//滚轮背景颜色 Night mode
                .setType(new boolean[]{true, true, true, true, true, false})
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "")//默认设置为年月日时分秒
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .build();

    }


    /**
     * 选择活动时间
     */
    private void initTimePick1() {
        //时间选择器
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.set(2099, 11, 31);
        //正确设置方式 原因：注意事项有说明

        pvActTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                dtoBean.setBeginTime(TimeUtils.MillisToStr1(date.getTime()));
                timebegin = date;
                pvActEndTime.show();
//                tvborthday.setText(format);
//                tvxingzuo.setText(TimeUtils.getConstellation(date));
            }
        })

                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setTitleText("开始时间")
                .setTitleSize(SmartUtil.dp2px(6))//标题文字大小
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setSubmitColor(getResources().getColor(R.color.black))//确定按钮文字颜色
                .setCancelColor(getResources().getColor(R.color.textgray))//取消按钮文字颜色
                .setTitleBgColor(getResources().getColor(R.color.color_withe))//标题背景颜色 Night mode
                .setBgColor(Color.WHITE)//滚轮背景颜色 Night mode
                .setType(new boolean[]{true, true, true, true, true, false})
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "")//默认设置为年月日时分秒
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)
                .build();

    }

    /**
     * 选择活动时间
     */
    private void initTimePickEND() {
        //时间选择器
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.set(2099, 11, 31);
        //正确设置方式 原因：注意事项有说明

        pvActEndTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {

                dtoBean.setEndTime(TimeUtils.MillisToStr1(date.getTime()));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String endtime = sdf.format(date);
                String begintime = sdf.format(timebegin);
//                try {
//                    if ( TimeUtils.StrToMillisYMD(endtime) == TimeUtils.StrToMillisYMD(begintime)){
//                        ToastUtils.s(BaseApplication.INSTANT.getApplicationContext(),"结束时间不能等于开始时间");
//                        pvActTime.show();
//                    }else if(TimeUtils.StrToMillisYMD(endtime) < TimeUtils.StrToMillisYMD(begintime)){
//                        ToastUtils.s(BaseApplication.INSTANT.getApplicationContext(),"结束时间不能小于开始时间");
//                        pvActTime.show();
//                    }else {
//                        binding.tvActtime.setText(begintime+" 至 "+endtime);
//                    }
//                }catch (Exception e){
//
//                }
                if (timebegin.getTime() > date.getTime()) {
                    ToastUtils.s(BaseApplication.INSTANT.getApplicationContext(), "结束时间不能小于开始时间");
                    pvActTime.show();
                } else {
                    binding.tvActtime.setText(begintime + " 至 " + endtime);
                }
            }
        })

                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setTitleText("结束时间")
                .setTitleSize(SmartUtil.dp2px(6))//标题文字大小
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setSubmitColor(getResources().getColor(R.color.black))//确定按钮文字颜色
                .setCancelColor(getResources().getColor(R.color.textgray))//取消按钮文字颜色
                .setTitleBgColor(getResources().getColor(R.color.color_withe))//标题背景颜色 Night mode
                .setBgColor(Color.WHITE)//滚轮背景颜色 Night mode
                .setType(new boolean[]{true, true, true, true, true, false})
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "")//默认设置为年月日时分秒
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)
                .build();

    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            if (BroadcastAction.ACTION_DELETE_PREVIEW_POSITION.equals(action)) {
                // 外部预览删除按钮回调
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int position = extras.getInt(PictureConfig.EXTRA_PREVIEW_DELETE_POSITION);
                    ToastUtils.s(getApplicationContext(), "delete image index:" + position);

                    mAdapter.remove(position);
                    mAdapter.notifyItemRemoved(position);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            BroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver,
                    BroadcastAction.ACTION_DELETE_PREVIEW_POSITION);
        }
        try {
            FileUtils.deleteAllInDir(Environment.getExternalStorageDirectory().getPath() + "/Uni");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath() + "/Uni"))));
        } catch (Exception e) {

        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null && mAdapter.getData() != null && mAdapter.getData().size() > 0) {
            outState.putParcelableArrayList("selectorList",
                    (ArrayList<? extends Parcelable>) mAdapter.getData());
        }
    }

    /**
     * 清空缓存包括裁剪、压缩、AndroidQToPath所生成的文件，注意调用时机必须是处理完本身的业务逻辑后调用；非强制性
     */
    private void clearCache() {
        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        if (PermissionChecker.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //PictureFileUtils.deleteCacheDirFile(this, PictureMimeType.ofImage());
            PictureFileUtils.deleteAllCacheDirFile(getApplicationContext());
        } else {
            PermissionChecker.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }
    }

    @Override
    public void initData() {
        viewModel.getAttributes();
        binding.rlZiliao.setOnClickListener(v -> {
            Showattribult(attributeListBeans);
        });
        mAdapter.setItemLongClickListener((holder, position, v) -> {
            //如果item不是最后一个，则执行拖拽
            needScaleBig = true;
            needScaleSmall = true;
            int size = mAdapter.getData().size();
            if (size != maxSelectNum) {
                mItemTouchHelper.startDrag(holder);
                return;
            }
            if (holder.getLayoutPosition() != size - 1) {
                mItemTouchHelper.startDrag(holder);
            }
        });

        mAdapter.setOnItemDeleteListener(position -> {
            dtoBean.getContentImgList().remove(position);
        });


        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                selectList = mAdapter.getData();
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String mimeType = media.getMimeType();
                    int mediaType = PictureMimeType.getMimeType(mimeType);
                    switch (mediaType) {
                        case PictureConfig.TYPE_VIDEO:
//                            ReleaseActivity.this.startActivity(new Intent(ReleaseActivity.this, EsayVideoEditActivity.class).putExtra(PATH, TextUtils.isEmpty(media.getAndroidQToPath()) ? media.getPath() : media.getAndroidQToPath()));
//                            startARouter(ARouterCirclePath.PictureEditAudioActivity,Bundle().apply {
//                            putString("path",selectList[0].realPath)
//                        })
                            Bundle bundle = new Bundle();
                            bundle.putString("path", selectList.get(0).getRealPath());
                            ARouterNavigationKt.startARouter(ARouterCirclePath.PictureEditAudioActivity);
                            break;
                        case PictureConfig.TYPE_AUDIO:
                            // 预览音频
                            PictureSelector.create(ReleaseActivity.this)
                                    .externalPictureAudio(PictureMimeType.isContent(media.getPath()) ? media.getAndroidQToPath() : media.getPath());
                            break;
                        default:
                            // 预览图片 可自定长按保存路径

                            List<LocalMedia> mediaList = new ArrayList<>();
                            mediaList.addAll(selectList);
//                            Intent intent = new Intent(ReleaseActivity.this, PictureeditlActivity.class);
//                            intent.putExtra("showEditType", -1);
//                            intent.putExtra("position", position);
//                            intent.putParcelableArrayListExtra("picList",
//                                    (ArrayList<? extends Parcelable>) mediaList);
//                            ReleaseActivity.this.startActivityForResult(intent, CompleCUTBACK);

                            Bundle bundle2 = new Bundle();
                            bundle2.putInt("position", position);
                            bundle2.putInt("showEditType", 1);
                            bundle2.putParcelableArrayList("picList",
                                    (ArrayList<? extends Parcelable>) mediaList);
                            ARouterNavigationKt.startARouterForResult(ReleaseActivity.this, ARouterCirclePath.PictureeditlActivity, bundle2, CompleCUTBACK);
                            break;
                    }
                }
            }
        });

        viewModel.attributeBean.observe(this, attributeBean -> {
            if (viewModel.attributeBean.getValue().getAttributesInfo().getAttributeCategoryVos() != null) {
                attributeListBeans.clear();
                attributeListBeans.addAll(viewModel.attributeBean.getValue().getAttributesInfo().getAttributeCategoryVos());
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog(this).builder().setGone().setTitle("提示").setMsg("你正在编辑活动是否确认离开").setNegativeButton("取消", null).setPositiveButton("确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            }).show();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CompleCUTBACK) {
            List<LocalMedia> retlist = (List<LocalMedia>) data.getSerializableExtra("picList");


            List<DtoBean.ContentImg> contentImgs = dtoBean.getContentImgList();
            for (int i = 0; i < retlist.size(); i++) {
                contentImgs.get(i).setContentDesc(retlist.get(i).getContentDesc());
                String path = PictureUtil.INSTANCE.getFinallyPath(retlist.get(i));
                contentImgs.get(i).setContentImgUrl(path);

            }
            dtoBean.setContentImgList(contentImgs);
            mAdapter.setList(retlist);
            mAdapter.notifyDataSetChanged();

        } else if (resultCode == RESULT_OK && requestCode == ADDRESSBACK) {
            MapReturnBean poiInfo = data.getBundleExtra("mbundaddress").getParcelable("poi");
            if (poiInfo != null && poiInfo.getPoiInfo() != null) {
                binding.tvAddress.setText(poiInfo.getPoiInfo().getAddress());
                dtoBean.setActivityAddr(poiInfo.getPoiInfo().getAddress());
                dtoBean.setCityId(poiInfo.getCid());
                dtoBean.setProvinceId(poiInfo.getSid());
                dtoBean.setTownId(poiInfo.getQid());
                dtoBean.setCityName(poiInfo.getCityName());
                dtoBean.setProvinceName(poiInfo.getPoiInfo().province);
                dtoBean.setTownName(poiInfo.getPoiInfo().area);
                dtoBean.setLongitude(poiInfo.getPoiInfo().getLocation().longitude + "");
                dtoBean.setLatitude(poiInfo.getPoiInfo().getLocation().latitude + "");

//                dtoBean.setCityId(poiInfo.get);
            }
        }
    }

    private GridPictureAdapter.onAddPicClickListener onAddPicClickListener = new GridPictureAdapter.onAddPicClickListener() {

        @Override
        public void onAddPicClick() {
// 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(ReleaseActivity.this)
                    .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                    .theme(R.style.picture_WeChat_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
                    .isWeChatStyle(true)// 是否开启微信图片选择风格
                    .isUseCustomCamera(false)// 是否使用自定义相机
                    .isPageStrategy(true)// 是否开启分页策略 & 每页多少条；默认开启
                    .isWithVideoImage(true)// 图片和视频是否可以同选,只在ofAll模式下有效
                    .isMaxSelectEnabledMask(true)// 选择数到了最大阀值列表是否启用蒙层效果
                    //.isAutomaticTitleRecyclerTop(false)// 连续点击标题栏RecyclerView是否自动回到顶部,默认true
                    //.loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                    //.setOutputCameraPath()// 自定义相机输出目录，只针对Android Q以下，例如 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +  File.separator + "Camera" + File.separator;
                    //.setButtonFeatures(CustomCameraView.BUTTON_STATE_BOTH)// 设置自定义相机按钮状态
                    .maxSelectNum(maxSelectNum)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .maxVideoSelectNum(1) // 视频最大选择数量
                    //.minVideoSelectNum(1)// 视频最小选择数量
                    //.closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q())// 关闭在AndroidQ下获取图片或视频宽高相反自动转换
                    .imageSpanCount(4)// 每行显示个数
                    .isReturnEmpty(false)// 未选择数据时点击按钮是否可以返回
                    //.isAndroidQTransform(false)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                    .isOriginalImageControl(false)// 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                    //.bindCustomPlayVideoCallback(new MyVideoSelectedPlayCallback(getContext()))// 自定义视频播放回调控制，用户可以使用自己的视频播放界面
                    //.bindCustomCameraInterfaceListener(new MyCustomCameraInterfaceListener())// 提供给用户的一些额外的自定义操作回调
                    //.cameraFileName(System.currentTimeMillis() +".jpg")    // 重命名拍照文件名、如果是相册拍照则内部会自动拼上当前时间戳防止重复，注意这个只在使用相机时可以使用，如果使用相机又开启了压缩或裁剪 需要配合压缩和裁剪文件名api
                    //.renameCompressFile(System.currentTimeMillis() +".jpg")// 重命名压缩文件名、 如果是多张压缩则内部会自动拼上当前时间戳防止重复
                    //.renameCropFileName(System.currentTimeMillis() + ".jpg")// 重命名裁剪文件名、 如果是多张裁剪则内部会自动拼上当前时间戳防止重复
                    .selectionMode(
                            PictureConfig.MULTIPLE)// 多选 or 单选
                    .isSingleDirectReturn(true)// 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                    .isPreviewImage(true)// 是否可预览图片
                    .isPreviewVideo(false)// 是否可预览视频
                    //.querySpecifiedFormatSuffix(PictureMimeType.ofJPEG())// 查询指定后缀格式资源
                    .isEnablePreviewAudio(false) // 是否可播放音频
                    .isCamera(true)// 是否显示拍照按钮
                    //.isMultipleSkipCrop(false)// 多图裁剪时是否支持跳过，默认支持
                    //.isMultipleRecyclerAnimation(false)// 多图裁剪底部列表显示动画效果
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg,Android Q使用PictureMimeType.PNG_Q
                    .isEnableCrop(false)// 是否裁剪
                    //.basicUCropConfig()//对外提供所有UCropOptions参数配制，但如果PictureSelector原本支持设置的还是会使用原有的设置
                    .isCompress(false)// 是否压缩
                    .compressQuality(90)// 图片压缩后输出质量 0~ 100
                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
                    //.queryMaxFileSize(10)// 只查多少M以内的图片、视频、音频  单位M
                    //.compressSavePath(getPath())//压缩图片保存地址
                    //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效 注：已废弃
                    //.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 注：已废弃
//                    .withAspectRatio(aspect_ratio_x, aspect_ratio_y)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
//                    .hideBottomControls(!cb_hide.isChecked())// 是否显示uCrop工具栏，默认不显示
                    .isGif(false)// 是否显示gif图片
                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                    .circleDimmedLayer(false)// 是否圆形裁剪
                    //.setCircleDimmedColor(ContextCompat.getColor(getContext(), R.color.app_color_white))// 设置圆形裁剪背景色值
                    //.setCircleDimmedBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_white))// 设置圆形裁剪边框色值
                    //.setCircleStrokeWidth(3)// 设置圆形裁剪边框粗细
                    .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                    .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                    .isOpenClickSound(true)// 是否开启点击声音
                    .selectionData(mAdapter.getData())// 是否传入已选图片
                    .isDragFrame(true)// 是否可拖动裁剪框(固定)
                    //.videoMinSecond(10)// 查询多少秒以内的视频
                    //.videoMaxSecond(15)// 查询多少秒以内的视频
                    //.recordVideoSecond(10)//录制视频秒数 默认60s
                    //.isPreviewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                    //.cropCompressQuality(90)// 注：已废弃 改用cutOutQuality()
                    .cutOutQuality(90)// 裁剪输出质量 默认100
                    .minimumCompressSize(1024)// 小于100kb的图片不压缩
                    //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                    //.cropImageWideHigh()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                    //.rotateEnabled(false) // 裁剪是否可旋转图片
                    //.scaleEnabled(false)// 裁剪是否可放大缩小图片
                    //.videoQuality()// 视频录制质量 0 or 1
                    //.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                    .forResult(new MyResultCallback(mAdapter));
        }
    };


    DragListener mDragListener = new DragListener() {
        @Override
        public void deleteState(boolean isDelete) {
            if (isDelete) {
//                tvDeleteText.setText(getString(R.string.app_let_go_drag_delete));
//                tvDeleteText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_let_go_delete, 0, 0);
            } else {
//                tvDeleteText.setText(getString(R.string.app_drag_delete));
//                tvDeleteText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.picture_icon_delete, 0, 0);
            }

        }

        @Override
        public void dragState(boolean isStart) {
//            int visibility = tvDeleteText.getVisibility();
            if (isStart) {
//                if (visibility == View.GONE) {
////                    tvDeleteText.animate().alpha(1).setDuration(300).setInterpolator(new AccelerateInterpolator());
////                    tvDeleteText.setVisibility(View.VISIBLE);
//                }
            } else {
//                if (visibility == View.VISIBLE) {
////                    tvDeleteText.animate().alpha(0).setDuration(300).setInterpolator(new AccelerateInterpolator());
////                    tvDeleteText.setVisibility(View.GONE);
//                }
            }
        }
    };

    ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != GridPictureAdapter.TYPE_CAMERA) {
                viewHolder.itemView.setAlpha(0.7f);
            }
            return makeMovementFlags(ItemTouchHelper.DOWN | ItemTouchHelper.UP
                    | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            //得到item原来的position
            try {
                int fromPosition = viewHolder.getAdapterPosition();
                //得到目标position
                int toPosition = target.getAdapterPosition();
                int itemViewType = target.getItemViewType();
                if (itemViewType != GridPictureAdapter.TYPE_CAMERA) {
                    if (fromPosition < toPosition) {
                        for (int i = fromPosition; i < toPosition; i++) {
                            Collections.swap(mAdapter.getData(), i, i + 1);
                        }
                    } else {
                        for (int i = fromPosition; i > toPosition; i--) {
                            Collections.swap(mAdapter.getData(), i, i - 1);
                        }
                    }
                    mAdapter.notifyItemMoved(fromPosition, toPosition);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != GridPictureAdapter.TYPE_CAMERA) {
                if (null == mDragListener) {
                    return;
                }
                if (needScaleBig) {
                    //如果需要执行放大动画
                    viewHolder.itemView.animate().scaleXBy(0.1f).scaleYBy(0.1f).setDuration(100);
                    //执行完成放大动画,标记改掉
                    needScaleBig = false;
                    //默认不需要执行缩小动画，当执行完成放大 并且松手后才允许执行
                    needScaleSmall = false;
                }
//                int sh = recyclerView.getHeight() + tvDeleteText.getHeight();
//                int ry = tvDeleteText.getBottom() - sh;
//                if (dY >= ry) {
//                    //拖到删除处
//                    mDragListener.deleteState(true);
//                    if (isUpward) {
//                        //在删除处放手，则删除item
//                        viewHolder.itemView.setVisibility(View.INVISIBLE);
//                        mAdapter.delete(viewHolder.getAdapterPosition());
//                        resetState();
//                        return;
//                    }
//                } else {//没有到删除处
                if (View.INVISIBLE == viewHolder.itemView.getVisibility()) {
                    //如果viewHolder不可见，则表示用户放手，重置删除区域状态
                    mDragListener.dragState(false);
                }
                if (needScaleSmall) {//需要松手后才能执行
                    viewHolder.itemView.animate().scaleXBy(1f).scaleYBy(1f).setDuration(100);
                }
                mDragListener.deleteState(false);
//                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        @Override
        public void onSelectedChanged(@androidx.annotation.Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            int itemViewType = viewHolder != null ? viewHolder.getItemViewType() : GridPictureAdapter.TYPE_CAMERA;
            if (itemViewType != GridPictureAdapter.TYPE_CAMERA) {
                if (ItemTouchHelper.ACTION_STATE_DRAG == actionState && mDragListener != null) {
                    mDragListener.dragState(true);
                }
                super.onSelectedChanged(viewHolder, actionState);
            }
        }

        @Override
        public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
            needScaleSmall = true;
            isUpward = true;
            return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != GridPictureAdapter.TYPE_CAMERA) {
                viewHolder.itemView.setAlpha(1.0f);
                super.clearView(recyclerView, viewHolder);
                mAdapter.notifyDataSetChanged();
                resetState();
            }
        }
    });

    /**
     * 重置
     */
    private void resetState() {
        if (mDragListener != null) {
            mDragListener.deleteState(false);
            mDragListener.dragState(false);
        }
        isUpward = false;
    }

    @Override
    public void initView() {

    }


    /**
     * 返回结果回调
     */
    public class MyResultCallback implements OnResultCallbackListener<LocalMedia> {
        private GridPictureAdapter mAdapterWeakReference;

        public MyResultCallback(GridPictureAdapter adapter) {
            super();
            this.mAdapterWeakReference = adapter;
        }

        @Override
        public void onResult(List<LocalMedia> result) {
            for (LocalMedia media : result) {
                Log.i(TAG, "是否压缩:" + media.isCompressed());
                Log.i(TAG, "压缩:" + media.getCompressPath());
                Log.i(TAG, "原图:" + media.getPath());
                Log.i(TAG, "是否裁剪:" + media.isCut());
                Log.i(TAG, "裁剪:" + media.getCutPath());
                Log.i(TAG, "是否开启原图:" + media.isOriginal());
                Log.i(TAG, "原图路径:" + media.getOriginalPath());
                Log.i(TAG, "Android Q 特有Path:" + media.getAndroidQToPath());
                Log.i(TAG, "宽高: " + media.getWidth() + "x" + media.getHeight());
                Log.i(TAG, "Size: " + media.getSize());
                // TODO 可以通过PictureSelectorExternalUtils.getExifInterface();方法获取一些额外的资源信息，如旋转角度、经纬度等信息
            }
            if (mAdapterWeakReference != null) {
                mAdapterWeakReference.setList(result);
                List<DtoBean.ContentImg> contentImgs = new ArrayList<>();
                for (int i = 0; i < result.size(); i++) {
                    DtoBean.ContentImg contentImg = new DtoBean.ContentImg();
                    LocalMedia media = result.get(i);

                    contentImg.setContentImgUrl(PictureUtil.INSTANCE.getFinallyPath(media));
                    contentImgs.add(contentImg);
                }
                dtoBean.setContentImgList(contentImgs);
                mAdapterWeakReference.notifyDataSetChanged();

                List<LocalMedia> mediaList = new ArrayList<>();
                mediaList.addAll(mAdapterWeakReference.getData());
//                Intent intent = new Intent(ReleaseActivity.this, PictureeditlActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("position", 0);
                bundle.putInt("showEditType", 1);
                bundle.putParcelableArrayList("picList",
                        (ArrayList<? extends Parcelable>) mediaList);
                ARouterNavigationKt.startARouterForResult(ReleaseActivity.this, ARouterCirclePath.PictureeditlActivity, bundle, CompleCUTBACK);
//                ReleaseActivity.this.startActivityForResult(intent, CompleCUTBACK);
            }
        }

        @Override
        public void onCancel() {
            Log.i(TAG, "PictureSelector Cancel");
        }
    }

    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


}
