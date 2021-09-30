package com.changanford.common.sharelib.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.changanford.common.sharelib.ModuleConfigureConstant;
import com.changanford.common.sharelib.event.RxBus;
import com.changanford.common.sharelib.event.ShareEventMessage;
import com.changanford.common.sharelib.event.ShareResultType;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


/**
 * 微信分享回调Activty
 */
public class WxShareCallBackActivity extends Activity implements IWXAPIEventHandler {
    /**
     *
     */
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initData();
    }

    /**
     * 初始化
     */
    private void initData() {
        api = WXAPIFactory.createWXAPI(this, ModuleConfigureConstant.WECHAT_APPID, true);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        int result;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = ShareResultType.SHARE_SUCCESS;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = ShareResultType.SHARE_CANCLE;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = ShareResultType.SHARE_FAIL;
                break;
            default:
                result = ShareResultType.SHARE_FAIL;
                break;
        }
        RxBus.getIntanceBus().post(new ShareEventMessage(result));
        this.finish();
    }
}
