package com.changanford.evos.utils.pop

import android.app.Activity
import android.content.Context
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.changanford.common.ui.GetCoupopBindingPop
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.evos.PopViewModel
import razerdp.basepopup.BasePopupWindow

/**
 *Author lcw
 *Time on 2023/3/10
 *Purpose 优惠券job
 */
class ReceivePopJob : SingleJob {

    private var popViewModel: PopViewModel? = null
    private var context: Context? = null
    private var lifecycleOwner: LifecycleOwner? = null

    override fun setContext(context: Context) {
        this.context = context
    }

    override fun setPopViewMode(popViewModel: PopViewModel) {
        this.popViewModel = popViewModel
    }

    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
    }

    override fun handle(): Boolean {
        val bean = popViewModel?.popBean?.value?.coupons
        return !(bean == null || bean.isEmpty())
    }

    override fun launch(callback: () -> Unit) {
        val bean = popViewModel?.popBean?.value?.coupons
        if (bean == null || context == null || lifecycleOwner == null) {
            callback.invoke()
        } else {
            android.os.Handler(Looper.myLooper()!!).postDelayed({
                GetCoupopBindingPop(context as Activity, lifecycleOwner!!, bean).apply {
                    LiveDataBus.get().with(LiveDataBusKey.UPDATE_MAIN_CHANGE).observe(context as AppCompatActivity){
                        dismiss()
                    }
                    showPopupWindow()
                    onDismissListener = object : BasePopupWindow.OnDismissListener() {
                        override fun onDismiss() {
                            callback.invoke()
                        }

                    }
                }
            }, 0)
        }

    }
}