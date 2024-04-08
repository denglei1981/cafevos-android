package com.changanford.common.utilext.permission;

/**
 * @author: niubobo
 * @date: 2024/4/7
 * @description：
 */
public enum PermissionState {
    /**
     * 询问状态，展示弹窗
     */
    ASKING(true),
    /**
     * 点击拒绝，未点击不再显示，展示弹窗
     */
    MORE_REQUEST(true),
    /**
     * 已同意，不展示弹窗
     */
    HAS_GRANTED(false),
    /**
     * 点击拒绝，已点击不再显示，不展示弹窗
     */
    DEFINED(false);

    private boolean mShowDialog;

    PermissionState(boolean showDialog){
        mShowDialog = showDialog;
    }

    public boolean isShowDialog(){
        return mShowDialog;
    }
}
