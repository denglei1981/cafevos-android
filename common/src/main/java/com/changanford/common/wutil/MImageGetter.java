package com.changanford.common.wutil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

public class MImageGetter implements Html.ImageGetter {
    private Context c;
    private TextView textView;
    private int w;
    public MImageGetter(TextView text, Context c) {
        this.c = c;
        this.textView = text;
        w= ScreenUtils.INSTANCE.getScreenWidth(c)-ScreenUtils.INSTANCE.dp2px(c,40f);
    }
    public MImageGetter(TextView text, Context c, int width) {
        this.c = c;
        this.textView = text;
        w=width;
    }
    @Override
    public Drawable getDrawable(String source) {
        if(TextUtils.isEmpty(source))return null;
        final LevelListDrawable drawable = new LevelListDrawable();
        if(source.contains("http")){
            Glide.with(c).asBitmap().load(source).into(new SimpleTarget<Bitmap>() {
                @RequiresApi(api = Build.VERSION_CODES.P)
                @Override
                public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(c.getResources(),bitmap);
                    drawable.addLevel(1, 1, bitmapDrawable);
                    int h=w*bitmap.getHeight()/bitmap.getWidth();
                    drawable.setBounds(0, 0, w,h);
                    drawable.setLevel(1);
                    textView.invalidate();
                    textView.setText(textView.getText());
                }
            });
        }else {
            Drawable d= ContextCompat.getDrawable(c,Integer.parseInt(source));
            d.setBounds(0, 0, d.getIntrinsicWidth(),d.getIntrinsicHeight());
            drawable.setLevel(1);
            textView.invalidate();
            textView.setText(textView.getText());
            return d;
        }
        return drawable;
    }
}
