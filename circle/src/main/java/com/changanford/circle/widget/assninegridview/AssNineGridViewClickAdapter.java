package com.changanford.circle.widget.assninegridview;

import static com.changanford.common.router.ARouterNavigationKt.startARouter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;


import com.changanford.common.router.path.ARouterCirclePath;

import java.util.List;


/**
 * @author assionhonty
 * Created on 2018/9/19 9:38.
 * Email：assionhonty@126.com
 * Function:
 */
public class AssNineGridViewClickAdapter extends AssNineGridViewAdapter {

    private int statusHeight;

    public AssNineGridViewClickAdapter(Context context, List<ImageInfo> imageInfo) {
        super(context, imageInfo);
        statusHeight = getStatusHeight(context);
    }

    @Override
    public void onImageItemClick(Context context, AssNineGridView angv, int index, List<? extends ImageInfo> imageInfo) {
        for (int i = 0; i < imageInfo.size(); i++) {
            ImageInfo info = imageInfo.get(i);
            View imageView;
            if (i < angv.getMaxSize()) {
                imageView = angv.getChildAt(i);
            } else {
                //如果图片的数量大于显示的数量，则超过部分的返回动画统一退回到最后一个图片的位置
                imageView = angv.getChildAt(angv.getMaxSize() - 1);
            }
            info.imageViewWidth = imageView.getWidth();
            info.imageViewHeight = imageView.getHeight();
            int[] points = new int[2];
            imageView.getLocationInWindow(points);
            info.imageViewX = points[0];
            info.imageViewY = points[1] - statusHeight;
        }
        // TODO 预览
        String topicId = imageInfo.get(0).postId;
        if(!TextUtils.isEmpty(topicId)){
            Bundle bundle =new  Bundle();
            bundle.putString("postsId", imageInfo.get(0).postId);
            startARouter(ARouterCirclePath.PostDetailsActivity, bundle);
        }

    }

    /**
     * 获得状态栏的高度
     */
    public int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }
}
