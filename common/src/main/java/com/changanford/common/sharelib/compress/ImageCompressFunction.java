package com.changanford.common.sharelib.compress;




import com.changanford.common.sharelib.bean.IShareImageBean;
import com.changanford.common.sharelib.util.MediaType;

import io.reactivex.functions.Function;

/**
 * File description.
 *
 * @author lihongjun
 * @date 2018/1/12
 */

public class ImageCompressFunction {

    /**
     * 根据sharebean 的 mediatype 来选择压缩图片方式
     *
     * @param shareBean 分享数据结构体
     * @param <T> 继承至 IShareBean
     * @return 返回对应的压缩方法
     */
    public static <T extends IShareImageBean> Function getCompressFunction(T shareBean) {
        int mediaType = shareBean.getMediaType();
        Function function = null;
        switch (mediaType) {
            case MediaType.MEDIA_TYPE_IMG:
            case MediaType.MEDIA_TYPE_IMG_TEXT: {
                function = new PosterImageCompressFunction(shareBean);
                break;
            }
            case MediaType.MEDIA_TYPE_MINI_APP: {
                function = new MiniProgramCompressFunction(shareBean);
                break;
            }
            default: {
                function = new WebPageCompressFunction(shareBean);
                break;
            }
        }
        return function;
    }
}
