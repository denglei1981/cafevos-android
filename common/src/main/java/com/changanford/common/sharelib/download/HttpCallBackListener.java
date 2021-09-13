package com.changanford.common.sharelib.download;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2018/11/29.
 */

//自定义一个接口
public interface HttpCallBackListener {
    void onFinish(Bitmap bitmap);
    void onError(Exception e);
}
