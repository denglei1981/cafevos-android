package com.changanford.my.ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.bean.RoundBean
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.MineUtils
import com.changanford.common.util.TimeUtils
import com.changanford.my.BaseMineUI
import com.changanford.my.adapter.MineCommAdapter
import com.changanford.my.databinding.FragmentsignmonthBinding
import com.changanford.my.viewmodel.SignViewModel

@Route(path = ARouterMyPath.SignMonth)
class SignMonthUI : BaseMineUI<FragmentsignmonthBinding, SignViewModel>() {
    private var monthAdapter = MineCommAdapter.MonthSignAdapter()
    var dateList = ArrayList<RoundBean>()
    var index: Int = 0
    override fun initView() {
        binding.title.toolbarTitle.text = "签到记录"
        binding.title.toolbar.setNavigationOnClickListener { finish() }
        binding.signview.apply {
            signDateRec.adapter = monthAdapter
            monthleft.setOnClickListener {
                index--
                curTime.text = TimeUtils.getShowYearMonth(index)
                getData(TimeUtils.getRequestYearMonth(index))
            }
            monthright.setOnClickListener {
                index++
                curTime.text = TimeUtils.getShowYearMonth(index)
                getData(TimeUtils.getRequestYearMonth(index))
            }
        }
    }

    override fun initData() {
        binding.signview.curTime.text = TimeUtils.getShowYearMonth(index)
        getData(TimeUtils.getRequestYearMonth(index))
    }

    fun getData(date: String) {
        viewModel.monthSignDetail(date) { bean ->
            bean?.let {
                dateList.clear()
                it.data?.apply {
                    binding.signview.apply {
                        smCzz.text = totalGrowth
                        smJf.text = totalIntegral
                        guize.text = signRule
                        MineUtils.signAccMonth(
                            signTotal,
                            "您已连续签到",
                            "${ontinuous}",
                            "天,累计签到" + "${cumulation}天"
                        )
                        MineUtils.signAccMonth(signAcc, "当前奖励：", "${multiple}倍", "奖励")
                    }
                    roundList?.let { it1 ->
                        dateList.addAll(it1)
                        monthAdapter.data = dateList
                        monthAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}