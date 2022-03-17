package com.changanford.evos.ui.pay;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.changanford.common.util.bus.LiveDataBus;
import com.changanford.common.util.bus.LiveDataBusKey;
import com.changanford.evos.BuildConfig;
import com.changanford.evos.R;

import java.util.HashMap;

public class AlipayMiniProgramCallbackActivity extends AppCompatActivity {
    TextView tv;
    HashMap<String,String> payResultCode = new HashMap();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alipay_mini_program_callback);
        tv = findViewById(R.id.tv);
        initPayResultCode();
        showMsg();
    }

    private  void initPayResultCode(){
        payResultCode.put("0000","支付请求发送成功。商户订单是否成功支付应该以商户后台收到支付结果。");
        payResultCode.put("1000","用户取消支付");
        payResultCode.put("1001","参数错误");
        payResultCode.put("1002","网络连接错误");
        payResultCode.put("1003","支付客户端未安装");
        payResultCode.put("2001","订单处理中，支付结果未知(有可能已经支付成功)，请通过后台接口查询订单状态");
        payResultCode.put("2002","订单号重复");
        payResultCode.put("2003","订单支付失败");
        payResultCode.put("9999","其他支付错误");
    }
    private String getResultMsg(String code){
        if(!TextUtils.isEmpty(code)){
            code = code.trim();
        }
        String msg = payResultCode.get(code);
        if(TextUtils.isEmpty(msg)){
            msg = "状态不存在";
        }
        return msg;
    }
    private void showMsg() {
        if(getIntent() != null){
            try {
                Uri uri = getIntent().getData();
                //完整路径
                String url = uri.toString();
                String errCode = uri.getQueryParameter("errCode");
                String errStr = uri.getQueryParameter("errStr");
                String str = "支付结果 ===》 errCode = " + errCode + " ------ errStr = " + errStr + "\n 支付状态 ---> " + getResultMsg(errCode);
                tv.setText("Scheme url="+url+"\n ------------ \n" + str );
                if(BuildConfig.DEBUG) Log.e("PayUtils","Scheme url="+url+"\n ------------ \n" + str);
                boolean isSuccess= errCode.equals("0000");
                LiveDataBus.get().with(LiveDataBusKey.ALIPAY_RESULT).postValue(isSuccess);
                finish();
            }catch (Exception e){
                e.getStackTrace();
                showMsgDialog(e.getMessage());
            }

        }else {
            tv.setText("内容为空的");
        }
    }

    private void showMsgDialog(final String  msg) {
        runOnUiThread(() -> {
            AlertDialog dialog = new AlertDialog.Builder(AlipayMiniProgramCallbackActivity.this).setMessage(msg).
                    setPositiveButton("确定", (dialogInterface, i) -> dialogInterface.dismiss()).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        });
    }


}
