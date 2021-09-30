package com.changanford.common.sharelib.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.changanford.common.ui.dialog.LoadDialogdy;


/**
 * File description.
 *
 * @author lihongjun
 * @date 2018/3/9
 */

public class BaseShareManager {

    public String title = ""; // 标题
    public String content = ""; // 文本年内容
    public String targetUrl = ""; // 跳转链接

    protected ProgressDialog progressDialog;
    protected LoadDialogdy mLoadDialogdy;
    protected Context mContext;

    public BaseShareManager(Context context) {
        mContext = context;
    }

    public void showProgressDialog(boolean cancelable) {
        initProgressDialog(cancelable);
        if (!((Activity)mContext).isFinishing() && !mLoadDialogdy.isShowing()) {
            mLoadDialogdy.show();
        }
    }

    public void showProgressDialog() {
        showProgressDialog(false);
    }

    public void hideProgressDialog(boolean cancelable) {
        initProgressDialog(cancelable);
        if (mLoadDialogdy.isShowing()) {
            mLoadDialogdy.dismiss();
        }
    }

    public void hideProgressDialog() {
        initProgressDialog(false);
        if (mLoadDialogdy.isShowing()) {
            mLoadDialogdy.dismiss();
        }
    }

    private void initProgressDialog(boolean cancelable) {

        if (mLoadDialogdy==null){
            mLoadDialogdy = new LoadDialogdy(mContext);
            mLoadDialogdy.setCanceledOnTouchOutside(cancelable);
            mLoadDialogdy.setCancelable(false);
        }
        if (mLoadDialogdy!=null){
            mLoadDialogdy.dismiss();
        }
    }
}
