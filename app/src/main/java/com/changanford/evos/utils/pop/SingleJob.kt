package com.changanford.evos.utils.pop

import android.content.Context
import com.changanford.evos.PopViewModel

/**
 *Author lcw
 *Time on 2023/3/10
 *Purpose
 */
interface SingleJob {
    fun setContext(context: Context)
    fun setPopViewMode(popViewModel: PopViewModel)
    fun handle(): Boolean
    fun launch( callback: () -> Unit)
}