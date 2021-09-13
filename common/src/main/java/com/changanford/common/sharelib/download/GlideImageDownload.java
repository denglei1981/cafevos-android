package com.changanford.common.sharelib.download;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.changanford.common.R;
import com.changanford.common.basic.BaseApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


/**
 * File description.
 * 默认的gelide图片下载方式
 *
 * @author lihongjun
 * @date 2018/1/12
 */

public class GlideImageDownload implements IShareImageDownLoad {


    @Override
    public Bitmap dowwnload(Context context, String url) throws Exception {
//        return getUrlBitmap(url);
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        if (!TextUtils.isEmpty(url) && (url.startsWith("http") || url.startsWith("https"))) {
           Bitmap bitmap ;
           try{
               bitmap =Glide.with(context)
                       .asBitmap() //必须
                       .load(url)
                       .error( R.mipmap.ic_launcher)
                       .apply(options)
                       .into(800, 800)
                       .get();
           }catch (Exception e){
               bitmap = BitmapFactory.decodeResource(BaseApplication.INSTANT.getResources(),R.mipmap.ic_launcher);
           }
            return bitmap;
        } else {
            return Glide.with(context)
                    .asBitmap() //必须
                    .load(R.mipmap.ic_launcher)
                    .apply(options)
                    .into(800, 800)
                    .get();
        }


    }

    public static Bitmap getBitMBitmap(String urlpath) {
        Bitmap map = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            map = BitmapFactory.decodeStream(in);
            // TODO Auto-generated catch block
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    private Bitmap getUrlBitmap(String url) {
        Bitmap resultBitmap = null;
        Bitmap bm = null;
        try {
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            byte[] bt = getBytes(is);                                         //注释部分换用另外一种方式解码
            bm = BitmapFactory.decodeByteArray(bt, 0, bt.length);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);//bitmap是一张图片
            bm = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
            /*该平方根表示大约缩进zoom倍，实际存储大小会接近32KB，可以自己算一下，就是长乘以宽*/
            float zoom = (float) Math.sqrt(32 * 1024 / (float) baos.toByteArray().length);
            Matrix matrix = new Matrix();
            matrix.setScale(zoom, zoom);
            resultBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            baos.reset();
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            /*压缩到32KB为止*/
            while (baos.toByteArray().length > 32 * 1024) {
                matrix.setScale(0.8f, 0.8f);
                resultBitmap = Bitmap.createBitmap(resultBitmap, 0, 0, resultBitmap.getWidth(), resultBitmap.getHeight(), matrix, true);
                baos.reset();
                resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            }
            return resultBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultBitmap;

    }


    private byte[] getBytes(InputStream is) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len = 0;

        while ((len = is.read(b, 0, 1024)) != -1) {
            baos.write(b, 0, len);
            baos.flush();
        }
        byte[] bytes = baos.toByteArray();
        return bytes;
    }


}



