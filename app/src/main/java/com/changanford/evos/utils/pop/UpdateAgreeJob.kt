package com.changanford.evos.utils.pop

import android.app.Activity
import android.content.Context
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.changanford.common.ui.UpdateAgreePop
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.request.addRecord
import com.changanford.evos.PopViewModel
import com.changanford.evos.R

/**
 *Author lcw
 *Time on 2023/3/10
 *Purpose 协议更新job
 */
class UpdateAgreeJob : SingleJob {

    private var popViewModel: PopViewModel? = null
    private var context: Context? = null

    override fun setContext(context: Context) {
        this.context = context
    }

    override fun setPopViewMode(popViewModel: PopViewModel) {
        this.popViewModel = popViewModel
    }

    override fun handle(): Boolean {
        val bean = popViewModel?.popBean?.value?.bizCodeBean
        return bean?.windowMsg != null
    }

    override fun launch(callback: () -> Unit) {
        val bean = popViewModel?.popBean?.value?.bizCodeBean
        if (context == null || bean == null) {
            callback.invoke()
            return
        }
        android.os.Handler(Looper.myLooper()!!).postDelayed({
            UpdateAgreePop(
                context!!,
                bean.windowMsg!!,
                object : UpdateAgreePop.UpdateAgreePopListener {
                    override fun clickCancel() {
                        (context as Activity).finish()
                    }

                    override fun clickSure() {
                        bean.ids?.let { it1 -> addRecord(it1) }
                        callback.invoke()
                    }

                }).apply {
                LiveDataBus.get().with(LiveDataBusKey.UPDATE_MAIN_CHANGE).observe(context as AppCompatActivity){
                    dismiss()
                }
                setBackground(R.color.m_pop_bg)
                showPopupWindow()
            }
        }, 0)
    }
}