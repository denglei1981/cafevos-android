package com.changanford.common.sharelib.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.changanford.common.sharelib.ModuleConfigureConstant;
import com.changanford.common.sharelib.event.RxBus;
import com.changanford.common.sharelib.event.ShareEventMessage;
import com.changanford.common.sharelib.event.ShareResultType;
import com.changanford.common.sharelib.util.MediaType;
import com.changanford.common.util.bus.LiveDataBus;
import com.changanford.common.util.bus.LiveDataBusKey;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;


/**
 * QQ分享对应的透明Activity
 */
public class QqShareActivity extends AppCompatActivity implements IUiListener {

    private static final String EXTRA_TITLE = "EXTRA_TITLE";
    private static final String EXTRA_CONTENT = "EXTRA_CONTENT";
    private static final String EXTRA_TARGET_URL = "EXTRA_TARGET_URL";
    private static final String EXTRA_IMAGE_URL = "EXTRA_IMAGE_URL";
    private static final String EXTRA_MEDIA_TYPE = "EXTRA_MEDIA_TYPE";

    private String title; // 标题
    private String content; // 分享内容
    private String targetUrl; // 跳转地址
    private String imageUrl; // 图片地址
    private int mediaType; // 内容类型

    /**
     * 成员属性  Tencent
     */
    private Tencent mTencent;


    boolean iszoom;

    /**
     * 跳转逻辑
     *
     * @param context 上下文
     */
    public static void skipTo(boolean iszoom, Context context, String title, String content, String targetUrl, String imageUrl, int mediaType) {

        Intent intent = new Intent(context, QqShareActivity.class);
        intent.putExtra("iszoom", iszoom);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_CONTENT, content);
        intent.putExtra(EXTRA_TARGET_URL, targetUrl);
        intent.putExtra(EXTRA_IMAGE_URL, imageUrl);
        intent.putExtra(EXTRA_MEDIA_TYPE, mediaType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerToQq();
        getData();
        if (!iszoom) {
            share2Qq();
        } else {
            share2Qqzoom();
        }
    }

    private void getData() {
        if (getIntent().hasExtra("iszoom")) {
            iszoom = getIntent().getBooleanExtra("iszoom", false);
        }
        if (getIntent().hasExtra(EXTRA_TITLE)) {
            title = getIntent().getStringExtra(EXTRA_TITLE);
        }
        if (getIntent().hasExtra(EXTRA_CONTENT)) {
            content = getIntent().getStringExtra(EXTRA_CONTENT);
        }
        if (getIntent().hasExtra(EXTRA_TARGET_URL)) {
            targetUrl = getIntent().getStringExtra(EXTRA_TARGET_URL);
        }
        if (getIntent().hasExtra(EXTRA_IMAGE_URL)) {
            imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        }
        if (getIntent().hasExtra(EXTRA_MEDIA_TYPE)) {
            mediaType = getIntent().getIntExtra(EXTRA_MEDIA_TYPE, MediaType.MEDIA_TYPE_WEB);
        }
    }

    /**
     * QQ的注册
     */
    private void registerToQq() {
        mTencent = Tencent.createInstance(ModuleConfigureConstant.QQ_APPID, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.REQUEST_QQ_SHARE || requestCode == Constants.REQUEST_QZONE_SHARE){
            if (resultCode == 0) {
                Tencent.handleResultData(data, this);
            }else{
                finish();
            }

        }
    }

    /**
     * 分享到QQ
     */
    private void share2Qq() {
        switch (mediaType) {
            case MediaType.MEDIA_TYPE_WEB:
                shareWeb();
                break;
            case MediaType.MEDIA_TYPE_IMG:
                shareImage();
                break;
            default:
                RxBus.getIntanceBus().post(new ShareEventMessage(ShareResultType.SHARE_FAIL));
                finish();
                break;
        }
    }

    /**
     * 分享到QQ空间
     */
    private void share2Qqzoom() {
        switch (mediaType) {
            case MediaType.MEDIA_TYPE_WEB:
                shareWebzoom();
                break;
            case MediaType.MEDIA_TYPE_IMG:
                shareImagezoom();
                break;
            default:
                RxBus.getIntanceBus().post(new ShareEventMessage(ShareResultType.SHARE_FAIL));
                finish();
                break;
        }
    }

    /**
     * QQ链接分享
     */
    private void shareWeb() {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, content);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        mTencent.shareToQQ(this, params, this);
    }

    /**
     * QQ空间链接分享
     */
    private void shareWebzoom() {
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        ArrayList<String> imgurllist= new ArrayList<>();
        imgurllist.add(imageUrl);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgurllist);
        mTencent.shareToQzone(this, params, this);
    }

    /**
     * QQ海报分享
     */
    private void shareImage() {
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl);
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        mTencent.shareToQQ(this, params, this);
    }

    /**
     * QQzoom海报分享
     */
    private void shareImagezoom() {

        // 分享类型
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(imageUrl);
        Bundle params = new Bundle();
        params.putInt(QzonePublish.PUBLISH_TO_QZONE_KEY_TYPE, QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD);
        params.putString(QzonePublish.PUBLISH_TO_QZONE_SUMMARY, "说说正文");
        params.putStringArrayList(QzonePublish.PUBLISH_TO_QZONE_IMAGE_URL,
                arrayList);// 图片地址ArrayList
        // 分享操作要在主线程中完成
        mTencent.publishToQzone(this, params, this);

    }

    @Override
    public void onComplete(Object o) {
        RxBus.getIntanceBus().post(new ShareEventMessage(ShareResultType.SHARE_SUCCESS));
        LiveDataBus.get().with(LiveDataBusKey.WX_SHARE_BACK,Integer.class).postValue(ShareResultType.SHARE_SUCCESS);
        finish();
    }

    @Override
    public void onError(UiError uiError) {
        RxBus.getIntanceBus().post(new ShareEventMessage(ShareResultType.SHARE_FAIL));
        LiveDataBus.get().with(LiveDataBusKey.WX_SHARE_BACK,Integer.class).postValue(ShareResultType.SHARE_FAIL);
        finish();
    }

    @Override
    public void onCancel() {
        RxBus.getIntanceBus().post(new ShareEventMessage(ShareResultType.SHARE_CANCLE));
        LiveDataBus.get().with(LiveDataBusKey.WX_SHARE_BACK,Integer.class).postValue(ShareResultType.SHARE_SUCCESS);
        finish();
    }

    @Override
    public void onWarning(int i) {

    }
}
