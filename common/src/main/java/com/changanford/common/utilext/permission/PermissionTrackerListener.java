package com.changanford.common.utilext.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: niubobo
 * @date: 2024/4/7
 * @description：
 */
public class PermissionTrackerListener implements PermissionListener{
    private Activity mActivity;
    private PermissionListener mListener;
    /**
     * 权限列表
     */
    private List<String> mNeedTrackPermissionList;
    /**
     * 当前几个权限的状态
     */
    private List<PermissionState> mNeedTrackPermissionStateList;

    public PermissionTrackerListener(Activity activity, PermissionListener listener){
        mActivity = activity;
        mListener = listener;
        mNeedTrackPermissionList = new ArrayList<>();
        mNeedTrackPermissionStateList = new ArrayList<>();
    }

    @Override
    public void onSucceed(int i, @NonNull List<String> list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int j = 0; j < mNeedTrackPermissionList.size(); j++) {
                PermissionState state = mNeedTrackPermissionStateList.get(j);
                String permission = mNeedTrackPermissionList.get(j);
                if (state == null){
                    state = PermissionState.ASKING;
                }
                if (state == PermissionState.ASKING || state == PermissionState.MORE_REQUEST) {
                    Toast.makeText(mActivity, "已弹窗埋点: " + permission, Toast.LENGTH_SHORT).show();
                    Toast.makeText(mActivity, "点击同意埋点: " + permission, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "未弹窗埋点: " + permission, Toast.LENGTH_SHORT).show();
                }
            }
        }
        mListener.onSucceed(i, list);
    }

    @Override
    public void onFailed(int i, @NonNull List<String> list) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int j = 0; j < mNeedTrackPermissionList.size(); j++) {
                String permission = mNeedTrackPermissionList.get(j);
                boolean denied = list.contains(permission);
                PermissionState state = mNeedTrackPermissionStateList.get(j);
                if (state == null) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
                        state = PermissionState.ASKING;
                    } else {
                        state = PermissionState.DEFINED;
                    }
                }
                if (state == PermissionState.ASKING || state == PermissionState.MORE_REQUEST) {
                    Toast.makeText(mActivity, "已弹窗埋点: " + permission, Toast.LENGTH_SHORT).show();
                    if (denied) {
                        Toast.makeText(mActivity, "点击拒绝埋点: " + permission, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mActivity, "点击同意埋点: " + permission, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mActivity, "未弹窗埋点: " + permission, Toast.LENGTH_SHORT).show();
                }
            }
        }
        mListener.onFailed(i, list);
    }

    /**
     * 申请权限之前调用
     * @param list  需要申请的权限
     */
    public void preRequestPermission(@NonNull List<String> list){
        mNeedTrackPermissionList = list;
        if (mNeedTrackPermissionList.isEmpty()){
            return;
        }
        for (String permission : mNeedTrackPermissionList) {
            PermissionState state = null;
            if (ActivityCompat.checkSelfPermission(mActivity, permission) == PackageManager.PERMISSION_GRANTED){
                state = PermissionState.HAS_GRANTED;
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)){
                state = PermissionState.MORE_REQUEST;
            }
            mNeedTrackPermissionStateList.add(state);
        }
    }
}
