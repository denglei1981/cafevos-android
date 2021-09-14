package com.changanford.common.sharelib.event;

/**
 * File description.
 *
 * 分享结果效果
 * @author lihongjun
 * @date 2018/3/9
 */

public class ShareEventMessage {

    private int mShareResult;

    public ShareEventMessage(int shareResult) {
        mShareResult = shareResult;
    }

    public int getShareResult() {
        return mShareResult;
    }
}
