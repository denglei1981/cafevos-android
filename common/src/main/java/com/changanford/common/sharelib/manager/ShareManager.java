package com.changanford.common.sharelib.manager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.Window;

import com.changanford.common.sharelib.ModuleConfigureConstant;
import com.changanford.common.sharelib.bean.IMediaObject;
import com.changanford.common.sharelib.download.GlideImageDownload;
import com.changanford.common.sharelib.download.IShareImageDownLoad;
import com.changanford.common.sharelib.download.ImageDownLoadFactory;
import com.changanford.common.sharelib.ui.ShareDialog;
import com.changanford.common.sharelib.util.PlamForm;
import com.changanford.common.util.AppUtils;
import com.changanford.common.util.ConfigUtils;
import com.changanford.common.utilext.ToastUtilsKt;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;

/**
 * Created by lihongjun on 2018/1/24.
 */

public  class ShareManager <T extends IMediaObject> implements ShareDialog.OnPlamFormClickListener{

    private Activity mActivity;
    private List<T> mMediaObjects; // 分享平台数据
    private Observable mExtralBitmapObservable; // 额外图片
    private BiFunction<Bitmap,Bitmap,Bitmap> mBitmapOpreatorFuncation; // 额外操作

    private ShareDialog.OnPlamFormClickListener mPlamFormClickListener; // 分享平台回调

    private IShareImageDownLoad mShareImageDownLoad; // 下载模式
    private ShareDialog mShareDialog; // 分享对话框


    public  ShareManager(Activity activity,int Type,boolean showpicshare) {
        mActivity = activity;
        ConfigUtils.CLASSNAME =mActivity.getLocalClassName();
        try {
            mShareImageDownLoad = ImageDownLoadFactory.factory(GlideImageDownload.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaObjects = new ArrayList<>();
        mShareDialog = new ShareDialog(mActivity,Type,showpicshare);
        mShareDialog.setPlamFormClickListener(this);
    }


    /**
     * 初始化微信sdkkey
     * @param wxAppId
     */
    public static void initWxShareSdk(String wxAppId) {
        ModuleConfigureConstant.APP_KEY = wxAppId;
    }

    /**
     * 初始化微博sdk
     * @param wbAppKey
     * @param redirctUrl
     * @param scrop
     */
    public static void initSinaSdk(String wbAppKey,String redirctUrl,String scrop) {
        ModuleConfigureConstant.APP_KEY = wbAppKey;
        ModuleConfigureConstant.REDIRECT_URL = redirctUrl;
        ModuleConfigureConstant.SCOPE = "email,direct_messages_read,direct_messages_write,"
                + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                + "follow_app_official_microblog," + "invitation_write";
    }

    /**
     * 初始化qqsdk
     * @param qqAppId
     * @param appName
     */
    public static void initQqSdk(String qqAppId,String appName) {
        ModuleConfigureConstant.QQ_APPID = qqAppId;
        ModuleConfigureConstant.APP_NAME = appName;
    }

    /**
     * 获取分享对话框实例
     * @return
     */
    public ShareDialog getShareDialog() {
        return mShareDialog;
    }

    /**
     * 分享平台数据
     * @param plamFormData
     * @return
     */
    public ShareManager withPlamFormData(List<T> plamFormData) {
        mMediaObjects = plamFormData;
        return this;
    }

    /**
     * 更新某平台数据
     * @param plamFormData
     */
    public void updataPlamForm(T plamFormData) {
        if (plamFormData == null) {
            return;
        }
        boolean hase = false; // 是否已经存在这个分享平台
        for (T mediaData: mMediaObjects) {
            if (mediaData.getPlamform() == plamFormData.getPlamform()) {
                mMediaObjects.remove(mediaData);
                mMediaObjects.add(plamFormData);
                hase = true;
                break;
            }
        }
        if (!hase) {
            mMediaObjects.add(plamFormData);
        }
    }

    /**
     * 自定义分享控件view
     * @param view
     * @return
     */
    public ShareManager withCustomDialogView(View view) {
        if (view != null && mShareDialog != null) {
            mShareDialog.setCustomView(view);
        }
        return this;
    }

    /**
     * 分享对话框模型
     * @param dimAmount
     * @return
     */
    public ShareManager withDialogDimAmount(float dimAmount) {
        mShareDialog.setDimAmount(dimAmount);
        return this;
    }

    /**
     * 自定义下载模式
     * @param shareImageDownLoad
     * @return
     */
    public ShareManager withDownload(IShareImageDownLoad shareImageDownLoad) {
        mShareImageDownLoad = shareImageDownLoad;
        return this;
    }

    /**
     * 海报额外操作
     * @param extralBitmapObservable
     * @param bitmapOpreatorFuncation
     * @return
     */
    public ShareManager withExtralOperator(Observable extralBitmapObservable, BiFunction<Bitmap,Bitmap,Bitmap> bitmapOpreatorFuncation) {
        mExtralBitmapObservable = extralBitmapObservable;
        mBitmapOpreatorFuncation = bitmapOpreatorFuncation;
        return this;
    }

    /**
     * 点击分享平台回调
     * @param onPlamFormClickListener
     * @return
     */
    public ShareManager withPlamformClickListener(ShareDialog.OnPlamFormClickListener onPlamFormClickListener) {
        mPlamFormClickListener = onPlamFormClickListener;
        return this;
    }

    /**
     * 发起分享
     */
    public void share() {
        T mediaObject = mMediaObjects.get(0);
        int plamform = mediaObject.getPlamform();
        switch (plamform) {
            case PlamForm.WX_CHAT: // 微信好友
            case PlamForm.WX_MOUMENT: //微信朋友圈
                shareToWx(mediaObject);
                break;
            case PlamForm.SINA: // 新浪微博
                share2Weibo(mediaObject);
                break;
        }
    }

    /**
     * 打开分享控件
     */
    public void open() {
        mShareDialog.setPlamforms(mMediaObjects);
        Window window=  mShareDialog.getWindow();
        mShareDialog.show();
//        window.setWindowAnimations(R.style.dialogsytle);
    }

    /**
     * 点击分享平台回调
     * @param view     点击的View
     * @param plamForm 点击的平台
     */
    @Override
    public void onPlamFormClick(View view, int plamForm) {
        if (mPlamFormClickListener != null) {
            mPlamFormClickListener.onPlamFormClick(view,plamForm);
        }
        T mediaData = getMediaData(plamForm);
        switch (plamForm) {
            case PlamForm.WX_MOUMENT:
            case PlamForm.WX_CHAT:
                if (!AppUtils.isWeixinAvilible(view.getContext())){
                    ToastUtilsKt.toastShow("请先安装微信");
                    return;
                }
                shareToWx(mediaData);
                mShareDialog.dismiss();
                break;
            case PlamForm.SINA:
                if (!AppUtils.isSinaInstalled(view.getContext())){
                    ToastUtilsKt.toastShow("请先安装新浪微博");
                    return;
                }

                share2Weibo(mediaData);
                mShareDialog.dismiss();
                break;
            case PlamForm.QQ:
                if (!AppUtils.isQQClientAvailable(view.getContext())){
                    ToastUtilsKt.toastShow("请先安装QQ");
                    return;
                }
                new QQShareManager(mActivity)
                        .withDownLoad(mShareImageDownLoad)
                        .withExtraOpration(mExtralBitmapObservable,mBitmapOpreatorFuncation)
                        .share2Qq(mediaData);
                mShareDialog.dismiss();
                break;
            case PlamForm.QQZOOM:
                if (!AppUtils.isQQClientAvailable(view.getContext())){
                    ToastUtilsKt.toastShow("请先安装QQ");
                    return;
                }
                new QQShareManager(mActivity)
                        .withDownLoad(mShareImageDownLoad)
                        .withExtraOpration(mExtralBitmapObservable,mBitmapOpreatorFuncation)
                        .share2Qqzoom(mediaData);
                mShareDialog.dismiss();
                break;
        }
    }


    /**
     * 分享到微信
     * @param mediaObject
     */
    private void shareToWx(T mediaObject) {
        new WxShareManager(mActivity)
                .withDownload(mShareImageDownLoad)
                .withExtraOpration(mExtralBitmapObservable,mBitmapOpreatorFuncation)
                .shareToWx(mediaObject);
    }

    /**
     * 分享到微博
     * @param mediaObject
     */
    private void share2Weibo(T mediaObject) {
        new SinaShareManager(mActivity)
                .withExtraOpration(mExtralBitmapObservable,mBitmapOpreatorFuncation)
                .withDownLoad(mShareImageDownLoad)
                .share2Weibo(mediaObject);
    }

    /**
     * 获取平台分享数据
     * @param plamform 平台
     * @return 返回的数据
     */
    private T getMediaData(int plamform) {
        T mediaData = null;
        for (T t:mMediaObjects) {
            if (t.getPlamform() == plamform) {
                mediaData = t;
                break;
            }
        }
        return mediaData;
    }
}
