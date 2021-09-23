package com.changanford.common.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.changanford.common.bean.MediaListBean;
import com.danikula.videocache.HttpProxyCacheServer;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Kevin on 2018/9/10.
 */

public class PhotoImageAdapter extends PagerAdapter {

    public static final String TAG = PhotoImageAdapter.class.getSimpleName();
    private ArrayList<MediaListBean> imageUrls;
    private AppCompatActivity activity;
//    private IjkVideoView jvpInformation;


    public PhotoImageAdapter(ArrayList<MediaListBean> imageUrls, AppCompatActivity activity) {
        this.imageUrls = imageUrls;
        this.activity = activity;
    }

//    public IjkVideoView getfirstview() {
//        if (imageUrls != null && imageUrls.size() > 0) {
//            for (int i = 0; i < imageUrls.size(); i++) {
//                if (!TextUtils.isEmpty(imageUrls.get(i).getVideoUrl())) {
//                    return jvpInformation;
//                }
//            }
//        }
//        return null;
//    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        String url = imageUrls.get(position).getImg_url();
        if (url.contains(".gif")) {
            GifImageView gifImageView = new GifImageView(activity);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            gifImageView.setLayoutParams(layoutParams);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            Glide.with(activity).asGif().load(url).apply(requestOptions).into(gifImageView);
            Glide.with(activity).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource,  Transition<? super Bitmap> transition) {
                    imageUrls.get(position).setBitmap(resource);
                }
            });
            gifImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.finish();
                }
            });
            container.addView(gifImageView);
            return gifImageView;
        }
//        else if (!TextUtils.isEmpty(imageUrls.get(position).getVideoUrl())) {
//
//            View view = activity.getLayoutInflater().inflate(R.layout.layoutjzvideo, null);
//            jvpInformation = view.findViewById(R.id.jz_video);
//            initPlayer(imageUrls.get(position).getVideoUrl(), jvpInformation);
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    activity.finish();
//                }
//            });
//            container.addView(view);
//            return view;
//        }
        else {
            SubsamplingScaleImageView s = new SubsamplingScaleImageView(activity);
            Glide.with(activity).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource,  Transition<? super Bitmap> transition) {
                    imageUrls.get(position).setBitmap(resource);
                    float initImageScale = getInitImageScale(resource);
                    s.setMaxScale(initImageScale + 2.0f);//最大显示比例
                    s.setImage(ImageSource.bitmap(resource), new ImageViewState(initImageScale, new PointF(0, 0), 0));
                }
            });
            s.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.finish();
                }
            });
            container.addView(s);
            return s;
        }
    }



    @Override
    public int getCount() {
        return imageUrls != null ? imageUrls.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    public float getInitImageScale(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        WindowManager wm = activity.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        // 拿到图片的宽和高
        int dw = bitmap.getWidth();
        int dh = bitmap.getHeight();
        float scale = 1.0f;
        //图片宽度大于屏幕，但高度小于屏幕，则缩小图片至填满屏幕宽
        if (dw > width && dh <= height) {
            scale = width * 1.0f / dw;
        }
        //图片宽度小于屏幕，但高度大于屏幕，则放大图片至填满屏幕宽
        if (dw <= width && dh > height) {
            scale = width * 1.0f / dw;
        }
        //图片高度和宽度都小于屏幕，则放大图片至填满屏幕宽
        if (dw < width && dh < height) {
            scale = width * 1.0f / dw;
        }
        //图片高度和宽度都大于屏幕，则缩小图片至填满屏幕宽
        if (dw > width && dh > height) {
            scale = width * 1.0f / dw;
        }
        return scale;
    }

    public float getInitImageScale(Bitmap mbitmap) {
        Bitmap bitmap = mbitmap;
        WindowManager wm = activity.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        // 拿到图片的宽和高
        int dw = bitmap.getWidth();
        int dh = bitmap.getHeight();
        float scale = 1.0f;
        //图片宽度大于屏幕，但高度小于屏幕，则缩小图片至填满屏幕宽
        if (dw > width && dh <= height) {
            scale = width * 1.0f / dw;
        }
        //图片宽度小于屏幕，但高度大于屏幕，则放大图片至填满屏幕宽
        if (dw <= width && dh > height) {
            scale = width * 1.0f / dw;
        }
        //图片高度和宽度都小于屏幕，则放大图片至填满屏幕宽
        if (dw < width && dh < height) {
            scale = width * 1.0f / dw;
        }
        //图片高度和宽度都大于屏幕，则缩小图片至填满屏幕宽
        if (dw > width && dh > height) {
            scale = width * 1.0f / dw;
        }
        return scale;
    }

//    private void initPlayer(String videoUrl, IjkVideoView player) {
//        HttpProxyCacheServer proxy = MyApp.getProxy(MyApp.mContext);
//        if (player != null) {
//            String proxyUrl = proxy.getProxyUrl(videoUrl);
//            player.setUrl(videoUrl);
//            StandardVideoController controller = new StandardVideoController(player.getContext());
//            player.setVideoController(controller);
//        }
//    }


}
