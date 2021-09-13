package com.changanford.common.sharelib.download;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * 分享图片下载通用方法
 * Created by lihongjun on 2018/1/12.
 */

public interface IShareImageDownLoad {

    Bitmap dowwnload(Context context, String url) throws Exception;
}
