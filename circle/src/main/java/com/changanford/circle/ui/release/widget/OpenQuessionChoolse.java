package com.changanford.circle.ui.release.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;


import com.changanford.circle.R;
import com.changanford.circle.databinding.OpenquessionchooseBinding;

import razerdp.basepopup.BasePopupWindow;

/**
 * 相册 相机选择
 */
public class OpenQuessionChoolse extends BasePopupWindow implements View.OnClickListener {

    LinearLayout lldanxuan;
    LinearLayout llduoxuan;
    LinearLayout llwenben;
    TextView tvquxiao;
    public OpenQuessionChoolseCallBack commonCallBack;

    int chooseType = 0; //1 单选 2多选 3 文本
    public OpenQuessionChoolse(Context context, OpenQuessionChoolseCallBack OpenCarcmeCallBackCallBack) {
        super(context);
        OpenquessionchooseBinding binding = DataBindingUtil.bind(createPopupById(R.layout.openquessionchoose));
        setContentView(binding.getRoot());
        this.commonCallBack = OpenCarcmeCallBackCallBack;
        setPopupGravity(Gravity.BOTTOM);
        bindEvent();
    }



    private void bindEvent() {
        lldanxuan = findViewById(R.id.ll_danxuan);
        llduoxuan = findViewById(R.id.llduoxuan);
        llwenben = findViewById(R.id.llwenben);
        tvquxiao = findViewById(R.id.tv_quxiao);
        lldanxuan.setOnClickListener(this);
        llduoxuan.setOnClickListener(this);
        llwenben.setOnClickListener(this);
        tvquxiao.setOnClickListener(this);
    }




    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_danxuan) {
            dismiss(false);
            commonCallBack.OnItemClick(1);
        } else if (id == R.id.llduoxuan) {
            dismiss(false);
            commonCallBack.OnItemClick(2);
        }else if(id ==R.id.llwenben){
            dismiss(false);
            commonCallBack.OnItemClick(3);
        }else {
            dismiss(false);
        }
    }

    public interface OpenQuessionChoolseCallBack {
        void OnItemClick(int Choolsetype);  //点击打开相册
    }
}
