package com.changanford.circle.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.changanford.circle.widget.assninegridview.AssNineGridView;
import com.changanford.common.utilext.GlideUtils;

/**
 * @author assionhonty
 * Created on 2018/9/19 10:29.
 * Email：assionhonty@126.com
 * Function:
 */
public class GlideImageLoader implements AssNineGridView.ImageLoader{

    @Override
    public void onDisplayImage(Context context, ImageView imageView, String url) {
//        Glide.with(context).load(url).into(imageView);
        GlideUtils.INSTANCE.loadBigImage(url,imageView);
    }

    @Override
    public Bitmap getCacheImage(String url) {
        return null;
    }
}
