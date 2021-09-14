package com.changanford.common.sharelib.compress;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.changanford.common.sharelib.bean.IShareImageBean;
import com.changanford.common.sharelib.util.BitmapUtil;

import io.reactivex.Observable;
import io.reactivex.functions.Function;


/**
 * File description.
 * 海报图片压缩
 * @author lihongjun
 * @date 2018/1/12
 */

public class PosterImageCompressFunction <T extends IShareImageBean> implements Function<Bitmap, Observable<T>> {

    private static final String TAG = "PosterImageCompress";
    private T mShareBean;

    public PosterImageCompressFunction(T shareBean) {
        mShareBean = shareBean;
    }

    @Override
    public Observable<T> apply(Bitmap originBitmap) throws Exception {
        BitmapUtil bitmapUtil = new BitmapUtil();
        bitmapUtil.setCompressStop(true);
        byte[] data = null;
        byte[] thumbData = null;
        if (originBitmap != null) { // 主图缩放和压缩
            if (originBitmap.getWidth() != 1080) { // 缩放海报的宽度到1080px
                Matrix matrix = new Matrix();
                float scale = 1080f / (float) originBitmap.getWidth();
                matrix.postScale(scale, scale);
                originBitmap = Bitmap.createBitmap(originBitmap, 0, 0, originBitmap.getWidth(), originBitmap.getHeight(), matrix, false);
            }
            data = bitmapUtil.getScaledImageBytes(originBitmap, BitmapUtil.MAX_IMAGE_DATA_LENGTH);
            mShareBean.setPosterImageData(data);
            originBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        // 缩略图压缩
        if (originBitmap != null) {
            thumbData = bitmapUtil.getImageThumbnail(originBitmap);
            mShareBean.setPosterThumbData(thumbData);
        }
        return Observable.just(mShareBean);
    }
}
