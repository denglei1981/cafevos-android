package com.changanford.evos.utils.pop

import android.content.Context
import android.os.Looper
import com.changanford.common.ui.NewEstOnePop
import com.changanford.common.util.MineUtils
import com.changanford.evos.PopViewModel
import com.orhanobut.hawk.Hawk

/**
 *Author lcw
 *Time on 2023/3/10
 *Purpose 广告弹窗job
 */
class NewEstOnePopJob : SingleJob {

    private var popViewModel: PopViewModel? = null
    private var context: Context? = null

    override fun setContext(context: Context) {
        this.context = context
    }

    override fun setPopViewMode(popViewModel: PopViewModel) {
        this.popViewModel = popViewModel
    }

    override fun handle(): Boolean {
        return true
    }

    override fun launch(callback: () -> Unit) {
        val bean = popViewModel?.popBean?.value?.newEstOneBean
        if (!bean?.appVo?.ads.isNullOrEmpty()) {
//            if (!Hawk.get(
//                    MineUtils.getTodayTime() + bean?.appVo?.ads?.get(0)?.adId.toString(),
//                    false
//                )
//            ) {
                android.os.Handler(Looper.myLooper()!!).postDelayed({
                    NewEstOnePop(context!!, bean!!).apply {
                        showPopupWindow()
                        setOnPopupWindowShowListener {
                            callback.invoke()
                            Hawk.put(
                                MineUtils.getTodayTime() + bean.appVo?.ads?.get(0)?.adId.toString(),
                                true
                            )
                        }
                    }
                }, 0)
//            }
        }
    }
}