package com.changanford.common.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.changanford.common.basic.ApiException
import com.changanford.common.basic.Block
import com.changanford.common.utilext.longE
import com.changanford.common.utilext.toast
import kotlinx.coroutines.launch
import retrofit2.HttpException

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
            error.errorMessage.toast()
        } catch (error: HttpException) {
            error.message?.longE()
        } catch (error: Exception) {
            error.message?.longE()
        } catch (error: java.lang.Exception) {
            error.message?.longE()
        }
    }
}