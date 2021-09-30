package com.changanford.common.sharelib.bean;

import android.graphics.Bitmap;

/**
 * Created by lihongjun on 2018/1/12.
 */

public interface IShareImageBean {

    /**
     * 分享内容类型
     * @return 内容类型
     */
    int getMediaType();

    /**
     * 获取分享平台
     * @return
     */
    int getPlamForm();

    /**
     * 海报或者webpage 图片地址
     * @return
     */
    String getImageUrl();

    /**
     * 获取主图 如果有此图 直接进行压缩 不再进行下载
     */
    Bitmap getOriginBitmap();

    /**
     * 设置海报数据
     * @param data
     */
    void setPosterImageData(byte[] data);

    /**
     * 设置海报缩略图
     * @param data
     */
    void setPosterThumbData(byte[] data);

    /**
     * 设置webpage模式
     * @param data
     */
    void setWebPageData(byte[] data);

}
