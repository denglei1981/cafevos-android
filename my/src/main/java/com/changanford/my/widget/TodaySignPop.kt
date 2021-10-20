package com.changanford.my.widget

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.CycleInterpolator
import android.view.animation.RotateAnimation
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.changanford.common.bean.DaySignBean
import com.changanford.common.util.MineUtils
import com.changanford.my.R
import com.changanford.my.databinding.PopTodaySignBinding
import razerdp.basepopup.BasePopupWindow


/**
 *  文件名：TodaySignPop
 *  创建者: zcy
 *  创建日期：2020/5/13 19:04
 *  描述: 每日签到  pop
 *  修改描述：onCreateShowAnimation()/onCreateDismissAnimation():初始化一个显示/退出动画，该动画将会用到
 *           onCreatePopupView()所返回的view,可以为空。
 *           onCreatePopupView():初始化您的popupwindow界面，建议直接使用createPopupById()
 */

class TodaySignPop(context: Context) : BasePopupWindow(context) {

    lateinit var jf: TextView
    lateinit var czz: TextView
    lateinit var signTotal: TextView
    lateinit var signAcc: TextView
    lateinit var signTo: TextView
//    lateinit var adapter: MineCommAdapter.SignWeekAdapter
    lateinit var totalSignNum: TextView
    init {
        var viewDataBinding: PopTodaySignBinding = DataBindingUtil.bind(createPopupById(R.layout.pop_today_sign))!!
        contentView=viewDataBinding.root
        //星期
//        var weekRv = view.findViewById<RecyclerView>(R.id.sign_week_rv)
//        weekRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//        adapter = MineCommAdapter.SignWeekAdapter(R.layout.item_sign_time_line)
        totalSignNum = viewDataBinding.totalSignNum
//        weekRv.adapter = adapter
//        adapter.addData(MineUtils.listWeek)

        //U币
        jf = viewDataBinding.signJf
        //U币
        czz = viewDataBinding.signCzz

        signTotal = viewDataBinding.signTotal
        signAcc = viewDataBinding.signAcc
        signTo = viewDataBinding.signTo
        viewDataBinding.signKnow.setOnClickListener {
            dismiss()
        }
    }


    fun initDayBean(daySignBean: DaySignBean) {
        MineUtils.signJf(jf, "+${daySignBean.integral}", "福币")
        MineUtils.signJf(czz, "+${daySignBean.growth}", "成长值")
        MineUtils.signAcc(signTotal,"您已连续签到","${daySignBean.ontinuous}","天")
        MineUtils.signAcc(signAcc,"当前奖励：","${daySignBean.multiple}","倍奖励")
//        signTotal.text = "您已连续签到${daySignBean.ontinuous}天，累计签到${daySignBean.cumulation}天"

        signTo.text = "明天签到+${daySignBean.nextIntegral}福币 +${daySignBean.nextGrowth}成长值"
        totalSignNum.text = "您是今天第${daySignBean.actionTimes}位签到的用户"

    }

    override fun onCreateShowAnimation(): Animation {
        val set = AnimationSet(false)
        val shakeAnima: Animation = RotateAnimation(
            0f,
            15f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        shakeAnima.interpolator = CycleInterpolator(5f)
        shakeAnima.duration = 400
        set.addAnimation(shakeAnima)
        return set
    }
}