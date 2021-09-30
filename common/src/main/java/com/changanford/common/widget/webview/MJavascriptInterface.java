package com.changanford.common.widget.webview;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;

import com.alibaba.android.arouter.launcher.ARouter;
import com.changanford.common.bean.MediaListBean;
import com.changanford.common.router.path.ARouterCirclePath;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/1/16.
 */

public class MJavascriptInterface {
    private Activity activity;
    private List<String> list_imgs = new ArrayList<>();
    private int index = 0;

    public MJavascriptInterface(Activity activity){

        this.activity = activity;
    }

    @JavascriptInterface
    public void openImage(String img, String[] imageUrls) {

        Bundle bundle = new Bundle();
        bundle.putStringArray("imageUrls",imageUrls);
        List<MediaListBean> mediaListBeans = new ArrayList<>();
        int count =0;
        if (imageUrls.length>0){
            for (int i = 0; i < imageUrls.length; i++) {
                MediaListBean mediaListBean = new MediaListBean();
                mediaListBean.setImg_url(imageUrls[i]);
                mediaListBeans.add(mediaListBean);
                if (img.equals(imageUrls[i])){
                    count = i;
                }
            }
        }
        bundle.putSerializable("imgList", (Serializable) mediaListBeans);
        bundle.putString("curImageUrl",img);
        bundle.putInt("count",count);
        ARouter.getInstance().build(ARouterCirclePath.PhotoViewActivity).with(bundle).navigation();
    }
}
