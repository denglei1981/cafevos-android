package com.changanford.common.sharelib.bean;

import android.graphics.Bitmap;

import com.changanford.common.sharelib.util.MediaType;


/**
 * Created by lihongjun on 2018/1/24.
 * 连接类型
 */

public class ShareWeb implements IMediaObject {

    private int plamform; // 平台名称
    private String targetUrl; // 跳转目标连接
    private String imageUrl; // 图片地址
    private Bitmap thumbBitmap; // 缩略图
    private byte[] thumbByte; // 缩略图数据
    private String title; // 标题
    private String content; // 内容

    public ShareWeb(int plamform, String targetUrl, String imageUrl,String title,String content) {
        this.plamform = plamform;
        this.targetUrl = targetUrl;
        this.imageUrl = imageUrl;
        this.title = title;
        this.content = content;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Bitmap getThumbBitmap() {
        return thumbBitmap;
    }

    public void setThumbBitmap(Bitmap thumbBitmap) {
        this.thumbBitmap = thumbBitmap;
    }

    public byte[] getThumbByte() {
        return thumbByte;
    }

    public void setThumbByte(byte[] thumbByte) {
        this.thumbByte = thumbByte;
    }

    @Override
    public int getMediaType() {
        return MediaType.MEDIA_TYPE_WEB;
    }

    public void setPlamform(int plamform) {
        this.plamform = plamform;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 分享平台
     *
     * @return
     */
    @Override
    public int getPlamform() {
        return plamform;
    }
}
