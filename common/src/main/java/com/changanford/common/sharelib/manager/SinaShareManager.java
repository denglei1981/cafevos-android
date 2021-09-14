package com.changanford.common.sharelib.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.changanford.common.sharelib.ModuleConfigureConstant;
import com.changanford.common.sharelib.activity.SinaShareActivity;
import com.changanford.common.sharelib.base.BaseShareManager;
import com.changanford.common.sharelib.bean.IMediaObject;
import com.changanford.common.sharelib.bean.ShareImage;
import com.changanford.common.sharelib.bean.ShareImageData;
import com.changanford.common.sharelib.bean.ShareImageWithText;
import com.changanford.common.sharelib.bean.ShareWeb;
import com.changanford.common.sharelib.download.IShareImageDownLoad;
import com.changanford.common.sharelib.event.RxBus;
import com.changanford.common.sharelib.event.ShareEventMessage;
import com.changanford.common.sharelib.event.ShareResultType;
import com.changanford.common.sharelib.util.BitmapUtil;
import com.changanford.common.sharelib.util.MediaType;
import com.changanford.common.sharelib.util.ShareImageDataUtil;
import com.changanford.common.sharelib.util.StringUtil;
import com.changanford.common.util.AppUtils;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;


/**
 * File description.
 *
 * @author lihongjun
 * @date 2018/3/7
 */

public class SinaShareManager<T extends IMediaObject> extends BaseShareManager {

    private final String TAG = "SinaShareManager";

    public static byte[] posterData; // 海报主图数据
    public static byte[] posterThumbData; // 海报缩略图数据

    private Observable mExtralBitmapObservable; // 额外图片
    private BiFunction<Bitmap,Bitmap,Bitmap> mBitmapOpreatorFuncation; // 额外操作
    private IShareImageDownLoad mShareImageDownLoad; // 下载模式

    public SinaShareManager(Context context) {
        super(context);
    }

    public SinaShareManager withExtraOpration(Observable extralBitmapObservable, BiFunction<Bitmap,Bitmap,Bitmap> bitmapOpreatorFuncation) {
        mExtralBitmapObservable = extralBitmapObservable;
        mBitmapOpreatorFuncation = bitmapOpreatorFuncation;
        return this;
    }

    /**
     * 自定义下载模式
     * @param shareImageDownLoad
     * @return
     */
    public SinaShareManager withDownLoad(IShareImageDownLoad shareImageDownLoad) {
        mShareImageDownLoad = shareImageDownLoad;
        return this;
    }

    /**
     *
     * @param shareData 分享数据
     */
    public  void share2Weibo(T shareData) {
        if (StringUtil.isEmpt(ModuleConfigureConstant.APP_KEY) || StringUtil.isEmpt(ModuleConfigureConstant.REDIRECT_URL) || StringUtil.isEmpt(ModuleConfigureConstant.SCOPE)) {
            RxBus.getIntanceBus().post(new ShareEventMessage(ShareResultType.SHARE_FAIL));
            return;
        }
        String imageUrl = ""; // 图片地址
        boolean isPoster = false;
        Bitmap bitmap = null;
        if (shareData instanceof ShareWeb) {
            title = ((ShareWeb) shareData).getTitle();
            content = ((ShareWeb) shareData).getContent();
            targetUrl = ((ShareWeb) shareData).getTargetUrl();
            imageUrl = ((ShareWeb) shareData).getImageUrl();
        } else if (shareData instanceof ShareImage) {
            imageUrl = ((ShareImage) shareData).getImageUrl();
            bitmap = ((ShareImage) shareData).getOriginBitmap();
            isPoster = true;
        } else if (shareData instanceof ShareImageWithText) {
            title = ((ShareImageWithText) shareData).getTitle();
            content = ((ShareImageWithText) shareData).getContent();
            targetUrl = ((ShareImageWithText) shareData).getTargetUrl();
            imageUrl = ((ShareImageWithText) shareData).getImageUrl();
            bitmap = ((ShareImageWithText) shareData).getOriginBitmap();
            isPoster = true;
        } else {
            RxBus.getIntanceBus().post(new ShareEventMessage(ShareResultType.SHARE_FAIL));
            return;
        }

        ShareImageData shareImageData = ShareImageDataUtil.create(imageUrl,shareData.getPlamform(),shareData.getMediaType(),bitmap);
        // 分享海报模式才会调用此observer
        Observer saveFileObserver = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                showProgressDialog(false);
            }

            @Override
            public void onNext(String picPath) {
                SinaShareActivity.skipTo(mContext,title,content,targetUrl,null,picPath);
            }

            @Override
            public void onError(Throwable e) {
                hideProgressDialog(false);
                RxBus.getIntanceBus().post(new ShareEventMessage(ShareResultType.SHARE_FAIL));
            }

            @Override
            public void onComplete() {
                hideProgressDialog(false);
            }
        };
        Observer<ShareImageData> result = new Observer<ShareImageData>() {
            @Override
            public void onSubscribe(Disposable d) {
                showProgressDialog(false);
            }

            @Override
            public void onNext(ShareImageData shareImageData) {
                int mediaType = shareData.getMediaType();
                byte[] thumbData = null;
                boolean isLocalPicMode = false;
                switch (mediaType) {
                    case MediaType.MEDIA_TYPE_WEB: {
                        thumbData = shareImageData.getWebPageThumbData();
                        break;
                    }
                    case MediaType.MEDIA_TYPE_IMG:
                    case MediaType.MEDIA_TYPE_IMG_TEXT: {
                        // 如果客户端支持分享本地图片
                        if (AppUtils.isSinaInstalled(mContext)) {
                            isLocalPicMode = true;
                            Bitmap bmp = BitmapFactory.decodeByteArray(shareImageData.getPosterImageData(), 0, shareImageData.getPosterImageData().length);
                            new BitmapUtil().saveBitmapAsFile(bmp,saveFileObserver);
                        } else {
                            posterData = shareImageData.getPosterImageData();
                            posterThumbData = shareImageData.getPosterThumbData();
                        }
                        break;
                    }
                }
                if (!isLocalPicMode) {
                    SinaShareActivity.skipTo(mContext,title,content,targetUrl,thumbData);
                }
            }

            @Override
            public void onError(Throwable e) {
                hideProgressDialog(false);
                RxBus.getIntanceBus().post(new ShareEventMessage(ShareResultType.SHARE_FAIL));
            }

            @Override
            public void onComplete() {
                hideProgressDialog(false);
            }
        };

        if (isPoster) { // 海报模式传入额外操作
            new ContentManager<ShareImageData>(mContext).getShareContent(shareImageData, result
                    ,mShareImageDownLoad,mExtralBitmapObservable,mBitmapOpreatorFuncation);
        } else {
            new ContentManager<ShareImageData>(mContext).getShareContent(shareImageData
                    , result,mShareImageDownLoad);
        }

    }



    /**
     * 释放数据
     */
    public static void onDestory() {
        if (posterData != null) {
            posterData = null;
        }
        if (posterThumbData == null) {
            posterThumbData = null;
        }
    }
}
