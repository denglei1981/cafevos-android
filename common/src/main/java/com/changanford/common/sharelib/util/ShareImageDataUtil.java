package com.changanford.common.sharelib.util;

import android.graphics.Bitmap;

import com.changanford.common.sharelib.bean.ShareImageData;


/**
 * Created by lihongjun on 2018/1/17.
 */

public class ShareImageDataUtil {

    /**
     * 够着分享imagedata 数据
     * @param url 图片地址
     * @param plamForm 分享平台
     * @param mediaType  分享内容的类型
     * @param originBitmap 可以直接传入bitmap 后续直接进行压缩不用下载图片
     * @return
     */
    public static ShareImageData create(String url, int plamForm, int mediaType, Bitmap originBitmap) {
        ShareImageData data = new ShareImageData();
        data.setImageUrl(url);
        data.setMediaType(mediaType);
        data.setOriginBitmap(originBitmap);
        data.setPlamForm(plamForm);
        return data;
    }
}
