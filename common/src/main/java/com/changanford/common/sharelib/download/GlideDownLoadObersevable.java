package com.changanford.common.sharelib.download;

import android.content.Context;
import android.graphics.Bitmap;


import com.changanford.common.sharelib.bean.IShareImageBean;

import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * 下载分享的图片数据
 * Created by lihongjun on 2018/1/12.
 */

public class GlideDownLoadObersevable<T extends IShareImageBean> extends Observable {

    private Context mContext;
    private T mShareBean;
    private IShareImageDownLoad mShareImageDownLoad;

    public GlideDownLoadObersevable(Context context,T shareBean,IShareImageDownLoad shareImageDownLoad) {
        mContext = context;
        mShareBean = shareBean;
        mShareImageDownLoad = shareImageDownLoad;
    }

    @Override
    protected void subscribeActual(Observer observer) {
        try {
            Bitmap bitmap = mShareBean.getOriginBitmap();
            if (bitmap == null) { // 如果没有传递图片进来就下载图片 否则直接压缩传递进来的图片
                bitmap = mShareImageDownLoad.dowwnload(mContext,mShareBean.getImageUrl());
            }
            observer.onNext(bitmap);
            observer.onComplete();
        } catch (Exception e) {
            e.printStackTrace();
//            observer.onError(e);
        }
    }
}
