package com.changanford.common.sharelib.bean;

import android.graphics.Bitmap;

import com.changanford.common.sharelib.util.MediaType;


/**
 * Created by lihongjun on 2018/1/24.
 * 海报结构体
 */

public class ShareImage implements IMediaObject {

    private int plamform; // 平台名称
    private String imageUrl; // 图片地址
    private Bitmap originBitmap; // 主图
    private Bitmap thumbBitmap; // 缩略图
    private byte[] originByte;
    private byte[] thumbByte;

    public ShareImage(int plamform,String imageUrl) {
        this(plamform,imageUrl,null);
    }

    public ShareImage(int plamform,String imageUrl,Bitmap originBitmap) {
        this.plamform = plamform;
        this.imageUrl = imageUrl;
        this.originBitmap = originBitmap;
    }

    public int getPlamform() {
        return plamform;
    }

    public void setPlamform(int plamform) {
        this.plamform = plamform;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Bitmap getOriginBitmap() {
        return originBitmap;
    }

    public void setOriginBitma(Bitmap originBitmap) {
        this.originBitmap = originBitmap;
    }

    public Bitmap getThumbBitmap() {
        return thumbBitmap;
    }

    public void setThumbBitmap(Bitmap thumbBitmap) {
        this.thumbBitmap = thumbBitmap;
    }

    public byte[] getOriginByte() {
        return originByte;
    }

    public void setOriginByte(byte[] originByte) {
        this.originByte = originByte;
    }

    public byte[] getThumbByte() {
        return thumbByte;
    }

    public void setThumbByte(byte[] thumbByte) {
        this.thumbByte = thumbByte;
    }

    @Override
    public int getMediaType() {
        return MediaType.MEDIA_TYPE_IMG;
    }
}
