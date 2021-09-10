package com.changanford.evos.wxapi;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.changanford.common.util.MConstant;
import com.changanford.common.util.bus.LiveDataBus;
import com.changanford.common.util.bus.LiveDataBusKeyKt;
import com.changanford.evos.R;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";
    public static final String APP_ID = MConstant.WXAPPID;//微信
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
                LiveDataBus.get().with(LiveDataBusKeyKt.WXPAY_RESULT).postValue(0);
//                EventScreen eventScreen = new EventScreen(Constant.WXPAYSUCCESS, "true");
//                EventUtils.getDefault().forward(eventScreen);
            } else if (resp.errCode == -1) {
                LiveDataBus.get().with(LiveDataBusKeyKt.WXPAY_RESULT).postValue(1);
//                EventScreen eventScreen = new EventScreen(Constant.WXPAYSUCCESS, "false");
//                EventUtils.getDefault().forward(eventScreen);
//                T.showShortCenterToast("支付失败");

            } else if (resp.errCode == -2) {
                LiveDataBus.get().with(LiveDataBusKeyKt.WXPAY_RESULT).postValue(2);
//                EventScreen eventScreen = new EventScreen(Constant.WXPAYSUCCESS, "false");
//                EventUtils.getDefault().forward(eventScreen);
//                T.showShortCenterToast("取消支付");
            }
            finish();
        }

    }
}

