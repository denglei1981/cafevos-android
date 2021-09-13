package com.changanford.common.sharelib.bean;

import android.graphics.Bitmap;

import com.changanford.common.sharelib.util.MediaType;


/**
 * File description.
 *
 * @author lihongjun
 * @date 2018/3/12
 */

public class ShareImageWithText implements IMediaObject {

    private int plamFrom; // 平台
    private String title; // 标题
    private String content; // 内容
    private String targetUrl; // 跳转目标连接

    private String imageUrl; // 图片地址
    private Bitmap originBitmap; // 主图
    private Bitmap thumbBitmap; // 缩略图
    private byte[] originByte;
    private byte[] thumbByte;

    public ShareImageWithText(int plamform,String title,String content,String targetUrl,String imageUrl) {
        this(plamform,title,content,targetUrl,imageUrl,null);
    }

    public ShareImageWithText(int plamform,String title,String content,String targetUrl,String imageUrl,Bitmap originBitmap) {
        this.plamFrom = plamform;
        this.title = title;
        this.content = content;
        this.targetUrl = targetUrl;
        this.imageUrl = imageUrl;
        this.originBitmap = originBitmap;
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

    public Bitmap getOriginBitmap() {
        return originBitmap;
    }

    public void setOriginBitmap(Bitmap originBitmap) {
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
        return MediaType.MEDIA_TYPE_IMG_TEXT;
    }

    /**
     * 分享平台
     *
     * @return
     */
    @Override
    public int getPlamform() {
        return plamFrom;
    }
}
