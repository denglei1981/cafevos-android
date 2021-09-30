package com.changanford.common.sharelib.compress;

import android.graphics.Bitmap;

import com.changanford.common.sharelib.bean.IShareImageBean;
import com.changanford.common.sharelib.util.BitmapUtil;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * File description.
 * 小程序压缩图片方法
 * @author lihongjun
 * @date 2018/1/30
 * @param <T> 继承至 IShareBean
 */

public class MiniProgramCompressFunction<T extends IShareImageBean> implements Function<Bitmap, Observable<T>> {

    private static final String TAG = "PosterImageCompress";
    private T mShareBean;

    public MiniProgramCompressFunction(T shareBean) {
        mShareBean = shareBean;
    }

    @Override
    public Observable<T> apply(Bitmap originBitmap) throws Exception {
        BitmapUtil bitmapUtil = new BitmapUtil();
        if (originBitmap != null) {
            byte[] result = bitmapUtil.getScaledImageBytes(originBitmap,BitmapUtil.MAX_MINI_THUMBNAIL_DATA_LENGTH);
            mShareBean.setWebPageData(result);
        }
        return Observable.just(mShareBean);
    }
}
