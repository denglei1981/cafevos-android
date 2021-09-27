package com.changanford.my.ui

import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.GrowUpItem
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.JumpUtils
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.adapter.GrowUpAndJifenViewHolder
import com.changanford.my.databinding.ItemGrowUpBinding
import com.changanford.my.databinding.UiGrowUpBinding
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 *  文件名：GrowUpUI
 *  创建者: zcy
 *  创建日期：2021/9/17 20:15
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineGrowUpUI)
class GrowUpUI : BaseMineUI<UiGrowUpBinding, SignViewModel>() {

    override fun initView() {

        val gAdapter by lazy {
            GrowUpAdapter()
        }

        binding.mineToolbar.toolbarTitle.text = "成长值详情"

        binding.mineToolbar.toolbarIcon.visibility = View.VISIBLE
        binding.mineToolbar.toolbarSave.visibility = View.GONE

//        binding.mineToolbar.toolbarIcon.setImageResource(R.mipmap.icon_grow_up)
//        binding.mineToolbar.toolbarIcon.setOnClickListener {
//
//            JumpUtils.instans?.jump(1, H5_MINE_GROW_UP)
//        }


        binding.myUpdateGrade.setOnClickListener {
            JumpUtils.instans!!.jump(16, "")
        }
        binding.myGradeProgressbar.setProgressWithAnimation(0f)

        binding.growUp.rcyCommonView.adapter = gAdapter
        viewModel.jifenBean.observe(this, Observer {
            if (null == it) {
                showEmpty()?.let { empty ->
                    gAdapter.setEmptyView(empty)
                }
            } else {
                completeRefresh(it.dataList, gAdapter, it.total)
                if (pageSize == 1) {
                    var growUp = it.extend
                    growUp?.let {
                        binding.myGradeV.text = it.growSeriesName

                        try {
                            binding.myGradeDes.text =
                                "再获取${(it.nextSeriesMinGrow - it.growthSum).toInt()}成长值即可升级为${it.nextGrowSeriesName}"
                            binding.myGradeNum.text =
                                "${it.growthSum.toInt()}/${it.nextSeriesMinGrow.toInt()}"
                            binding.myGradeProgressbar.setProgressWithAnimation((it.growthSum * 100 / it.nextSeriesMinGrow).toFloat())

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        binding.num.text = "当前成长值${growUp.growthSum.toInt()}"
                        binding.tvExplainTitle.text = "${it.growSeriesName} 权益说明："
                        binding.tvExplain.text = "${it.rulesDesc}"
                    }
                }
            }
        })
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.growUp.smartCommonLayout
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        viewModel.mineGrowUp(pageSize, "2")
    }

    inner class GrowUpAdapter :
        BaseQuickAdapter<GrowUpItem, BaseDataBindingHolder<ItemGrowUpBinding>>(
            R.layout.item_grow_up
        ) {
        private var source: String = ""
        override fun convert(holder: BaseDataBindingHolder<ItemGrowUpBinding>, item: GrowUpItem) {
            GrowUpAndJifenViewHolder(holder, item, true, source)
        }

        fun setSource(source: String) {
            this.source = source
        }
    }
}