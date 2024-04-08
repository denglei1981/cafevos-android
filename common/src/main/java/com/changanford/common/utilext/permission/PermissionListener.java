package com.changanford.common.utilext.permission;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * @author: niubobo
 * @date: 2024/4/7
 * @description：
 */
public interface PermissionListener {
    void onSucceed(int var1, @NonNull List<String> var2);

    void onFailed(int var1, @NonNull List<String> var2);
}
