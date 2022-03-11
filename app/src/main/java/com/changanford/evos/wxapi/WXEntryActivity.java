package com.changanford.evos.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.changanford.common.sharelib.event.ShareResultType;
import com.changanford.common.util.ConfigUtils;
import com.changanford.common.util.bus.LiveDataBus;
import com.changanford.common.util.bus.LiveDataBusKey;
import com.changanford.common.util.toast.ToastUtils;
import com.changanford.evos.BuildConfig;
import com.changanford.evos.MainActivity;
import com.changanford.evos.R;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;

    private String code;
    //    private EventScreen eventScreen;
    IWXAPI api = WXAPIFactory.createWXAPI(this, ConfigUtils.WXAPPID);
    private int result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        switch (baseReq.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                //// TODO: 2018/6/20
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                // 小程序 跳转app
                openApp((ShowMessageFromWX.Req) baseReq);

                break;
            default:
                break;
        }
    }

    /**
     * 小程序跳转app
     */
    public void openApp(ShowMessageFromWX.Req showReq) {
        WXMediaMessage wxMsg = showReq.message;
        WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;

        /*wxMsg.description);
        obj.extInfo

        obj.filePath*/
        // TODO: 2018/7/23 obj.extinfo  获取的 才是 小程序传递过来的信息
        /*可以将小程序端的参数以json字符串的形式传递，app端做相应解析*/
/*
        String msg = "小程序信息:\n"+"extinfo:"+obj.extInfo+"\nfilePath:"+obj.filePath+"\nfileData:"+obj.fileData;
        ToastUtils.show(msg);*/

        startActivity(new Intent(this, MainActivity.class));
        finish();

    }

    @Override
    public void onResp(BaseResp resp) {
        int errorCode = resp.errCode;
        int type = resp.getType();
        if(BuildConfig.DEBUG)Log.e("wenke","errorCode:"+errorCode+">>>type:"+type);
        switch (type) {
            case RETURN_MSG_TYPE_LOGIN:
                switch (errorCode) {
                    case BaseResp.ErrCode.ERR_OK: //登录成功
                        code = ((SendAuth.Resp) resp).code;
                        LiveDataBus.get().with(LiveDataBusKey.MINE_SIGN_WX_CODE, String.class).postValue(code);
//                        eventScreen = new EventScreen(Constant.wx_code, code);
//                        EventUtils.getDefault().forward(eventScreen);
                        finish();
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        code = ((SendAuth.Resp) resp).code;
                        ToastUtils.INSTANCE.showLongToast("取消登录", this);
                        finish();
                        break;
                    case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        code = ((SendAuth.Resp) resp).code;
                        Log.e("-----------", ((SendAuth.Resp) resp).errStr);
//                        eventScreen = new EventScreen(Constant.wx_code, "-2");
//                        EventUtils.getDefault().forward(eventScreen);
                        ToastUtils.INSTANCE.showLongToast("用户取消获取信息授权", this);
                        finish();
                        //用户拒绝
                        break;
                    default:
//                        result = ShareResultType.SHARE_FAIL;
                        finish();
                        break;
                }
                break;
            case RETURN_MSG_TYPE_SHARE:
                switch (errorCode) {
                    case BaseResp.ErrCode.ERR_OK: //分享成功
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        result = ShareResultType.SHARE_SUCCESS;
                        break;
                    default:
                        ToastUtils.INSTANCE.showLongToast(getString(R.string.str_shareFailure), this);
                        result = ShareResultType.SHARE_FAIL;
                        break;
                }
                LiveDataBus.get().with(LiveDataBusKey.WX_SHARE_BACK, Integer.class).postValue(result);
//                eventScreen = new EventScreen(Constant.WXSHARE, result);
//                EventUtils.getDefault().forward(eventScreen);
                finish();
                break;

            case ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM://小程序
                WXLaunchMiniProgram.Resp launchMiniProResp = (WXLaunchMiniProgram.Resp) resp;
                String extraData = launchMiniProResp.extMsg; // 对应JsApi navigateBackApplication中的extraData字段数据
                //这里返回标识可以自行和小程序商量统一
//                eventScreen = new EventScreen(Constant.MINIPROGRAMBACK);
//                EventUtils.getDefault().forward(eventScreen);
                if (!TextUtils.isEmpty(extraData)) {
                    try {
                        JSONObject jumpjson = JSON.parseObject(extraData);
//                        JumpUtils.getInstans().jump(WXEntryActivity.this, jumpjson.getIntValue("type"), jumpjson.getString("value"));
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        finish();
                    }
                }
                finish();
                break;
        }
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
