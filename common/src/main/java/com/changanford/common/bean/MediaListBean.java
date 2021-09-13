package com.changanford.common.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Kevin on 2018/9/4.
 */

public class MediaListBean implements Serializable {

    /**
     * img_url : https://testimgchangan.oss-cn-beijing.aliyuncs.com/images/posts_pic/2018/08/153542717127.png
     */

    private String img_url;
    private Bitmap bitmap;

    public MediaListBean(String img_url, Bitmap bitmap, String videoUrl) {
        this.img_url = img_url;
        this.bitmap = bitmap;
        this.videoUrl = videoUrl;
    }

    public MediaListBean(String img_url) {
        this.img_url = img_url;
    }

    public MediaListBean() {
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    private String videoUrl;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }


}
