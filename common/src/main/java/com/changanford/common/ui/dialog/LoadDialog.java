package com.changanford.common.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.changanford.common.R;



/**
 * Created by Administrator on 2016/7/29.
 */
public class LoadDialog extends Dialog {
    private TextView textView;//内容
    private ImageView loadingView;
    private Animation animation;
    private TextView tvprogress;
    private Handler handler = new Handler(Looper.getMainLooper());
    public LoadDialog(Context context) {
        super(context, R.style.BankDialog);
        View rootView = LayoutInflater.from(context).inflate(R.layout.os_loading_dialog, null);
        textView = (TextView) rootView.findViewById(R.id.loading_text);
        loadingView = (ImageView) rootView.findViewById(R.id.juhua);
        tvprogress  =rootView.findViewById(R.id.tv_progress);
        animation = AnimationUtils.loadAnimation(context, R.anim.xuanzhuan);
        animation.setInterpolator(new LinearInterpolator());
        setContentView(rootView);
        setXiaomi();
    }
    private void setXiaomi(){
        //设置小米手机无法显示Dialog
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        android.view.WindowManager.LayoutParams p = this.getWindow().getAttributes();  //获取对话框当前的参数值
        p.width = (int) (dm.widthPixels*0.3);
        p.gravity = Gravity.CENTER;
        this.getWindow().setAttributes(p);     //设置生效
    }
    public void setLoadingText(String text){
        textView.setText(text);
    }

    public  void setTvprogress(String str){
        tvprogress.setVisibility(View.VISIBLE);
        tvprogress.setText(str);
    }
    @Override
    public void show() {
        loadingView.startAnimation(animation);
        setCanceledOnTouchOutside(false);
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