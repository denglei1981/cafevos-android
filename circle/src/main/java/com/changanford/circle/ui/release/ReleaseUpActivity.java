package com.changanford.circle.ui.release;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.changanford.circle.ui.release.widget.OpenQuessionChoolse;
import com.changanford.common.basic.BaseActivity;
import com.changanford.common.basic.BaseApplication;
import com.changanford.common.bean.QueryDetail;
import com.changanford.common.bean.QueryInfo;
import com.changanford.common.bean.STSBean;
import com.changanford.common.net.CommonResponse;
import com.changanford.common.router.path.ARouterMyPath;
import com.changanford.common.ui.dialog.AlertDialog;
import com.changanford.common.ui.dialog.LoadDialog;
import com.changanford.common.util.AliYunOssUploadOrDownFileConfig;
import com.changanford.common.util.AppUtils;
import com.changanford.common.util.FileUtils;
import com.changanford.common.util.JumpUtils;
import com.changanford.common.util.PictureUtils;
import com.changanford.common.util.TimeUtils;
import com.changanford.common.utilext.GlideUtils;
import com.changanford.circle.R;
import com.changanford.circle.databinding.ReleaseupActivityBinding;
import com.changanford.circle.ui.release.utils.ParamsUtils;
import com.changanford.circle.ui.release.widget.OpenCarcme;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.tools.DoubleUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.scwang.smart.refresh.layout.util.SmartUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * 发布调查投票
 */
@Route(path = "/circle/ReleaseUpActivity")
public class ReleaseUpActivity extends BaseActivity<ReleaseupActivityBinding, ReleaseUpActivityViewmodule> {

    private static final String TAG = "ReleaseUpActivity";
    ReleaseUpDragAdapter releaseUpDragAdapter;
    public static final int ADDQUESSION = 1854;
    public static final int QUESSIONWENBEN = 1855;
    public static final int UPDATEQUESS = 1856;
    public static final int UPDATEWENBEN = 1857;
    List<QueryInfo.QuessionBean> list = new ArrayList<>();
    ;
    QueryInfo queryInfo = new QueryInfo();
    private OpenCarcme openCarcme;
    Bundle bundle;
    int queryId;
    private TimePickerView pvTime;
    private String coverImgUrl;
    private String chooseType = "";

    @Override
    public void initView() {
        AppUtils.setStatusBarHeight(binding.title.barTitleView, this);
        bundle = getIntent().getExtras();
        if (bundle != null) {
            queryId = bundle.getInt("queryId");
        }
        queryInfo.setQuestionList(list);
        binding.title.barImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DoubleUtils.isFastDoubleClick()) {
                    new AlertDialog(ReleaseUpActivity.this).builder().setGone().setTitle("提示").setMsg("你正在编辑调查投票是否确认离开").setNegativeButton("取消", null).setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    }).show();
                }
            }
        });
        binding.rlFmimg.setOnClickListener(v -> {
            hideInput();
            openxiangche();
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
        binding.etShuoming.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.tvNumshuoming.setText(s.length()+"/1000");
            }
        });
        binding.title.barTvTitle.setText("发问卷");
        binding.title.barTvOther.setVisibility(View.VISIBLE);
        binding.title.barTvOther.setText("发布");
        binding.title.barTvOther.setTextColor(getResources().getColor(R.color.white));
        binding.title.barTvOther.setBackground(getResources().getDrawable(R.drawable.bg_bt_blue_corner39));
        binding.title.barTvOther.setPadding(20,10,20,10);
        binding.title.barTvOther.setOnClickListener(v -> {
            queryInfo.setTitle(binding.etBiaoti.getText().toString().trim());
            queryInfo.setDeadlineTime(binding.tvBaomingtime.getText().toString().trim());
            isCanFB();
        });
        releaseUpDragAdapter = new ReleaseUpDragAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.quessionrec.setLayoutManager(layoutManager);
        releaseUpDragAdapter.getDraggableModule().setSwipeEnabled(false);
        releaseUpDragAdapter.getDraggableModule().setDragEnabled(true);
        releaseUpDragAdapter.getDraggableModule().setOnItemDragListener(onItemDragListener);
        releaseUpDragAdapter.getDraggableModule().setOnItemSwipeListener(onItemSwipeListener);
        releaseUpDragAdapter.getDraggableModule().getItemTouchHelperCallback().setSwipeMoveFlags(ItemTouchHelper.START | ItemTouchHelper.END);
        binding.quessionrec.setAdapter(releaseUpDragAdapter);
//时间选择器
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.set(2099, 11, 31);
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                queryInfo.setDeadlineTime(TimeUtils.MillisToStr1(date.getTime()));  //报名截止时间
                binding.tvBaomingtime.setText(TimeUtils.MillisTo_YMDHM(date.getTime()));
            }
        })

                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setTitleSize(SmartUtil.dp2px(12))//标题文字大小
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                .setSubmitColor(getResources().getColor(R.color.black))//确定按钮文字颜色
                .setCancelColor(getResources().getColor(R.color.textgray))//取消按钮文字颜色
                .setTitleBgColor(getResources().getColor(R.color.color_withe))//标题背景颜色 Night mode
                .setBgColor(Color.WHITE)//滚轮背景颜色 Night mode
                .setType(new boolean[]{true, true, true, true, true, false})
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "")//默认设置为年月日时分秒
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .build();

        binding.rltime.setOnClickListener(v -> {
            if (!DoubleUtils.isFastDoubleClick()) {
                pvTime.show();
            }
        });
        binding.checkboxCanseeresult.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    queryInfo.setCanSeeResult(true);
                }else {
                    queryInfo.setCanSeeResult(false);
                }
            }
        });
        queryInfo.setCanSeeResult(true);
    }

    private void isCanFB() {
        if (ParamsUtils.isWENQUANOK(queryInfo)) {
            UpFm(queryInfo.getCoverImgUrl()); //上传封面图
        }
    }


    private void UpFm(String fmurl) {
        LoadDialog dialog = new LoadDialog(ReleaseUpActivity.this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setLoadingText("图片上传中..");
        dialog.show();
        upImg(fmurl, dialog);
    }

    //上传封面图片
    private void upImg(String fmurl, LoadDialog dialog) {
        viewModel.GetOSS(new Function1<CommonResponse<STSBean>, Unit>() {
            @Override
            public Unit invoke(CommonResponse<STSBean> response) {
                STSBean stsBean = response.getData();
                uploadImgs(fmurl, stsBean, dialog);
                return null;
            }
        });
    }

    private void uploadImgs(String fm, STSBean stsBean, LoadDialog dialog) {
        AliYunOssUploadOrDownFileConfig.getInstance(this).initOss(stsBean.getEndpoint(), stsBean.getAccessKeyId(),
                stsBean.getAccessKeySecret(), stsBean.getSecurityToken());
        String type = fm.substring(fm.lastIndexOf(".") + 1, fm.length());
        String path = stsBean.getTempFilePath() + System.currentTimeMillis() + "." + type;
        AliYunOssUploadOrDownFileConfig.getInstance(this).uploadFile(stsBean.getBucketName(), path, fm, "", 0);
        AliYunOssUploadOrDownFileConfig.getInstance(this).setOnUploadFile(new AliYunOssUploadOrDownFileConfig.OnUploadFile() {
            @Override
            public void onUploadFileSuccess(String info) {
                dialog.dismiss();
                queryInfo.setCoverImgUrl(path);
                if (queryId != 0) {  //是修改调查
                    viewModel.UPdatQuery(queryId, queryInfo, new Function1<CommonResponse<Object>, Unit>() {
                        @Override
                        public Unit invoke(CommonResponse<Object> response) {
                            ToastUtils.s(BaseApplication.INSTANT, response.getMsg());
                            if (response.getCode() == 0) {
                                //跳转到个人中心我的发布中心
                                JumpUtils.getInstans().jump(26,"");

//                                Bundle bundle = new Bundle();
//                                bundle.putInt("jumpType", 1);
//                                ARouter.getInstance().build(ARouterMyPath.MineFollowUI).with(bundle).navigation();
                                ReleaseUpActivity.this.finish();

                            } else {
                                queryInfo.setCoverImgUrl(coverImgUrl);
                            }
                            return null;
                        }
                    });

                } else { //提交调查
                    viewModel.CommitQuery(queryInfo, new Function1<CommonResponse<Object>, Unit>() {
                        @Override
                        public Unit invoke(CommonResponse<Object> response) {
                            ToastUtils.s(BaseApplication.INSTANT, response.getMsg());
                            if (response.getCode() == 0) {
                                //跳转到个人中心我的发布中心
                                Bundle bundle = new Bundle();
                                bundle.putInt("jumpType", 1);
                                ARouter.getInstance().build(ARouterMyPath.MineFollowUI).with(bundle).navigation();
                                ReleaseUpActivity.this.finish();
                            } else {
                                queryInfo.setCoverImgUrl(coverImgUrl);
                            }
                            return null;
                        }
                    });
                }

            }

            @Override
            public void onUploadFileFailed(String errCode) {
                dialog.dismiss();
                ToastUtils.s(BaseApplication.INSTANT, "图片上传失败，请重新上传");
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

    /**
     * 直接打开相册
     */
    private void openxiangche() {
        if (!DoubleUtils.isFastDoubleClick()) {

            openCarcme = new OpenCarcme(ReleaseUpActivity.this, new OpenCarcme.OpenCarcmeCallBack() {
                @Override
                public void onPicBack() {
                    PictureUtils.openGarlly(ReleaseUpActivity.this, 1, new OnResultCallbackListener<LocalMedia>() {

                        @Override
                        public void onResult(List<LocalMedia> result) {
                            for (LocalMedia media : result) {
                                queryInfo.setCoverImgUrl(AppUtils.getFinallyPath(media));
                                coverImgUrl = AppUtils.getFinallyPath(media);
                                GlideUtils.INSTANCE.loadRoundLocal(AppUtils.getFinallyPath(media), binding.ivFengmian, 5);

                            }
                        }

                        @Override
                        public void onCancel() {

                        }
                    }, 75, 37);
                }

                @Override
                public void onCarcme() {
                    PictureUtils.opencarcme(ReleaseUpActivity.this, new OnResultCallbackListener<LocalMedia>() {
                        @Override
                        public void onResult(List<LocalMedia> result) {
                            // 结果回调
                            if (result.size() > 0) {

                                for (LocalMedia media : result) {
                                    queryInfo.setCoverImgUrl(AppUtils.getFinallyPath(media));
                                    coverImgUrl = AppUtils.getFinallyPath(media);
                                    GlideUtils.INSTANCE.loadRoundLocal(AppUtils.getFinallyPath(media), binding.ivFengmian, 5);
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


    }

    private void initdetail(QueryDetail queryDetail) {
        if (!TextUtils.isEmpty(queryDetail.getQueryDetail().getCoverImgUrl())) {
            downloadFM(GlideUtils.INSTANCE.handleImgUrl(queryDetail.getQueryDetail().getCoverImgUrl()));  //下载封面
        }
        binding.etBiaoti.setText(queryDetail.getQueryDetail().getQueryName());
        queryInfo.setTitle(queryDetail.getQueryDetail().getQueryName());
        binding.tvBaomingtime.setText(TimeUtils.MillisToDayStr(Long.parseLong(queryDetail.getQueryDetail().getDeadlineTime())));
        queryInfo.setDeadlineTime(queryDetail.getQueryDetail().getDeadlineTime());
        if (queryDetail.getQueryDetail().getQuestions().size() > 0) {

            for (int i = 0; i < queryDetail.getQueryDetail().getQuestions().size(); i++) {
                QueryDetail.QueryDetailBean.QuestionsBean queryquestionsBean = queryDetail.getQueryDetail().getQuestions().get(i);
                QueryInfo.QuessionBean quessionBean = new QueryInfo.QuessionBean();
                quessionBean.setIsQuestionNecessary(queryquestionsBean.getIsQuestionNecessary());
                quessionBean.setQuestionImgUrl(queryquestionsBean.getQuestionImgUrl());
                quessionBean.setQuestionInfo(queryquestionsBean.getQuestionInfo());
                quessionBean.setQuestionType(queryquestionsBean.getQuestionType());
                List<QueryInfo.QuessionBean.OptionBean> mlist = new ArrayList<>();
                if (queryquestionsBean.getOptionList() != null) {
                    for (int j = 0; j < queryquestionsBean.getOptionList().size(); j++) {
                        QueryDetail.QueryDetailBean.QuestionsBean.OptionListBean optionListBean = queryquestionsBean.getOptionList().get(j);
                        QueryInfo.QuessionBean.OptionBean optionBean = new QueryInfo.QuessionBean.OptionBean("");
                        optionBean.setOptionImgUrl(optionListBean.getOptionImgUrl());
                        optionBean.setOptionName(optionListBean.getOptionName());
                        mlist.add(optionBean);
                    }
                    quessionBean.setOptionList(mlist);
                }
                list.add(quessionBean);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    releaseUpDragAdapter.setList(list);

                }
            });

        }
    }

    public void downloadFM(String url) {
        Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> e) throws Exception {
                //通过gilde下载得到file文件,这里需要注意android.permission.INTERNET权限
                e.onNext(Glide.with(ReleaseUpActivity.this)
                        .load(url)
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get());
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {

                        //获取到下载得到的图片，进行本地保存
                        File pictureFolder = Environment.getExternalStorageDirectory();
                        //第二个参数为你想要保存的目录名称
                        File appDir = new File(pictureFolder, "Uni");
                        if (!appDir.exists()) {
                            appDir.mkdirs();
                        }
                        String fileName = System.currentTimeMillis() + ".jpg";
                        File destFile = new File(appDir, fileName);
                        //把gilde下载得到图片复制到定义好的目录中去
                        copy(file, destFile);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GlideUtils.INSTANCE.loadRoundLocal(destFile.getPath(), binding.ivFengmian, 5);
                                queryInfo.setCoverImgUrl(destFile.getPath());
                            }
                        });

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            FileUtils.deleteAllInDir(Environment.getExternalStorageDirectory().getPath() + "/Uni");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath() + "/Uni"))));
        } catch (Exception e) {

        }
    }

    /**
     * 复制文件
     *
     * @param source 输入文件
     * @param target 输出文件
     */
    public void copy(File source, File target) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initData() {

        viewModel.queryDetail.observe(this, new Observer<QueryDetail>() {
            @Override
            public void onChanged(QueryDetail queryDetail) {
                initdetail(queryDetail);
            }
        });
        binding.llAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideInput();
                if (!DoubleUtils.isFastDoubleClick()) {
                    OpenQuessionChoolse openQuessionChoolse = new OpenQuessionChoolse(ReleaseUpActivity.this, new OpenQuessionChoolse.OpenQuessionChoolseCallBack() {
                        @Override
                        public void OnItemClick(int Choolsetype) {
                            Intent intent;
                            switch (Choolsetype) {
                                case 1:  //单选
                                    intent = new Intent(ReleaseUpActivity.this, PerfectQuessionActivity.class);
                                    intent.putExtra("quessionType", 0);
                                    startActivityForResult(intent, ADDQUESSION);
                                    chooseType = "单选";
                                    break;
                                case 2:  //多选
                                    intent = new Intent(ReleaseUpActivity.this, PerfectQuessionActivity.class);
                                    intent.putExtra("quessionType", 1);
                                    startActivityForResult(intent, ADDQUESSION);
                                    chooseType = "多选";
                                    break;
                                case 3:  //文本
                                    intent = new Intent(ReleaseUpActivity.this, PerfectQuessionWBActivity.class);
                                    intent.putExtra("quessionType", 2);
                                    startActivityForResult(intent, QUESSIONWENBEN);
                                    chooseType = "文本";
                                    break;
                            }

                        }
                    });
                    openQuessionChoolse.showPopupWindow();
                }

            }
        });

        releaseUpDragAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                for (int i = 0; i < list.size(); i++) {
                    if (i == position && !list.get(i).isSeleted()) {
                        list.get(i).setSeleted(true);
                    } else {
                        list.get(i).setSeleted(false);
                    }
                }
                releaseUpDragAdapter.notifyDataSetChanged();
            }
        });

        releaseUpDragAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.iv_close) {
                    queryInfo.getQuestionList().remove(position);
                    releaseUpDragAdapter.remove(position);
                }else if (view.getId() == R.id.iv_edit){
                    if (releaseUpDragAdapter.getItem(position).getQuestionType() == 0) {

                        Intent intent = new Intent(ReleaseUpActivity.this, PerfectQuessionActivity.class);
                        intent.putExtra("quessionBean", releaseUpDragAdapter.getItem(position));
                        intent.putExtra("position", position);
                        intent.putExtra("queryId", queryId);
                        startActivityForResult(intent, UPDATEQUESS);
                    } else if (releaseUpDragAdapter.getItem(position).getQuestionType() == 1) {
                        Intent intent = new Intent(ReleaseUpActivity.this, PerfectQuessionActivity.class);
                        intent.putExtra("quessionBean", releaseUpDragAdapter.getItem(position));
                        intent.putExtra("position", position);
                        intent.putExtra("queryId", queryId);
                        startActivityForResult(intent, UPDATEQUESS);
                    } else if (releaseUpDragAdapter.getItem(position).getQuestionType() == 2) {
                        Intent intent = new Intent(ReleaseUpActivity.this, PerfectQuessionWBActivity.class);
                        intent.putExtra("quessionBean", releaseUpDragAdapter.getItem(position));
                        intent.putExtra("position", position);
                        intent.putExtra("queryId", queryId);
                        startActivityForResult(intent, UPDATEWENBEN);
                    }
//                    releaseUpDragAdapter.getRecyclerView().getChildAt(position).callOnClick();
                }else if (view.getId() == R.id.iv_up){
                    if (position!=0){
                        Collections.swap(list,position-1,position);
                        releaseUpDragAdapter.setList(list);
                        releaseUpDragAdapter.notifyDataSetChanged();

                    }
                }else if (view.getId() == R.id.iv_down){
                    if (position!=list.size()-1){
                        Collections.swap(list,position,position+1);
                        releaseUpDragAdapter.setList(list);
                        releaseUpDragAdapter.notifyDataSetChanged();

                    }
                }else if (view.getId() == R.id.bglayout||view.getId() ==R.id.quessionrec||view.getId() ==R.id.inputed){
                    for (int i = 0; i < list.size(); i++) {
                        if (i == position && !list.get(i).isSeleted()) {
                            list.get(i).setSeleted(true);
                        } else {
                            list.get(i).setSeleted(false);
                        }
                    }
                    releaseUpDragAdapter.notifyDataSetChanged();
                }
            }
        });
        if (queryId != 0) {
            viewModel.Querydetail(queryId);
        }
    }

    OnItemDragListener onItemDragListener = new OnItemDragListener() {
        @Override
        public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
            Log.d(TAG, "drag start");
            final BaseViewHolder holder = ((BaseViewHolder) viewHolder);

            // 开始时，item背景色变化，demo这里使用了一个动画渐变，使得自然
            int startColor = Color.WHITE;
            int endColor = Color.rgb(245, 245, 245);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ValueAnimator v = ValueAnimator.ofArgb(startColor, endColor);
                v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        holder.itemView.setBackgroundColor((int) animation.getAnimatedValue());
                    }
                });
                v.setDuration(300);
                v.start();
            }
        }

        @Override
        public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {

        }

        @Override
        public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
            Log.d(TAG, "drag end");
            final BaseViewHolder holder = ((BaseViewHolder) viewHolder);
            // 结束时，item背景色变化，demo这里使用了一个动画渐变，使得自然
            int startColor = Color.rgb(245, 245, 245);
            int endColor = Color.WHITE;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ValueAnimator v = ValueAnimator.ofArgb(startColor, endColor);
                v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        holder.itemView.setBackgroundColor((int) animation.getAnimatedValue());
                    }
                });
                v.setDuration(300);
                v.start();
            }
        }
    };

    // 侧滑监听
    OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
        @Override
        public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
            Log.d(TAG, "view swiped start: " + pos);
            BaseViewHolder holder = ((BaseViewHolder) viewHolder);
        }

        @Override
        public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
            Log.d(TAG, "View reset: " + pos);
            BaseViewHolder holder = ((BaseViewHolder) viewHolder);
        }

        @Override
        public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
            Log.d(TAG, "View Swiped: " + pos);
        }

        @Override
        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
            canvas.drawColor(ContextCompat.getColor(ReleaseUpActivity.this, R.color.white));
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ADDQUESSION) {
            QueryInfo.QuessionBean quessionBean = (QueryInfo.QuessionBean) data.getBundleExtra("mbund").getSerializable("quession");
            list.add(quessionBean);
            releaseUpDragAdapter.addData(quessionBean);
        } else if (resultCode == RESULT_OK && requestCode == QUESSIONWENBEN) {
            QueryInfo.QuessionBean quession = (QueryInfo.QuessionBean) data.getBundleExtra("mbund").getSerializable("quession");
            list.add(quession);
            releaseUpDragAdapter.addData(quession);
        } else if (resultCode == RESULT_OK && requestCode == UPDATEQUESS) {
            QueryInfo.QuessionBean quession = (QueryInfo.QuessionBean) data.getBundleExtra("mbund").getSerializable("quession");
            int postion = data.getBundleExtra("mbund").getInt("position");
            queryInfo.getQuestionList().set(postion, quession);
            releaseUpDragAdapter.setList(queryInfo.getQuestionList());
        } else if (resultCode == RESULT_OK && requestCode == UPDATEWENBEN) {
            QueryInfo.QuessionBean quession = (QueryInfo.QuessionBean) data.getBundleExtra("mbund").getSerializable("quession");
            int postion = data.getBundleExtra("mbund").getInt("position");
            queryInfo.getQuestionList().set(postion, quession);
            releaseUpDragAdapter.setList(queryInfo.getQuestionList());
        }
    }

    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
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
}
