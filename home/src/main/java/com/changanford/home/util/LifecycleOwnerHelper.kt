package com.changanford.home.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.changanford.common.basic.Block
import com.changanford.common.utilext.toast
import com.huawei.hms.common.ApiException
import kotlinx.coroutines.launch

/**
 *Author lcw
 *Time on 2021/10/9
 *Purpose
 */
fun LifecycleOwner.launchWithCatch(block: Block<Unit>) {
    lifecycleScope.launch {
        try {
            block.invoke()
        } catch (error: ApiException) {
            error.message?.toast()
        }
    }
}