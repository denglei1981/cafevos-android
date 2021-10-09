package com.changanford.circle.ui.release;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.changanford.common.basic.BaseActivity;
import com.changanford.common.bean.QueryInfo;
import com.changanford.common.bean.STSBean;
import com.changanford.common.net.CommonResponse;
import com.changanford.common.ui.dialog.LoadDialog;
import com.changanford.common.util.AliYunOssUploadOrDownFileConfig;
import com.changanford.common.util.AppUtils;
import com.changanford.common.util.PictureUtils;
import com.changanford.common.utilext.GlideUtils;
import com.changanford.circle.R;
import com.changanford.circle.databinding.PerfectquessionActivityBinding;
import com.changanford.circle.ui.release.utils.ParamsUtils;
import com.changanford.circle.ui.release.widget.OpenCarcme;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.tools.DoubleUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * 单选多选完善问题
 */
@Route(path = "/circle/PerfectQuessionActivity")
public class PerfectQuessionActivity extends BaseActivity<PerfectquessionActivityBinding, PerfectQuessionActivityViewModule> {

    private static final String TAG = "PerfectQuessionActivity";
    PrefectQuessionDragAdapter prefectQuessionDragAdapter;
    private View HeadView;
    EditText ettimu;
    ImageView ivshanchu;
    private ImageView iv_timuimg;
    private ArrayList<QueryInfo.QuessionBean.OptionBean> list;
    QueryInfo.QuessionBean quessionBean = new QueryInfo.QuessionBean();
    List<QueryInfo.QuessionBean.OptionBean> optionBeans = new ArrayList<>();
    int quessionType;//0单选，1多选
    private OpenCarcme openCarcme;
    private ArrayList<String> upimgs;
    int position;
    int queryId;

    @Override
    public void initView() {
        AppUtils.setStatusBarHeight(binding.title.barTitleView, this);
        if (getIntent().getSerializableExtra("quessionBean") != null) {
            queryId = getIntent().getIntExtra("queryId", 0);
            position = getIntent().getIntExtra("position", 0);
            quessionBean = (QueryInfo.QuessionBean) getIntent().getSerializableExtra("quessionBean");
        } else {
            quessionType = getIntent().getIntExtra("quessionType", 0);
            quessionBean.setOptionList(optionBeans);
            quessionBean.setQuestionType(quessionType);
            quessionBean.setIsQuestionNecessary(0);
        }

        binding.addchoice.mcb.setOnClickListener(v -> {//是否必填
            if (binding.addchoice.mcb.isChecked()){
                quessionBean.setIsQuestionNecessary(0);
            }else {
                quessionBean.setIsQuestionNecessary(1);
            }
        });
        binding.addchoice.multeorsignle.setOnClickListener(v->{//切换单选多选
            if (quessionType == 0){//是单选，点击切换成多选
                quessionType = 1;
            }else {//是多选，点击切换成单选
                quessionType = 0;
            }
            quessionBean.setQuestionType(quessionType);
        });

        binding.title.barImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (quessionType ==0){
            binding.title.barTvTitle.setText("单选题");
            binding.addchoice.multeorsignletxt.setText("切换至多选题");
        }else {
            binding.title.barTvTitle.setText("多选题");
            binding.addchoice.multeorsignletxt.setText("切换至单选题");
        }
        prefectQuessionDragAdapter = new PrefectQuessionDragAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.prefectquessrec.setLayoutManager(layoutManager);
        prefectQuessionDragAdapter.getDraggableModule().setSwipeEnabled(false);
        prefectQuessionDragAdapter.getDraggableModule().setDragEnabled(true);
        prefectQuessionDragAdapter.getDraggableModule().setOnItemDragListener(onItemDragListener);
        prefectQuessionDragAdapter.getDraggableModule().setOnItemSwipeListener(onItemSwipeListener);
        prefectQuessionDragAdapter.getDraggableModule().getItemTouchHelperCallback().setSwipeMoveFlags(ItemTouchHelper.START | ItemTouchHelper.END);
        HeadView = LayoutInflater.from(this).inflate(R.layout.prefectquessionrechead, null, false);
        prefectQuessionDragAdapter.addHeaderView(HeadView);
        binding.prefectquessrec.setAdapter(prefectQuessionDragAdapter);
        if (getIntent().getSerializableExtra("quessionBean") == null) {

            list = new ArrayList<>();
            list.add(new QueryInfo.QuessionBean.OptionBean(""));
            list.add(new QueryInfo.QuessionBean.OptionBean(""));
            quessionBean.getOptionList().addAll(list);
            prefectQuessionDragAdapter.addData(list);
        }
        ettimu = HeadView.findViewById(R.id.et_timu);
        ivshanchu = HeadView.findViewById(R.id.iv_shanchu);
        TextView tvnum = HeadView.findViewById(R.id.tv_num);
        iv_timuimg = HeadView.findViewById(R.id.iv_timuimg);
        prefectQuessionDragAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.iv_img) {
                    openxiangche(position);
                } else if (view.getId() == R.id.iv_delete) {
                    if (prefectQuessionDragAdapter.getData().size() == 2) {
                        return;
                    }
                    prefectQuessionDragAdapter.remove(position);
                    quessionBean.getOptionList().remove(position);
                } else if (view.getId() == R.id.iv_del) {
                    prefectQuessionDragAdapter.getItem(position).setBdoptionImgUrl("");
                    prefectQuessionDragAdapter.getItem(position).setOptionImgUrl("");
                    prefectQuessionDragAdapter.notifyDataSetChanged();
                }
            }
        });
        ivshanchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quessionBean.setQuestionImgUrl("");
                quessionBean.setBDquestionImgUrl("");
                iv_timuimg.setImageResource(R.mipmap.add_image);
                ivshanchu.setVisibility(View.INVISIBLE);
            }
        });

        ettimu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvnum.setText(s.length() + "/20");

            }
        });
        iv_timuimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openxiangche();
                hideInput();
            }
        });
        binding.commit.setOnClickListener(v -> {
            quessionBean.setQuestionInfo(ettimu.getText().toString().trim());

            if (quessionCanBack() && !DoubleUtils.isFastDoubleClick()) {
                if (!TextUtils.isEmpty(quessionBean.getQuestionImgUrl()) || quessionBean.getOptionList().size() > 0) {
                    boolean isnext = false;
                    for (int i = 0; i < quessionBean.getOptionList().size(); i++) {
                        if (!TextUtils.isEmpty(quessionBean.getOptionList().get(i).getBdoptionImgUrl())) {
                            isnext = true;
                        }
                    }
                    if (!TextUtils.isEmpty(quessionBean.getBDquestionImgUrl())) {
                        isnext = true;
                    }
                    if (isnext) {

                        LoadDialog dialog = new LoadDialog(PerfectQuessionActivity.this);
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setLoadingText("图片上传中..");
                        dialog.show();
                        upImg(dialog);
                    } else {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("quession", quessionBean);
                        bundle.putInt("position", position);
                        intent.putExtra("mbund", bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else {

                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("quession", quessionBean);
                    bundle.putInt("position", position);
                    intent.putExtra("mbund", bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        binding.llAdd.setOnClickListener(v -> {
            QueryInfo.QuessionBean.OptionBean optionBean = new QueryInfo.QuessionBean.OptionBean("");
            prefectQuessionDragAdapter.addData(optionBean);
            quessionBean.getOptionList().add(optionBean);
        });

    }

    //上传图片
    private void upImg(LoadDialog dialog) {
        viewModel.GetOSS(new Function1<CommonResponse<STSBean>, Unit>() {
            @Override
            public Unit invoke(CommonResponse<STSBean> response) {
                if (response.getCode() == 0) {
                    STSBean stsBean = response.getData();
                    List<String> imglist = new ArrayList<>();
                    if (!TextUtils.isEmpty(quessionBean.getBDquestionImgUrl())) {
                        imglist.add(quessionBean.getBDquestionImgUrl());
                    }
                    if (quessionBean.getOptionList().size() > 0) {
                        for (int i = 0; i < quessionBean.getOptionList().size(); i++) {
                            if (!TextUtils.isEmpty(quessionBean.getOptionList().get(i).getBdoptionImgUrl())) {
                                imglist.add(quessionBean.getOptionList().get(i).getBdoptionImgUrl());
                            } else {
                                imglist.add("");
                            }
                        }
                    }
                    uploadImgs(imglist, stsBean, 0, dialog);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    });
                }
                return null;
            }
        });
    }


    private void uploadImgs(List<String> upfiles, STSBean stsBean, int count, LoadDialog dialog) {
        int size = upfiles.size();
        if (upfiles.get(count).isEmpty()) {
            int num = count + 1;
            if (num == size) {
                dialog.dismiss();
                Log.d("上传之后的图片集合", JSON.toJSONString(upfiles));
                if (!TextUtils.isEmpty(quessionBean.getBDquestionImgUrl())) {
                    quessionBean.setQuestionImgUrl(upfiles.get(0));
                    upfiles.remove(0);
                }

                for (int i = 0; i < quessionBean.getOptionList().size(); i++) {
                    if (!TextUtils.isEmpty(quessionBean.getOptionList().get(i).getBdoptionImgUrl())) {
                        quessionBean.getOptionList().get(i).setOptionImgUrl(upfiles.get(i));
                    }
                }
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("quession", quessionBean);
                bundle.putInt("position", position);
                intent.putExtra("mbund", bundle);
                setResult(RESULT_OK, intent);
                finish();
                return;
            }

            uploadImgs(upfiles, stsBean, num, dialog);
            return;
        }
        AliYunOssUploadOrDownFileConfig.getInstance(this).initOss(stsBean.getEndpoint(), stsBean.getAccessKeyId(),
                stsBean.getAccessKeySecret(), stsBean.getSecurityToken());
        String type = upfiles.get(count).substring(upfiles.get(count).lastIndexOf(".") + 1, upfiles.get(count).length());
        String path = stsBean.getTempFilePath() + System.currentTimeMillis() + "." + type;
//        String uploadFilePath = stsBean.getCdn() + path;
        AliYunOssUploadOrDownFileConfig.getInstance(this).uploadFile(stsBean.getBucketName(), path, upfiles.get(count), "", 0);
        upfiles.set(count, path);
        AliYunOssUploadOrDownFileConfig.getInstance(this).setOnUploadFile(new AliYunOssUploadOrDownFileConfig.OnUploadFile() {
            @Override
            public void onUploadFileSuccess(String info) {
                int scount = count + 1;
                if (scount == size) {
                    dialog.dismiss();
                    Log.d("上传之后的图片集合", JSON.toJSONString(upfiles));
                    if (!TextUtils.isEmpty(quessionBean.getBDquestionImgUrl())) {
                        quessionBean.setQuestionImgUrl(upfiles.get(0));
                        upfiles.remove(0);
                    }

                    for (int i = 0; i < quessionBean.getOptionList().size(); i++) {
                        if (!TextUtils.isEmpty(quessionBean.getOptionList().get(i).getBdoptionImgUrl())) {
                            quessionBean.getOptionList().get(i).setOptionImgUrl(upfiles.get(i));
                        }
                    }
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("quession", quessionBean);
                    bundle.putInt("position", position);
                    intent.putExtra("mbund", bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                    return;
                }
                uploadImgs(upfiles, stsBean, scount, dialog);
            }

            @Override
            public void onUploadFileFailed(String errCode) {
                dialog.dismiss();
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

    private boolean quessionCanBack() {
        return ParamsUtils.isquessionOk(quessionBean);
    }

    @Override
    public void initData() {

        if (queryId == 0) { //添加调查
            if (getIntent().getSerializableExtra("quessionBean") != null) {
                ettimu.setText(quessionBean.getQuestionInfo());
            }
            if (!TextUtils.isEmpty(quessionBean.getBDquestionImgUrl())) {
                GlideUtils.INSTANCE.loadRoundLocal(quessionBean.getBDquestionImgUrl(), iv_timuimg, 5);
            }
            prefectQuessionDragAdapter.setList(quessionBean.getOptionList());
        } else {  //修改调查
            if (!TextUtils.isEmpty(quessionBean.getQuestionImgUrl())) {
                downloadFM(GlideUtils.INSTANCE.handleImgUrl(quessionBean.getQuestionImgUrl()));
            }
            ettimu.setText(quessionBean.getQuestionInfo());
            if (quessionBean.getOptionList().size() > 0) {
                boolean isdownload = false;
                for (int i = 0; i < quessionBean.getOptionList().size(); i++) {
                    QueryInfo.QuessionBean.OptionBean optionBean = quessionBean.getOptionList().get(i);
                    if (!TextUtils.isEmpty(optionBean.getOptionImgUrl())) {
                        isdownload = true; //需要下载图片
                    }
                }
                if (isdownload) {
                    downloadListIMG(quessionBean.getOptionList(), 0, quessionBean.getOptionList().size()); //下载选项中的图片
                } else {
                    prefectQuessionDragAdapter.setList(quessionBean.getOptionList());
                }


            }
        }

//        if (getIntent().getSerializableExtra("quessionBean")!=null){
//            ettimu.setText(quessionBean.getQuestionInfo());
//            if (!TextUtils.isEmpty(quessionBean.getBDquestionImgUrl())){
//                GlideUtils.INSTANCE.loadRoundLocal(quessionBean.getBDquestionImgUrl(), iv_timuimg,5);
//                prefectQuessionDragAdapter.setList(quessionBean.getOptionList());
//            }else{
//
//                if (!TextUtils.isEmpty(quessionBean.getQuestionImgUrl())){
//                    downloadFM(GlideUtils.INSTANCE.handleImgUrl(quessionBean.getQuestionImgUrl()));
//                }
//                if (quessionBean.getOptionList().size()>0){
//                    boolean isdownload =false;
//                    for (int i = 0; i <quessionBean.getOptionList().size() ; i++) {
//                        QueryInfo.QuessionBean.OptionBean optionBean =quessionBean.getOptionList().get(i);
//                        if (!TextUtils.isEmpty(optionBean.getOptionImgUrl())){
//                            isdownload = true; //需要下载图片
//                        }
//                    }
//                    if (isdownload){
//                        downloadListIMG(quessionBean.getOptionList(),0,quessionBean.getOptionList().size()); //下载选项中的图片
//                    }else{
//
//                    }
//
//
//                }
//            }
//        }
    }

    /**
     * 下载图片集合
     *
     * @param
     */
    public void downloadListIMG(List<QueryInfo.QuessionBean.OptionBean> urls, int count, int total) {

        if (!TextUtils.isEmpty(urls.get(count).getOptionImgUrl())) {
            Observable.create(new ObservableOnSubscribe<File>() {
                @Override
                public void subscribe(ObservableEmitter<File> e) throws Exception {
                    //通过gilde下载得到file文件,这里需要注意android.permission.INTERNET权限
                    e.onNext(Glide.with(PerfectQuessionActivity.this)
                            .load(GlideUtils.INSTANCE.handleImgUrl(urls.get(count).getOptionImgUrl()))
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
//                            quessionBean.getOptionList().get(count).setOptionImgUrl(destFile.getPath());
                            quessionBean.getOptionList().get(count).setBdoptionImgUrl(destFile.getPath());
                            // 最后通知图库更新
//                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                                    Uri.fromFile(new File(destFile.getPath()))));


                            int scount = count + 1;
                            if (scount == total) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        prefectQuessionDragAdapter.setList(quessionBean.getOptionList());
                                    }
                                });
                                return;
                            }
                            downloadListIMG(urls, scount, urls.size());
                        }
                    });
        } else {
            int scount = count + 1;
            if (scount == total) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prefectQuessionDragAdapter.setList(quessionBean.getOptionList());
                    }
                });
                return;
            }
            downloadListIMG(urls, scount, urls.size());
        }
    }


    public void downloadFM(String url) {
        Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> e) throws Exception {
                //通过gilde下载得到file文件,这里需要注意android.permission.INTERNET权限
                e.onNext(Glide.with(PerfectQuessionActivity.this)
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
//                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                                Uri.fromFile(new File(destFile.getPath()))));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                GlideUtils.INSTANCE.loadRoundLocal(GlideUtils.INSTANCE.handleImgUrl(url), iv_timuimg, 5);
//                                quessionBean.setQuestionImgUrl(destFile.getPath());
                                quessionBean.setBDquestionImgUrl(destFile.getPath());
                            }
                        });

                    }
                });
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


    OnItemDragListener onItemDragListener = new OnItemDragListener() {
        @Override
        public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
//            Log.d(TAG, "drag start");
//            final BaseViewHolder holder = ((BaseViewHolder) viewHolder);
//
//            // 开始时，item背景色变化，demo这里使用了一个动画渐变，使得自然
//            int startColor = Color.WHITE;
//            int endColor = Color.rgb(245, 245, 245);
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                ValueAnimator v = ValueAnimator.ofArgb(startColor, endColor);
//                v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        holder.itemView.setBackgroundColor((int)animation.getAnimatedValue());
//                    }
//                });
//                v.setDuration(300);
//                v.start();
//            }
        }

        @Override
        public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {

        }

        @Override
        public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
//            Log.d(TAG, "drag end");
//            final BaseViewHolder holder = ((BaseViewHolder) viewHolder);
//            // 结束时，item背景色变化，demo这里使用了一个动画渐变，使得自然
//            int startColor = Color.rgb(245, 245, 245);
//            int endColor = Color.WHITE;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                ValueAnimator v = ValueAnimator.ofArgb(startColor, endColor);
//                v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        holder.itemView.setBackgroundColor((int)animation.getAnimatedValue());
//                    }
//                });
//                v.setDuration(300);
//                v.start();
//            }
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
            canvas.drawColor(ContextCompat.getColor(PerfectQuessionActivity.this, R.color.white));
        }
    };

    /**
     * 直接打开相册
     */
    private void openxiangche() {
        if (!DoubleUtils.isFastDoubleClick()) {

            openCarcme = new OpenCarcme(PerfectQuessionActivity.this, new OpenCarcme.OpenCarcmeCallBack() {
                @Override
                public void onPicBack() {
                    PictureUtils.openGarlly(PerfectQuessionActivity.this, 1, new OnResultCallbackListener<LocalMedia>() {

                        @Override
                        public void onResult(List<LocalMedia> result) {
                            for (LocalMedia media : result) {

                                quessionBean.setQuestionImgUrl("");
                                quessionBean.setBDquestionImgUrl(AppUtils.getFinallyPath(media));
                                GlideUtils.INSTANCE.loadRoundLocal(AppUtils.getFinallyPath(media), iv_timuimg, 5);
                                ivshanchu.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancel() {

                        }
                    }, 2, 1);
                }

                @Override
                public void onCarcme() {
                    PictureUtils.opencarcme(PerfectQuessionActivity.this, new OnResultCallbackListener<LocalMedia>() {
                        @Override
                        public void onResult(List<LocalMedia> result) {
                            // 结果回调
                            if (result.size() > 0) {

                                for (LocalMedia media : result) {
                                    String path = "";
                                    if (media.isCut() && !media.isCompressed()) {
                                        // 裁剪过
                                        path = media.getCutPath();
                                    } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                                        // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                                        path = media.getCompressPath();
                                    } else {
                                        // 原图
                                        path = media.getPath();
                                    }
                                    quessionBean.setQuestionImgUrl("");
                                    quessionBean.setBDquestionImgUrl(AppUtils.getFinallyPath(media));
                                    GlideUtils.INSTANCE.loadRoundLocal(AppUtils.getFinallyPath(media), iv_timuimg, 5);
                                }
                            }
                        }

                        @Override
                        public void onCancel() {
                            // 取消
                        }
                    });
                }
            });
            openCarcme.showPopupWindow();
        }
    }

    /**
     * 直接打开相册
     */
    private void openxiangche(int index) {

        // 进入相册 以下是例子：不需要的api可以不写
        PictureUtils.openGarlly(PerfectQuessionActivity.this, new OnResultCallbackListener<LocalMedia>() {

            @Override
            public void onResult(List<LocalMedia> result) {
                for (LocalMedia media : result) {

                    quessionBean.getOptionList().get(index).setOptionImgUrl("");
                    quessionBean.getOptionList().get(index).setBdoptionImgUrl(AppUtils.getFinallyPath(media));
                    prefectQuessionDragAdapter.getItem(index).setOptionImgUrl("");
                    prefectQuessionDragAdapter.getItem(index).setBdoptionImgUrl(AppUtils.getFinallyPath(media));
                    prefectQuessionDragAdapter.setData(index, prefectQuessionDragAdapter.getItem(index));

                }
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        try{
//            FileUtils.deleteAllInDir( Environment.getExternalStorageDirectory().getPath()+"/Uni");
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                    Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()+"/Uni"))));
//        }catch (Exception e){
//
//        }
    }

    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
