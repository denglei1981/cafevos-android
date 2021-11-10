/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.changanford.common.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.changanford.common.MyApp;
import com.changanford.common.R;
import com.changanford.common.basic.BaseActivity;
import com.changanford.common.basic.EmptyViewModel;
import com.changanford.common.bean.JumpDataBean;
import com.changanford.common.databinding.ActivityDefinedBinding;
import com.changanford.common.net.CommonResponse;
import com.changanford.common.net.HeaderUtilsKt;
import com.changanford.common.router.path.ARouterHomePath;
import com.changanford.common.ui.dialog.AlertDialog;
import com.changanford.common.util.JumpUtils;
import com.changanford.common.util.bus.LiveDataBus;
import com.changanford.common.utilext.LogUtilsKt;
import com.changanford.common.utilext.ToastUtilsKt;
import com.gyf.immersionbar.ImmersionBar;
import com.huawei.hms.hmsscankit.OnLightVisibleCallBack;
import com.huawei.hms.hmsscankit.OnResultCallback;
import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;


/**
 * 扫一扫页面
 */
@Route(path = ARouterHomePath.CaptureActivity)
public class CaptureActivity extends BaseActivity<ActivityDefinedBinding, CommonViewModel> {
    private FrameLayout frameLayout;
    private RemoteView remoteView;
    private ImageView backBtn;
    private ImageView imgBtn;
    private ImageView flushBtn;
    int mScreenWidth;
    int mScreenHeight;
    //The width and height of scan_view_finder is both 240 dp.
    final int SCAN_FRAME_SIZE = 240;

    private int[] img = {R.mipmap.flashlight_on, R.mipmap.flashlight_off};
    private static final String TAG = "DefinedActivity";

    //Declare the key. It is used to obtain the value returned from Scan Kit.
    public static final String SCAN_RESULT = "scanResult";
    public static final int REQUEST_CODE_PHOTO = 0X1113;

    private boolean isScanBarCode;
    private boolean shouldCallback;

    private ImmersionBar mImmersionBar;
    private ImageView mIvScan;
    Animation mTop2Bottom, mBottom2Top;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setStatusBar();
//        Bundle mBundle = getIntent().getExtras();
//        if (mBundle != null) {
//            isScanBarCode = mBundle.getBoolean("isScanBarCode");
//            shouldCallback = mBundle.getBoolean("shouldCallback");
//        }
//
//        // Bind the camera preview screen.
//        frameLayout = findViewById(R.id.rim);
//
//        //1. Obtain the screen density to calculate the viewfinder's rectangle.
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        float density = dm.density;
//        //2. Obtain the screen size.
//        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
//        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
//
//        int scanFrameSize = (int) (SCAN_FRAME_SIZE * density);
//
//        //3. Calculate the viewfinder's rectangle, which in the middle of the layout.
//        //Set the scanning area. (Optional. Rect can be null. If no settings are specified, it will be located in the middle of the layout.)
//        Rect rect = new Rect();
//        rect.left = mScreenWidth / 2 - scanFrameSize / 2;
//        rect.right = mScreenWidth / 2 + scanFrameSize / 2;
//        rect.top = mScreenHeight / 2 - scanFrameSize / 2;
//        rect.bottom = mScreenHeight / 2 + scanFrameSize / 2;
//
//
//        //Initialize the RemoteView instance, and set callback for the scanning result.
//        remoteView = new RemoteView.Builder().setContext(this).setBoundingBox(rect).setFormat(HmsScan.ALL_SCAN_TYPE)
//                .build();
//        // When the light is dim, this API is called back to display the flashlight switch.
//        flushBtn = findViewById(R.id.flush_btn);
//        remoteView.setOnLightVisibleCallback(new OnLightVisibleCallBack() {
//            @Override
//            public void onVisibleChanged(boolean visible) {
//                if(visible){
//                    flushBtn.setVisibility(View.GONE);
//                }
//            }
//        });
//        // Subscribe to the scanning result callback event.
//        remoteView.setOnResultCallback(new OnResultCallback() {
//            @Override
//            public void onResult(HmsScan[] result) {
//                //Check the result.
//                if (result != null && result.length > 0 && result[0] != null && !TextUtils.isEmpty(result[0].getOriginalValue())) {
//                    String ss = result[0].getOriginalValue();
//                    LogUtil.e(ss);
////                    ToastUtils.INSTANCE.showShort(ss);
//                    if (shouldCallback) {
//                        Intent intent = new Intent();
//                        intent.putExtra(SCAN_RESULT, result[0].getOriginalValue());
//                        setResult(RESULT_OK, intent);
//                        finish();
//                    } else {
//                        getCapBack(ss);
//                        remoteView.pauseContinuouslyScan();
//                    }
//
//                }
//            }
//        });
//        // Load the customized view to the activity.
//        remoteView.onCreate(savedInstanceState);
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        frameLayout.addView(remoteView, params);
//        // Set the back, photo scanning, and flashlight operations.
//        setBackOperation();
//        setPictureScanOperation();
//        setFlashOperation();
//        initscanline();
//    }

    private void initscanline() {
        mIvScan = findViewById(R.id.scan_line);

        mTop2Bottom = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.8f);

        mBottom2Top = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.8f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f);

        mBottom2Top.setRepeatMode(Animation.RESTART);
        mBottom2Top.setInterpolator(new LinearInterpolator());
        mBottom2Top.setDuration(1500);
        mBottom2Top.setFillEnabled(true);//使其可以填充效果从而不回到原地
        mBottom2Top.setFillAfter(true);//不回到起始位置
        //如果不添加setFillEnabled和setFillAfter则动画执行结束后会自动回到远点
        mBottom2Top.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIvScan.startAnimation(mTop2Bottom);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mTop2Bottom.setRepeatMode(Animation.RESTART);
        mTop2Bottom.setInterpolator(new LinearInterpolator());
        mTop2Bottom.setDuration(1500);
        mTop2Bottom.setFillEnabled(true);
        mTop2Bottom.setFillAfter(true);
        mTop2Bottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIvScan.startAnimation(mBottom2Top);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mIvScan.startAnimation(mTop2Bottom);
    }


    protected void setStatusBar() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.statusBarDarkFont(true, 0.8f).fitsSystemWindows(false).init();

    }

    /**
     * Call the lifecycle management method of the remoteView activity.
     */
    private void setPictureScanOperation() {
        imgBtn = findViewById(R.id.img_btn);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                CaptureActivity.this.startActivityForResult(pickIntent, REQUEST_CODE_PHOTO);

            }
        });
    }

    private void setFlashOperation() {
        flushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (remoteView.getLightStatus()) {
                    remoteView.switchLight();
                    flushBtn.setImageResource(img[1]);
                } else {
                    remoteView.switchLight();
                    flushBtn.setImageResource(img[0]);
                }
            }
        });
    }

    private void setBackOperation() {
        backBtn = findViewById(R.id.back_img);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureActivity.this.finish();
            }
        });
    }

    /**
     * Call the lifecycle management method of the remoteView activity.
     */
    @Override
    protected void onStart() {
        super.onStart();
        remoteView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        remoteView.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("resume", "saomaresume----");
    }

    @Override
    protected void onPause() {
        super.onPause();
        remoteView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        remoteView.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        remoteView.onStop();
    }

    /**
     * Handle the return results from the album.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                HmsScan[] hmsScans = ScanUtil.decodeWithBitmap(CaptureActivity.this, bitmap, new HmsScanAnalyzerOptions.Creator().setPhotoMode(true).create());
                if (hmsScans != null && hmsScans.length > 0 && hmsScans[0] != null && !TextUtils.isEmpty(hmsScans[0].getOriginalValue())) {
                    Intent intent = new Intent();
                    intent.putExtra(SCAN_RESULT, hmsScans[0]);
                    setResult(RESULT_OK, intent);
                    CaptureActivity.this.finish();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void getCapBack(String result) {
        ToastUtilsKt.toast("扫码成功");
        viewModel.scanRequest(result, response -> {
            if (response != null && response.getData() != null) {
                if (response.getData().getJumpDataType() == 60) {
                    JumpUtils.getInstans().jump(60, response.getData().getJumpDataValue());
                    remoteView.pauseContinuouslyScan();
                } else if (response.getData().getJumpDataType() == 59) {
                    remoteView.pauseContinuouslyScan();
                    new AlertDialog(CaptureActivity.this).builder()
                            .setTitle("扫码结果")
                            .setMsg(response.getData().getJumpDataValue())
                            .setCancelable(false)
                            .setNegativeButton("退出扫描", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            }).setPositiveButton("再次扫描", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            remoteView.resumeContinuouslyScan();
                        }
                    }).show();
                } else {
                    JumpUtils.getInstans().jump(response.getData().getJumpDataType(),response.getData().getJumpDataValue());
                }
            }
        });
    }

    private void resume() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            remoteView.resumeContinuouslyScan();
        }).start();
    }


    @Override
    public void initData() {
        LiveDataBus.get().with("DISMISS_DIALOG").observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                Log.d("DISMISS_DIALOG", "----DISMISS_DIALOG");
                remoteView.resumeContinuouslyScan();
            }
        });
    }

    @Override
    public void initView() {
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            isScanBarCode = mBundle.getBoolean("isScanBarCode");
            shouldCallback = mBundle.getBoolean("shouldCallback");
        }

        // Bind the camera preview screen.
        frameLayout = findViewById(R.id.rim);

        //1. Obtain the screen density to calculate the viewfinder's rectangle.
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;
        //2. Obtain the screen size.
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;

        int scanFrameSize = (int) (SCAN_FRAME_SIZE * density);

        //3. Calculate the viewfinder's rectangle, which in the middle of the layout.
        //Set the scanning area. (Optional. Rect can be null. If no settings are specified, it will be located in the middle of the layout.)
        Rect rect = new Rect();
        rect.left = mScreenWidth / 2 - scanFrameSize / 2;
        rect.right = mScreenWidth / 2 + scanFrameSize / 2;
        rect.top = mScreenHeight / 2 - scanFrameSize / 2;
        rect.bottom = mScreenHeight / 2 + scanFrameSize / 2;


        //Initialize the RemoteView instance, and set callback for the scanning result.
        remoteView = new RemoteView.Builder().setContext(this).setBoundingBox(rect).setFormat(HmsScan.ALL_SCAN_TYPE)
                .build();
        // When the light is dim, this API is called back to display the flashlight switch.
        flushBtn = findViewById(R.id.flush_btn);
        remoteView.setOnLightVisibleCallback(new OnLightVisibleCallBack() {
            @Override
            public void onVisibleChanged(boolean visible) {
                if (visible) {
                    flushBtn.setVisibility(View.GONE);
                }
            }
        });
        // Subscribe to the scanning result callback event.
        remoteView.setOnResultCallback(new OnResultCallback() {
            @Override
            public void onResult(HmsScan[] result) {
                //Check the result.
                if (result != null && result.length > 0 && result[0] != null && !TextUtils.isEmpty(result[0].getOriginalValue())) {
                    String ss = result[0].getOriginalValue();
//                    ToastUtils.INSTANCE.showShort(ss);
                    if (shouldCallback) {
                        Intent intent = new Intent();
                        intent.putExtra(SCAN_RESULT, result[0].getOriginalValue());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        getCapBack(ss);
                    }

                }
            }
        });
        // Load the customized view to the activity.
        remoteView.onCreate(null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        frameLayout.addView(remoteView, params);
        // Set the back, photo scanning, and flashlight operations.
        setBackOperation();
        setPictureScanOperation();
        setFlashOperation();
        initscanline();
    }

}
