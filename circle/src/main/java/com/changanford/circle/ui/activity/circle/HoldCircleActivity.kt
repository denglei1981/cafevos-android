package com.changanford.circle.ui.activity.circle

import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.circle.HoldCircleAdapter
import com.changanford.circle.bean.MyJoinCircleBean
import com.changanford.circle.databinding.ActivityHoldCircleBinding
import com.changanford.circle.viewmodel.circle.HoleCircleViewModel
import com.changanford.common.basic.BaseLoadSirActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.changanford.common.util.ext.noAnima
import com.changanford.common.util.ext.setOnFastClickListener

/**
 * @author: niubobo
 * @date: 2024/6/4
 * @description：保留圈子(title=选择圈子)
 */
@Route(path = ARouterCirclePath.HoldCircleActivity)
class HoldCircleActivity : BaseLoadSirActivity<ActivityHoldCircleBinding, HoleCircleViewModel>() {

    private val adapter by lazy {
        HoldCircleAdapter()
    }

    override fun onRetryBtnClick() {
        initData()
    }

    private var checkCircleId: String = ""

    override fun initView() {
        binding.run {
            AppUtils.setStatusBarMarginTop(title.root, this@HoldCircleActivity)
            ryCircle.noAnima()
            setLoadSir(ryCircle)
            title.tvTitle.text = "选择圈子"
            title.ivBack.setOnClickListener { finish() }
            ryCircle.adapter = adapter
        }
        initListener()
    }

    private fun initListener() {
        adapter.setOnItemClickListener { _, _, position ->
            val item = adapter.getItem(position)
            if (!item.canCheck) {
                return@setOnItemClickListener
            }
            var bottomEnable = false
            adapter.data.forEachIndexed { index, holdCircleBean ->
                if (index == position) {
                    holdCircleBean.isCheck = !holdCircleBean.isCheck
                } else {
                    holdCircleBean.isCheck = false
                }
                if (holdCircleBean.isCheck) {
                    checkCircleId = holdCircleBean.circleId.toString()
                    bottomEnable = true
                }
            }
            setBottomColor(bottomEnable)
            adapter.notifyItemRangeChanged(0, adapter.data.size)
        }
        binding.tvHold.setOnFastClickListener {
            viewModel.getCircleHomeData(checkCircleId) {
                finish()
            }
        }
    }

    private fun setBottomColor(bottomEnable: Boolean) {
        if (bottomEnable) {
            binding.tvHold.setBackgroundResource(R.drawable.bg_shape_1700f4_23)
            binding.tvHold.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            binding.tvHold.setBackgroundResource(R.drawable.bg_shape_80a6_23)
            binding.tvHold.setTextColor(ContextCompat.getColor(this, R.color.color_4d16))
        }
        binding.tvHold.isEnabled = bottomEnable
    }

    override fun initData() {
        viewModel.getCircleHomeData()
    }

    override fun observe() {
        super.observe()
        viewModel.myJoinCircleBean.observe(this) {
            if (it.isNullOrEmpty()) {
                showEmptyLoadView()
            } else {
                adapter.setList(resetData(it))
                showContent()
            }
        }
    }

    private fun resetData(list: ArrayList<MyJoinCircleBean>): ArrayList<MyJoinCircleBean> {
        val useList = ArrayList<MyJoinCircleBean>()
        val lordList = list.find { item -> item.circleLord == true }
        if (lordList != null) {//有圈主 只能选择圈主
            list.forEach {
                it.canCheck = it.circleLord == true
                useList.add(it)
            }
        } else {//都能选择
            list.forEach {
                it.canCheck = true
                useList.add(it)
            }
        }
        return useList
    }
}