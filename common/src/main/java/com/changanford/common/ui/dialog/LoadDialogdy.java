package com.changanford.common.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.changanford.common.R;


/**
 * Created by Administrator on 2016/7/29.
 */
public class LoadDialogdy extends Dialog {

    DYLoadingView mDYLoadingView;
    private  int time =5000;
    private Handler handler = new Handler(Looper.getMainLooper());
    public LoadDialogdy(Context context) {
        super(context, R.style.BankDialog);
        View rootView = LayoutInflater.from(context).inflate(R.layout.loading, null);
        mDYLoadingView = rootView.findViewById(R.id.dy3);
        setContentView(rootView);
    }
    public LoadDialogdy(Context context, int mtime) {
        super(context, R.style.BankDialog);
        time = mtime;
        View rootView = LayoutInflater.from(context).inflate(R.layout.loading, null);
        mDYLoadingView = rootView.findViewById(R.id.dy3);
        setContentView(rootView);
    }

    @Override
    public void show() {
        setCanceledOnTouchOutside(false);
        mDYLoadingView.start();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isShowing()) {
                        mDYLoadingView.stop();
                        LoadDialogdy.this.dismiss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },time);

        super.show();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}