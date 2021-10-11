package com.changanford.circle.ui.release.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;


import com.changanford.circle.R;
import com.changanford.circle.databinding.OpencarcmeBinding;

import razerdp.basepopup.BasePopupWindow;

/**
 * 相册 相机选择
 */
public class OpenCarcme extends BasePopupWindow implements View.OnClickListener {

    TextView tvxiangce;
    TextView tvxiangji;
    TextView tvquxiao;
    public OpenCarcmeCallBack commonCallBack;

    public OpenCarcme(Context context, OpenCarcmeCallBack OpenCarcmeCallBackCallBack) {
        super(context);
        OpencarcmeBinding binding =
                DataBindingUtil.bind(createPopupById(R.layout.opencarcme));
        setContentView(binding.getRoot());
        this.commonCallBack = OpenCarcmeCallBackCallBack;
        setPopupGravity(Gravity.BOTTOM);
        bindEvent();
    }



    private void bindEvent() {
        tvxiangce = findViewById(R.id.tv_xiangce);
        tvxiangji = findViewById(R.id.tv_xiangji);
        tvquxiao = findViewById(R.id.tvqx);
        tvxiangce.setOnClickListener(this);
        tvxiangji.setOnClickListener(this);
        tvquxiao.setOnClickListener(this);
    }




    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tvqx) {
            dismiss();
        } else if (id == R.id.tv_xiangji) {
            commonCallBack.onCarcme();


            dismiss();
        }else if(id ==R.id.tv_xiangce){
            commonCallBack.onPicBack();
            dismiss();
        }
    }

    public interface OpenCarcmeCallBack {
        void onPicBack();  //点击打开相册

        void onCarcme(); //点击打开相机

    }
}
