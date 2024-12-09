package com.changanford.evos.wxapi;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.changanford.common.util.ConfigUtils;
import com.changanford.common.util.MConstant;
import com.changanford.common.util.bus.LiveDataBus;
import com.changanford.common.util.bus.LiveDataBusKey;
import com.changanford.evos.BuildConfig;
import com.changanford.evos.R;
import com.chinaums.pppay.unify.UnifyPayPlugin;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";
    public static final String APP_ID = ConfigUtils.WXAPPID;//微信
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        api = WXAPIFactory.createWXAPI(this, APP_ID);
        api.handleIntent(getIntent(), this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
//        L.d(TAG, "onPayFinish, errCode = " + resp.errCode);
//        payMoneyTv.setText("¥" + ChooseOrderPayActivity.payMoney);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

            if (resp.errCode == 0) {
                LiveDataBus.get().with(LiveDataBusKey.WXPAY_RESULT).postValue(0);
//                EventScreen eventScreen = new EventScreen(Constant.WXPAYSUCCESS, "true");
//                EventUtils.getDefault().forward(eventScreen);
            } else if (resp.errCode == -1) {
                MConstant.INSTANCE.setPayErrorCode(String.valueOf(resp.errCode));
                LiveDataBus.get().with(LiveDataBusKey.WXPAY_RESULT).postValue(1);
//                EventScreen eventScreen = new EventScreen(Constant.WXPAYSUCCESS, "false");
//                EventUtils.getDefault().forward(eventScreen);
//                T.showShortCenterToast("支付失败");

            } else if (resp.errCode == -2) {
                LiveDataBus.get().with(LiveDataBusKey.WXPAY_RESULT).postValue(2);
//                EventScreen eventScreen = new EventScreen(Constant.WXPAYSUCCESS, "false");
//                EventUtils.getDefault().forward(eventScreen);
//                T.showShortCenterToast("取消支付");
            }
            finish();
        }
        //小程序支付-银商支付
        if (resp.getType() == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
            if(BuildConfig.DEBUG){
                WXLaunchMiniProgram.Resp launchMiniProResp = (WXLaunchMiniProgram.Resp) resp;
                String extraData =launchMiniProResp.extMsg; //对应小程序组件 <button open-type="launchApp"> 中的 app-parameter 属性
                Log.d(TAG,"onResp   ---   " + extraData);
                String msg = "onResp   ---   errStr：" + resp.errStr + " --- errCode： " + resp.errCode + " --- transaction： "
                        + resp.transaction + " --- openId：" + resp.openId + " --- extMsg：" + launchMiniProResp.extMsg;
                Log.d(TAG,msg);
            }
            UnifyPayPlugin.getInstance(this).getWXListener().onResponse(this, resp);
            LiveDataBus.get().with(LiveDataBusKey.WXPAY_RESULT).postValue(Math.abs(resp.errCode));
            finish();
        }
    }
}

