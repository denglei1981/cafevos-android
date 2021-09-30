package com.changanford.common.sharelib.bean;


import android.graphics.Bitmap;

import com.changanford.common.sharelib.util.MediaType;


/**
 * File description.
 *
 * 小程序分享结构体
 * @author lihongjun
 * @date 2018/3/7
 */

public class ShareMiniApp implements IMediaObject {

    private int plamform;
    private String miniRoutineId;
    private String miniRoutinePath;
    private String title; // 标题
    private String content; // 内容
    private String targeUrl; // 跳转链接
    private String imageUrl; // 图片地址
    private Bitmap thumbBitmap; // 缩略图
    private byte[] thumbByte; // 缩略图数据

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

    public ShareMiniApp(int plamform, String miniRoutineId, String miniRoutinePath, String title, String content, String targeUrl,String url) {
        this.plamform = plamform;
        this.miniRoutineId = miniRoutineId;
        this.miniRoutinePath = miniRoutinePath;
        this.title = title;
        this.content = content;
        this.targeUrl = targeUrl;
        this.imageUrl = url;

    }

    public String getMiniRoutineId() {
        return miniRoutineId;
    }

    public void setMiniRoutineId(String miniRoutineId) {
        this.miniRoutineId = miniRoutineId;
    }

    public String getMiniRoutinePath() {
        return miniRoutinePath;
    }

    public void setMiniRoutinePath(String miniRoutinePath) {
        this.miniRoutinePath = miniRoutinePath;
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

    public String getTargeUrl() {
        return targeUrl;
    }

    public void setTargeUrl(String targeUrl) {
        this.targeUrl = targeUrl;
    }

    /**
     * 分享内容类型
     *
     * @return
     */
    @Override
    public int getMediaType() {
        return MediaType.MEDIA_TYPE_MINI_APP;
    }

    public void setPlamform(int plamform) {
        this.plamform = plamform;
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
