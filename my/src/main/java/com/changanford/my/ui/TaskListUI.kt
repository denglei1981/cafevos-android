package com.changanford.my.ui

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.RoundBean
import com.changanford.common.net.*
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.adapter.TaskTitleAdapter
import com.changanford.my.databinding.ItemSignDayBinding
import com.changanford.my.databinding.UiTaskBinding
import com.changanford.my.utils.ConfirmTwoBtnPop
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.launch

/**
 *  文件名：TaskListUI
 *  创建者: zcy
 *  创建日期：2021/9/13 11:14
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineTaskListUI)
class TaskListUI : BaseMineUI<UiTaskBinding, SignViewModel>() {
    var isRefresh: Boolean = false //回到当前页面刷新列表

    val taskAdapter: TaskTitleAdapter by lazy {
        TaskTitleAdapter()
    }

    val dayAdapter: DayAdapter by lazy {
        DayAdapter()
    }

    override fun initView() {
//        StatusBarUtil.setTranslucentForImageView(this, null)
        binding.imBack.setOnClickListener {
            back()
        }
        binding.tvTaskExplain.setOnClickListener { //任务说明
            JumpUtils.instans?.jump(1, MConstant.H5_TASK_RULE)
        }

        binding.taskRcy.rcyCommonView.adapter = taskAdapter
        binding.taskRcy.rcyCommonView.scheduleLayoutAnimation()
        binding.signmonth.setOnClickListener { JumpUtils.instans?.jump(55) }

        viewModel.taskBean.observe(this, Observer {
            completeRefresh(it, taskAdapter, 0)
        })

        binding.rcyDay.layoutManager = LinearLayoutManager(this).apply {
            orientation = RecyclerView.HORIZONTAL
        }
        binding.rcyDay.adapter = dayAdapter
        viewModel.userDatabase.getUniUserInfoDao().getUser().observe(this, Observer {
            it?.let {
                binding.tvTaskJifenNum.text = "我的福币：${it.integral?.toInt()}"
            }
        })


        binding.taskFinish.setOnClickListener {
            JumpUtils.instans?.jump(37)
        }

        LiveDataBus.get()
            .with("SEND_POST", Boolean::class.java)
            .observe(this, Observer {
                isRefresh = true
                if (it) {//此时发帖
                    JumpUtils.instans?.jump(102)
                }
            })
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.taskRcy.smartCommonLayout
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        task()
    }

    override fun onResume() {
        super.onResume()
        getData()
        if (isRefresh) {
            isRefresh = false
            task()
        }
    }

    fun task() {
        viewModel.queryTasksList()
    }

    private fun getData() {
        viewModel.weekSignDetail() { bean ->
            dayAdapter.data.clear()
            bean?.let {
                it.data?.apply {
                    binding.des.text =
                        "已连续签到${ontinuous ?: 0}天，明天签到+${nextIntegral ?: 0}福币+${nextGrowth ?: 0}成长值"
                    roundList?.forEach {
                        it.isNowDay = TimeUtils.getNowDay().equals(it.date)
                        if (TimeUtils.getNowDay().equals(it.date)) {
                            binding.taskFinish.visibility =
                                if (it.isSignIn == 1) View.GONE else View.VISIBLE
                        }
                    }
                    dayAdapter.addData(roundList)
                }
            }
        }
    }

    inner class DayAdapter :
        BaseQuickAdapter<RoundBean, BaseDataBindingHolder<ItemSignDayBinding>>(R.layout.item_sign_day) {
        override fun convert(holder: BaseDataBindingHolder<ItemSignDayBinding>, item: RoundBean) {

            holder.dataBinding?.let {
                if (item.isSignIn == 1) {//已签到
                    it.signCheck.visibility = View.VISIBLE
                    it.num.visibility = View.GONE
                } else {//未签到
                    it.signCheck.visibility = View.GONE
                    it.num.visibility = View.VISIBLE
                    it.num.text =
                        if (TimeUtils.dayTaskBefore(item.date)) "补" else "+${item.integral}"
                    if (TimeUtils.dayTaskBefore(item.date)) {
                        it.clLayout.setOnClickListener {
                            var pop = ConfirmTwoBtnPop(BaseApplication.curActivity)
                            pop.contentText.text = "本次补签将消耗 ${item.integral} 福币"
                            pop.btnConfirm.text = "立即补签"
                            pop.btnConfirm.setOnClickListener {
                                pop.dismiss()
                                BaseApplication.currentViewModelScope.launch {
                                    fetchRequest(showLoading = true) {
                                        var body = HashMap<String, String>()
                                        body["date"] = item.date
                                        var rkey = getRandomKey()
                                        apiService.signReissue(
                                            body.header(rkey),
                                            body.body(rkey)
                                        )
                                    }.onSuccess {
                                        getData()
                                        viewModel.getUserInfo()
                                    }.onWithMsgFailure {
                                        it?.let {
                                            showToast(it)
                                        }
                                    }
                                }
                            }
                            pop.btnCancel.setOnClickListener {
                                pop.dismiss()
                            }
                            pop.showPopupWindow()
                        }
                    }
                }
                try {
                    var date = item.date.split("-")
                    if (item.isNowDay) {
                        it.day.text = "今天"
                    } else {
                        it.day.text = "${date[1]}.${date[2]}"
                    }
                } catch (e: Exception) {

                }
            }
        }
    }

    override fun hasRefresh(): Boolean {
        return false
    }

    override fun isUseFullScreenMode(): Boolean {
        return true
    }

    override fun isUserLightMode(): Boolean {
        return false
    }

}