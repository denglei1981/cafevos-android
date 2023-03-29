package com.changanford.evos.utils.pop

import android.content.Context
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.evos.PopViewModel
import com.changanford.evos.R
import com.changanford.home.request.HomeV2ViewModel
import com.changanford.home.widget.pop.GetFbPop
import razerdp.basepopup.BasePopupWindow

/**
 *Author lcw
 *Time on 2023/3/10
 *Purpose 领取福币弹窗job
 */
class GetFbPopJob : SingleJob {

    private var popViewModel: PopViewModel? = null
    private var context: Context? = null
    private var homeV2ViewModel: HomeV2ViewModel? = null
    private var lifecycleOwner: LifecycleOwner? = null

    override fun setContext(context: Context) {
        this.context = context
    }

    override fun setPopViewMode(popViewModel: PopViewModel) {
        this.popViewModel = popViewModel
    }

    fun setHomeV2ViewModel(homeV2ViewModel: HomeV2ViewModel) {
        this.homeV2ViewModel = homeV2ViewModel
    }

    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
    }

    override fun handle(): Boolean {
        val fbBean = popViewModel?.popBean?.value?.fbBean
        return !(fbBean == null || fbBean.isPop == 0)
    }

    override fun launch(callback: () -> Unit) {
        val fbBean = popViewModel?.popBean?.value?.fbBean
        if (Looper.myLooper() == null || context == null || fbBean == null || lifecycleOwner == null || homeV2ViewModel == null) {
            callback.invoke()
        } else {
            Looper.myLooper()?.let {
                android.os.Handler(it).postDelayed({
                    GetFbPop(context!!, homeV2ViewModel!!, fbBean, lifecycleOwner!!).apply {
                        LiveDataBus.get().with(LiveDataBusKey.UPDATE_MAIN_CHANGE).observe(context as AppCompatActivity){
                            dismiss()
                        }
                        setBackground(R.color.m_pop_bg)
                        setOutSideDismiss(false)
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
}