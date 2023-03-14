package com.changanford.evos.utils.pop

import android.content.Context
import android.os.Looper
import com.changanford.common.bean.NewEstOneItemBean
import com.changanford.common.ui.NewEstOnePop
import com.changanford.common.util.time.GetTimeBeforeDate
import com.changanford.evos.PopViewModel
import com.orhanobut.hawk.Hawk
import razerdp.basepopup.BasePopupWindow

/**
 *Author lcw
 *Time on 2023/3/10
 *Purpose 广告弹窗job
 */
class NewEstOnePopJob : SingleJob {

    private var popViewModel: PopViewModel? = null
    private var context: Context? = null
    private var mDayNum = 0
    private var mDays = 0
    private var mDaysNum = 0

    override fun setContext(context: Context) {
        this.context = context
    }

    override fun setPopViewMode(popViewModel: PopViewModel) {
        this.popViewModel = popViewModel
    }

    override fun handle(): Boolean {
        val rule = popViewModel?.popBean?.value?.popRuleBean ?: return false
        val dayNum = getDayNum()//获取今天弹窗次数
        if (dayNum >= rule.oneDayNum) return false //今天的弹窗次数大于一天最大次数
//        if (getDaysNum() > rule.daysNum) return false //本地储存天数次数大于配置天数的弹窗次数
        val bean = popViewModel?.popBean?.value?.newEstOneBean ?: return false
        if (bean.appVo == null || bean.appVo?.ads.isNullOrEmpty()) return false
        if (bean.maVo == null || bean.maVo?.ads.isNullOrEmpty()) return false
        mDays = rule.days
        mDayNum = rule.oneDayNum
        mDaysNum = rule.daysNum
        return true
    }

    override fun launch(callback: () -> Unit) {
        val bean = popViewModel?.popBean?.value?.newEstOneBean ?: return
        var useBean: NewEstOneItemBean? = null

        if (!bean.maVo?.ads.isNullOrEmpty() && getDaysNum(bean.maVo?.ads?.get(0)?.adId) < mDaysNum) {
            bean.maVo?.let {
                if (getDaysNum(it.ads[0].adId) < mDaysNum)
                    useBean = it.ads[0]
            }
        } else if (!bean.appVo?.ads.isNullOrEmpty()) {
            bean.appVo?.let {
                if (getDaysNum(it.ads[0].adId) < mDaysNum)
                    useBean = it.ads[0]
            }
        }

        if (useBean != null) {
            android.os.Handler(Looper.myLooper()!!).postDelayed({
                NewEstOnePop(context!!, useBean!!).apply {
                    showPopupWindow()
                    setOnPopupWindowShowListener {
                        addEstOneDayNum(useBean!!.adId)
                        addDayNum()
                    }
                    onDismissListener = object : BasePopupWindow.OnDismissListener() {
                        override fun onDismiss() {
                            callback.invoke()
                        }

                    }
                }
            }, 0)
        }
    }

    private fun getEstOneDayNum(id: Int?, before: Int): Int {
        return Hawk.get(GetTimeBeforeDate.getTimeDate(before) + id.toString(), 0)
    }

    private fun addEstOneDayNum(id: Int) {
        var estDayNum = Hawk.get(GetTimeBeforeDate.getTimeDate(0) + id.toString(), 0)
        estDayNum++
        Hawk.put(GetTimeBeforeDate.getTimeDate(0) + id.toString(), estDayNum)
    }

    private fun getDayNum(): Int {
        return Hawk.get(GetTimeBeforeDate.getTimeDate(0), 0)
    }

    private fun addDayNum() {
        var dayNum = Hawk.get(GetTimeBeforeDate.getTimeDate(0), 0)
        dayNum++
        Hawk.put(GetTimeBeforeDate.getTimeDate(0), dayNum)
    }

    //获取单个广告配置天数总次数
    private fun getDaysNum(id: Int?): Int {
        var beforeNum = 0
        if (mDays == 0) return getDayNum()
        for (i in 0 until mDays) {
            beforeNum += getEstOneDayNum(id, i)
        }
        return beforeNum
    }
}