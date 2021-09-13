package com.changanford.common.sharelib.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.changanford.common.sharelib.ModuleConfigureConstant;
import com.changanford.common.sharelib.event.RxBus;
import com.changanford.common.sharelib.event.ShareEventMessage;
import com.changanford.common.sharelib.event.ShareResultType;
import com.changanford.common.sharelib.manager.SinaShareManager;
import com.changanford.common.sharelib.util.StringUtil;
import com.changanford.common.util.bus.LiveDataBus;
import com.changanford.common.util.bus.LiveDataBusKey;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MultiImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.common.UiError;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;
import com.sina.weibo.sdk.share.WbShareCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;


/**
 * 微博分享对应的Activity
 */
public class SinaShareActivity  extends AppCompatActivity implements WbShareCallback {

    private static final String EXTRA_TITLE = "EXTRA_TITLE";
    private static final String EXTRA_CONTENT = "EXTRA_CONTENT";
    private static final String EXTRA_URL = "EXTRA_URL";
    private static final String EXTRA_THUMB_DATA = "EXTRA_THUMB_DATA";
    private static final String EXTRA_LOCAL_IMAGE = "EXTRA_LOCAL_IMAGE";

    private String title; // 标题
    private String content; // 内容
    private String url; // 连接地址
    private byte[] thumbData; // webpage模式时使用
    private String localImage; // 本地图片地址

    private Oauth2AccessToken mAccessToken; // token
    private IWBAPI mWBAPI;
//    private SsoHandler mSsoHandler; // 授权

    /**
     * 分享回调
     */
//    private WbShareHandler shareHandler;

    public static   void skipTo(Context context,String title, String content, String url,byte[] thumbData) {
        skipTo(context,title,content,url,thumbData,null);
    }

    /**
     * 跳转方法
     *
     * @param context 上下文
     * @param localPicPath 本地图片地址
     */
    public static   void skipTo(Context context,String title, String content, String url,byte[] thumbData,String localPicPath) {
        Intent intent = new Intent(context, SinaShareActivity.class);
        intent.putExtra(EXTRA_TITLE,title);
        intent.putExtra(EXTRA_CONTENT,content);
        intent.putExtra(EXTRA_URL,url);
        intent.putExtra(EXTRA_THUMB_DATA,thumbData);
        if (!StringUtil.isEmpt(localPicPath)) {
            intent.putExtra(EXTRA_LOCAL_IMAGE,localPicPath);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        registerToSina();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (getIntent().hasExtra(EXTRA_TITLE)) {
            title = getIntent().getStringExtra(EXTRA_TITLE);
        }
        if (getIntent().hasExtra(EXTRA_CONTENT)) {
            content = getIntent().getStringExtra(EXTRA_CONTENT);
        }
        if (getIntent().hasExtra(EXTRA_URL)) {
            url = getIntent().getStringExtra(EXTRA_URL);
        }
        if (getIntent().hasExtra(EXTRA_THUMB_DATA)) {
            thumbData = getIntent().getByteArrayExtra(EXTRA_THUMB_DATA);
        }
        if (getIntent().hasExtra(EXTRA_LOCAL_IMAGE)) {
            localImage = getIntent().getStringExtra(EXTRA_LOCAL_IMAGE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mWBAPI.doResultIntent(intent, this);
    }

    /**
     * 微博分享注册
     */
    private void registerToSina() {
//        WbSdk.install(this, new AuthInfo(this, ModuleConfigureConstant.APP_KEY, ModuleConfigureConstant.REDIRECT_URL, ModuleConfigureConstant.SCOPE));
//        shareHandler = new WbShareHandler(this);
//        shareHandler.registerApp();
//        shareHandler.setProgressColor(0xff33b5e5);
//
//        mSsoHandler = new SsoHandler(this);
//        mSsoHandler.authorize(new SelfWbAuthListener());

        AuthInfo authInfo = new AuthInfo(this, ModuleConfigureConstant.APP_KEY, ModuleConfigureConstant.REDIRECT_URL, ModuleConfigureConstant.SCOPE);
        mWBAPI = WBAPIFactory.createWBAPI(this);
        mWBAPI.registerApp(this, authInfo);

        mWBAPI.setLoggerEnable(false);
        share2Sina();
//        startAuth();
    }

    private void startAuth() {
        //auth
        mWBAPI.authorize(new WbAuthListener() {
            @Override
            public void onComplete(Oauth2AccessToken token) {
            SinaShareActivity.this.runOnUiThread(() -> {
                mAccessToken = token;
                if (mAccessToken.isSessionValid()) {
                    // 保存 Token 到 SharedPreferences
                    Oauth2AccessToken.parseAccessToken(String.valueOf(mAccessToken));
//                    Oauth2AccessToken.writeAccessToken(SinaShareActivity.this, mAccessToken);
                    Toast.makeText(SinaShareActivity.this,"验证成功", Toast.LENGTH_SHORT).show();
                    share2Sina();
                }
            });
//                Toast.makeText(SinaShareActivity.this, "微博授权成功", Toast.LENGTH_SHORT).show();
//                share2Sina();
            }

            @Override
            public void onError(UiError error) {
                Toast.makeText(SinaShareActivity.this, "微博授权出错", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(SinaShareActivity.this, "微博授权取消", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 微博分享
     *
     */
    private void share2Sina() {
        boolean haseText = !StringUtil.isEmpt(title) || !StringUtil.isEmpt(content);
        boolean haseImage = (SinaShareManager.posterData != null && SinaShareManager.posterThumbData != null) || !StringUtil.isEmpt(localImage);
        if (!haseText && !haseImage) {
            RxBus.getIntanceBus().post(new ShareEventMessage(ShareResultType.SHARE_FAIL));
            finish();
        } else {
            share(haseText,haseImage);
        }
    }

    /**
     * 微博海报分享
     *
     * @param haseText 是否带有text
     *
     */
    private void share(boolean haseText,boolean haseImage) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
//        if (haseText) {
//            weiboMessage.textObject = getTextObject();
//        }
//        // 图片模式并且本地图片地址不为空
//        if (haseImage && !StringUtil.isEmpt(localImage)) {
//            weiboMessage.imageObject = getImageObject(localImage);
//        } else {
//            weiboMessage.imageObject = getImageObject(localImage);
//        }
        if (TextUtils.isEmpty(localImage)){
            WebpageObject webObject = new WebpageObject();
            webObject.identify = UUID.randomUUID().toString();
            webObject.title = title;
            webObject.description = content;
            webObject.thumbData = thumbData;
            webObject.actionUrl = url;
            webObject.defaultText = "分享网页";
            weiboMessage.mediaObject = webObject;
            if (thumbData!=null){
                ImageObject imageObject = new ImageObject();
                Bitmap bitmap = BitmapFactory.decodeByteArray(thumbData, 0, thumbData.length);
                imageObject.setImageData(bitmap);
                weiboMessage.imageObject = imageObject;
            }
        }else{
            ImageObject imageObject = new ImageObject();
            Bitmap bitmap = BitmapFactory.decodeFile(localImage);
            imageObject.setImageData(bitmap);
            weiboMessage.imageObject = imageObject;
        }
        mWBAPI.shareMessage(weiboMessage, false);
    }

    /**
     * 文本分享内容
     * @return
     */
    private TextObject getTextObject() {
        TextObject textObject = new TextObject();
        textObject.text = content + url;
        textObject.title = title;
        textObject.actionUrl = url;
        textObject.thumbData = thumbData;
        return textObject;
    }

    /**
     * 获取图片分享内容
     * @param
     * @param
     * @return
     */
    private ImageObject getImageObject(String url) {
        ImageObject imageObject = new ImageObject();
        Bitmap bitmap = BitmapFactory.decodeFile(localImage);
        imageObject.setImageData(bitmap);
        return imageObject;
    }

    private MultiImageObject getMultiImageObject() {
        File file = new File(localImage);
        MultiImageObject multiImageObject = new MultiImageObject();
        ArrayList<Uri> pathList = new ArrayList<>();
        pathList.add(Uri.fromFile(new File(localImage)));
        multiImageObject.imageList=pathList;
        return multiImageObject;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mWBAPI.doResultIntent(data, this);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清除缓存数据
        SinaShareManager.onDestory();
    }

    @Override
    public void onComplete() {
        LiveDataBus.get().with(LiveDataBusKey.WX_SHARE_BACK,Integer.class).postValue(ShareResultType.SHARE_SUCCESS);
        finish();
    }

    @Override
    public void onError(UiError uiError) {
        LiveDataBus.get().with(LiveDataBusKey.WX_SHARE_BACK,Integer.class).postValue(ShareResultType.SHARE_FAIL);
        finish();
    }

    @Override
    public void onCancel() {
        LiveDataBus.get().with(LiveDataBusKey.WX_SHARE_BACK,Integer.class).postValue(ShareResultType.SHARE_SUCCESS);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
