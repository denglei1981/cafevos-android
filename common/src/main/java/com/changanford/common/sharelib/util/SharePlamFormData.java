package com.changanford.common.sharelib.util;

import android.graphics.Bitmap;

import com.changanford.common.sharelib.bean.IMediaObject;
import com.changanford.common.sharelib.bean.ShareImage;
import com.changanford.common.sharelib.bean.ShareImageWithText;
import com.changanford.common.sharelib.bean.ShareMiniApp;
import com.changanford.common.sharelib.bean.ShareWeb;

import java.util.ArrayList;
import java.util.List;

/**
 * File description.
 * 分享数据集合
 *
 * @author lihongjun
 * @date 2018/3/15
 */

public class SharePlamFormData {

    private List<? super IMediaObject> mPlamFormDatas; //分享平台集合数据


    public SharePlamFormData() {
        mPlamFormDatas = new ArrayList<>();
    }

    /**
     * 获取平台数据
     *
     * @return
     */
    public List<? super IMediaObject> getPlamFormDatas() {
        if (mPlamFormDatas == null) return new ArrayList<>();
        else return mPlamFormDatas;
    }

    /**
     * 添加微信好友分享数据
     * @param wxChatMessageBuilder
     * @return
     */
    public SharePlamFormData withWxChatMessageBuilder(WxChatMessageBuilder wxChatMessageBuilder) {
        mPlamFormDatas.add(wxChatMessageBuilder.build());
        return this;
    }

    /**
     * 添加微信朋友圈数据
     * @param wxMomentBuilder
     * @return
     */
    public SharePlamFormData withWxMomentMessageBuilder(WxMomentMessageBuilder wxMomentBuilder) {
        mPlamFormDatas.add(wxMomentBuilder.build());
        return this;
    }

    /**
     * 添加新浪微博数据
     * @param sinaBuilder
     * @return
     */
    public SharePlamFormData withSinaMessageBuilder(SinaMessageBuilder sinaBuilder) {
        mPlamFormDatas.add(sinaBuilder.build());
        return this;
    }

    /**
     * 添加QQ分享数据
     * @param qqBuilder
     * @return
     */
    public SharePlamFormData withQqMessageBuilder(QQMessageBuilder qqBuilder) {
        mPlamFormDatas.add(qqBuilder.build());
        return this;
    }

    /**
     * 微信好友数据创建
     */
    public static class WxChatMessageBuilder {

        IMediaObject data;

        public WxChatMessageBuilder buidWebMessage(String targetUrl, String imageUrl, String title, String content) {
            data = new ShareWeb(PlamForm.WX_CHAT, targetUrl, imageUrl, title, content);
            return this;
        }

        public WxChatMessageBuilder buildedrImageMessage(String imageUrl, Bitmap originBitmap) {
            data = new ShareImage(PlamForm.WX_CHAT, imageUrl, originBitmap);
            return this;
        }

        public WxChatMessageBuilder buildMiniAppMessage(String miniRoutineId, String miniRoutinePath, String title, String content, String targeUrl,String url) {
            data = new ShareMiniApp(PlamForm.WX_CHAT, miniRoutineId, miniRoutinePath, title, content, targeUrl,url);
            return this;
        }

        public IMediaObject build() {
            return data;
        }
    }

    /**
     * 微信朋友圈数据构造
     */
    public static class WxMomentMessageBuilder {
        IMediaObject data;

        public WxMomentMessageBuilder buidWebMessage(String targetUrl, String imageUrl, String title, String content) {
            data = new ShareWeb(PlamForm.WX_MOUMENT, targetUrl, imageUrl, title, content);
            return this;
        }

        public WxMomentMessageBuilder buildedrImageMessage(String imageUrl, Bitmap originBitmap) {
            data = new ShareImage(PlamForm.WX_MOUMENT, imageUrl, originBitmap);
            return this;
        }
        public WxMomentMessageBuilder buildMiniAppMessage(String miniRoutineId, String miniRoutinePath, String title, String content, String targeUrl,String url) {
            data = new ShareMiniApp(PlamForm.WX_CHAT, miniRoutineId, miniRoutinePath, title, content, targeUrl,url);
            return this;
        }

        public IMediaObject build() {
            return data;
        }
    }

    /**
     * 微博分享消息构造
     */
    public static class SinaMessageBuilder {

        IMediaObject data;

        public SinaMessageBuilder buidWebMessage(String targetUrl, String imageUrl, String title, String content) {
            data = new ShareWeb(PlamForm.SINA, targetUrl, imageUrl, title, content);
            return this;
        }

        public SinaMessageBuilder buildedrImageMessage(String imageUrl, Bitmap originBitmap) {
            data = new ShareImage(PlamForm.SINA, imageUrl, originBitmap);
            return this;
        }

        /**
         * 创建图文分享数据
         *
         * @param title     标题
         * @param content   内容
         * @param targetUrl 跳转链接
         * @param imageUrl  图片地址
         * @return
         */
        public SinaMessageBuilder builderImageWithTextMessage(String title, String content, String targetUrl, String imageUrl) {
            data = new ShareImageWithText(PlamForm.SINA, title, content, targetUrl, imageUrl);
            return this;
        }

        public IMediaObject build() {
            return data;
        }

    }

    /**
     * QQ 分享消息构造
     */
    public static class QQMessageBuilder {

        IMediaObject data;

        public QQMessageBuilder buidWebMessage(String targetUrl, String imageUrl, String title, String content) {
            data = new ShareWeb(PlamForm.QQ, targetUrl, imageUrl, title, content);
            return this;
        }

        public QQMessageBuilder buidWebMessagezoom(String targetUrl, String imageUrl, String title, String content) {
            data = new ShareWeb(PlamForm.QQZOOM, targetUrl, imageUrl, title, content);
            return this;
        }

        public QQMessageBuilder buildedrImageMessage(String imageUrl, Bitmap originBitmap) {
            data = new ShareImage(PlamForm.QQ, imageUrl, originBitmap);
            return this;
        }

        public QQMessageBuilder buildedrImageMessagezoom(String imageUrl, Bitmap originBitmap) {
            data = new ShareImage(PlamForm.QQZOOM, imageUrl, originBitmap);
            return this;
        }

        public IMediaObject build() {
            return data;
        }
    }
}
