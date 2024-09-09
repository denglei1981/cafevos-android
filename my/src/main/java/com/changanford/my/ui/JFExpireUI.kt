package com.changanford.my.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.alibaba.android.arouter.facade.annotation.Route
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.basic.BaseLoadSirActivity
import com.changanford.common.bean.JFExpireItemBean
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.AppUtils
import com.changanford.common.util.MUtils
import com.changanford.common.util.SpannableStringUtils
import com.changanford.common.util.TimeUtils
import com.changanford.my.R
import com.changanford.my.databinding.ActivityJfExpireBinding
import com.changanford.my.databinding.ItemGrowUpBinding
import com.changanford.my.viewmodel.JFExpireViewModel
import com.scwang.smart.refresh.layout.util.SmartUtil
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


/**
 * @author: niubobo
 * @date: 2024/8/27
 * @description：积分过期筛选
 */
@Route(path = ARouterMyPath.JFExpireUI)
class JFExpireUI : BaseLoadSirActivity<ActivityJfExpireBinding, JFExpireViewModel>() {

    private var pvActTime: OptionsPickerView<String>? = null
    private var page = 1
    private var starTime = ""
    private var endTime = ""
    private val adapter by lazy { ExpireAdapter() }
    private var times: ArrayList<String>? = null
    private var isShowLoading = false
    private var endShowTime = ""

    override fun onRetryBtnClick() {
        initData()
    }

    override fun initView() {
        isDarkFont = false
        setLoadSir(binding.clContent)
        AppUtils.setStatusBarPaddingTop(binding.toolbar, this)
        binding.toolbar.setBackgroundResource(0)
        binding.imBack.setOnClickListener { finish() }
        binding.tvFilter.setOnClickListener {
            initTimePick()
        }
        binding.rcyJifen.smartCommonLayout.setEnableLoadMore(false)
        binding.rcyJifen.smartCommonLayout.setEnableRefresh(false)
        binding.rcyJifen.rcyCommonView.adapter = adapter
        adapter.loadMoreModule.setOnLoadMoreListener {
            isShowLoading = false
            page++
            initData()
        }
    }

    override fun initData() {
        viewModel.getData(page, starTime = starTime, endTime = endTime, isShowLoading)
    }

    override fun observe() {
        super.observe()
        viewModel.jfExpireBean.observe(this) {
            showContent()
            if (page == 1) {
                if (it==null){
                    showEmptyLoadView()
                    return@observe
                }
                if (starTime.isEmpty()) {
                    setTextContent(it.showDaysNum, it.totalScore.toString())
                } else {
                    setTextContent(endShowTime, it.totalScore.toString())
                }
                adapter.setList(it.detailList)
                if (it.detailList?.size == 0) {
                    adapter.setEmptyView(R.layout.base_layout_empty)
                }
            } else {
                it?.detailList?.let { list ->
                    adapter.addData(list)
                }
                adapter.loadMoreModule.loadMoreComplete()
            }
            it?.let {
                if (it.totalPage >= page) {
                    adapter.loadMoreModule.loadMoreEnd()
                }
            }

        }
    }

    private fun setTextContent(time: String, fb: String) {
        val starContent = "在${time}日内将有"
        val changeContent = "${fb}福币"
        val endContent = "即将过期"
        val sp = SpannableStringUtils.colorSpan(
            "${starContent}${changeContent}${endContent}",
            starContent.length,
            starContent.length + changeContent.length,
            R.color.color_FAC572
        )
        binding.tvHint.text = sp
    }

    private fun initTimePick() {
        if (
            pvActTime == null
        ) {
            pvActTime = OptionsPickerBuilder(
                this
            ) { p1, _, _, _ ->
                val selectTime = times?.get(p1)
                if (selectTime == "默认") {
                    isShowLoading = true
                    starTime = ""
                    endTime = ""
                    page = 1
                    initData()
                } else {
                    selectTime?.let {
                        val result = extractYearAndMonth(it)
                        result?.let { r ->
                            val (year, month) = r
                            val (star, end) = getMonthStartEndDates(year, month)
                            val (star2, end2) = getMonthStartEndDates2(year, month)
                            endShowTime = end2
                            starTime = star
                            endTime = end
                            page = 1
                            isShowLoading = true
                            initData()
                        }
                    }
                }
            }
                .setLayoutRes(R.layout.customer_expire_time_picker_title) {
                    val tvSubmit = it.findViewById<TextView>(R.id.tv_finish)
                    val ivCancel = it.findViewById<ImageView>(R.id.iv_cancel)
                    val tvTitle = it.findViewById<TextView>(R.id.tv_title)
                    tvSubmit.setOnClickListener {
                        pvActTime?.returnData()
                        pvActTime?.dismiss()
                    }
                    ivCancel.setOnClickListener {

                    }
                    tvTitle.text = "选择时间"
                }
                .setCancelText("取消") //取消按钮文字
                .setSubmitText("确定") //确认按钮文字
                .setTitleText("开始时间")
                .setTitleSize(SmartUtil.dp2px(6f)) //标题文字大小
                .setContentTextSize(17)
                .setTextColorCenter(ContextCompat.getColor(this, R.color.color_16))
                .setTextColorOut(ContextCompat.getColor(this, R.color.color_9d))
                .setDividerColor(ContextCompat.getColor(this, R.color.transparent))
                .setOutSideCancelable(true) //点击屏幕，点在控件外部范围时，是否取消显示
                .setSubmitColor(resources.getColor(R.color.black)) //确定按钮文字颜色
                .setCancelColor(resources.getColor(R.color.textgray)) //取消按钮文字颜色
                .setTitleBgColor(resources.getColor(R.color.color_withe)) //标题背景颜色 Night mode
                .setBgColor(Color.WHITE) //滚轮背景颜色 Night mode
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)
                .build()
            times = getNextThreeMonths()
            pvActTime?.setPicker(times)
        }
        pvActTime?.show()
    }

    //获取当前月和后面三个月
    private fun getNextThreeMonths(): ArrayList<String> {
        val dateFormat = SimpleDateFormat("yyyy年MM月", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val months = ArrayList<String>()
        months.add("默认")

        for (i in 0..3) {
            months.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.MONTH, 1)
        }

        return months
    }

    //提取出年-月
    private fun extractYearAndMonth(dateString: String): Pair<Int, Int>? {
        val regex = "(\\d{4})年(\\d{1,2})月".toRegex()
        val matchResult = regex.find(dateString)

        return matchResult?.let {
            val (year, month) = it.destructured
            Pair(year.toInt(), month.toInt())
        }
    }

    //获取某月的月初和月末时间
    private fun getMonthStartEndDates(year: Int, month: Int): Pair<String, String> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        // 设置年月
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1) // Calendar.MONTH 是从0开始的

        // 获取月初日期
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = dateFormat.format(calendar.time)

        // 获取月末日期
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val endDate = dateFormat.format(calendar.time)

        return Pair(startDate, endDate)
    }

    private fun getMonthStartEndDates2(year: Int, month: Int): Pair<String, String> {
        val dateFormat = SimpleDateFormat("yyyy年MM月dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        // 设置年月
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1) // Calendar.MONTH 是从0开始的

        // 获取月初日期
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = dateFormat.format(calendar.time)

        // 获取月末日期
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val endDate = dateFormat.format(calendar.time)

        return Pair(startDate, endDate)
    }

    inner class ExpireAdapter :
        BaseQuickAdapter<JFExpireItemBean, BaseDataBindingHolder<ItemGrowUpBinding>>(R.layout.item_grow_up),
        LoadMoreModule {

        @SuppressLint("SetTextI18n")
        override fun convert(
            holder: BaseDataBindingHolder<ItemGrowUpBinding>,
            item: JFExpireItemBean
        ) {
            holder.dataBinding?.apply {
                MUtils.setTopMargin(root, 10, holder.layoutPosition)
                title.text = item.stype
                date.text = "过期日期:${TimeUtils.MillisToDayStr(item.expireTime)}"
                num.text = item.availableAmount.toString()
                from.isVisible = false
                num.setTextColor(Color.parseColor("#D9161616"))
            }

        }

    }
}