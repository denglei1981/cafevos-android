package com.changanford.evos.utils.pop

import android.content.Context
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.changanford.common.ui.HoldCirclePop
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.evos.PopViewModel
import com.changanford.evos.R
import razerdp.basepopup.BasePopupWindow

/**
 *Author lcw
 *Time on 2024/6/6
 *Purpose 保留圈子job
 */
class HoldCircleJob : SingleJob {

    private var popViewModel: PopViewModel? = null
    private var context: Context? = null

    override fun setContext(context: Context) {
        this.context = context
    }

    override fun setPopViewMode(popViewModel: PopViewModel) {
        this.popViewModel = popViewModel
    }

    override fun handle(): Boolean {
        val bean = popViewModel?.popBean?.value?.holdCircleBean
        return bean == true
//        return true //测试使用 一直弹出
    }

    override fun launch(callback: () -> Unit) {
        val bean = popViewModel?.popBean?.value?.holdCircleBean
        if (context == null || bean == null) {
            callback.invoke()
            return
        }
        android.os.Handler(Looper.myLooper()!!).postDelayed({
            HoldCirclePop(context!!).apply {
                LiveDataBus.get().with(LiveDataBusKey.UPDATE_MAIN_CHANGE)
                    .observe(context as AppCompatActivity) {
                        dismiss()
                    }
                setBackground(R.color.m_pop_bg)
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