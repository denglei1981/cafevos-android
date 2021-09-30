package com.changanford.common.sharelib.bean;

import android.graphics.Bitmap;

/**
 * Created by lihongjun on 2018/1/17.
 */

public class ShareImageData implements IShareImageBean {

    private String imageUrl; // 图片地址
    private int mediaType; // 内容类型
    private int mPlamForm; // 平台
    private Bitmap originBitmap; // 可以直接传入bitmap进行压缩

    private byte[] posterImageData; // 海报主图
    private byte[] posterThumbData; // 海报缩略图
    private byte[] webPageThumbData; // webpage 缩略图


    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public int getMediaType() {
        return mediaType;
    }

    /**
     * 获取分享平台
     *
     * @return
     */
    @Override
    public int getPlamForm() {
        return mPlamForm;
    }

    public void setPlamForm(int plamForm) {
        mPlamForm = plamForm;
    }

    public void setOriginBitmap(Bitmap originBitmap) {
        this.originBitmap = originBitmap;
    }

    @Override
    public Bitmap getOriginBitmap() {
        return originBitmap;
    }

    @Override
    public void setPosterImageData(byte[] data) {
        this.posterImageData = data;
    }

    @Override
    public void setPosterThumbData(byte[] data) {
        this.posterThumbData = data;
    }

    @Override
    public void setWebPageData(byte[] data) {
        this.webPageThumbData = data;
    }

    public byte[] getPosterImageData() {
        return posterImageData;
    }

    public byte[] getPosterThumbData() {
        return posterThumbData;
    }

    public byte[] getWebPageThumbData() {
        return webPageThumbData;
    }
}
