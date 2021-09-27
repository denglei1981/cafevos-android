package com.changanford.my.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.CircleMemberData
import com.changanford.common.manger.RouterManger
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.load
import com.changanford.my.BaseMineFM
import com.changanford.my.R
import com.changanford.my.bean.MangerCircleCheck
import com.changanford.my.databinding.FragmentMemberCircleBinding
import com.changanford.my.viewmodel.CircleViewModel
import com.google.android.material.imageview.ShapeableImageView
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 *  文件名：CircleFragment
 *  创建者: zcy
 *  创建日期：2021/9/26 19:31
 *  描述: TODO
 *  修改描述：TODO
 */
class MangerCircleFragment : BaseMineFM<FragmentMemberCircleBinding, CircleViewModel>() {

    val circleAdapter: CircleAdapter by lazy {
        CircleAdapter()
    }

    var index: Int = 0 // 0 全部 1 待审核
    var circleId: String = ""
    var circleCheck: MangerCircleCheck = MangerCircleCheck(0, false) //显示选择

    companion object {
        fun newInstance(value: Int, circleId: String): MangerCircleFragment {
            var bundle: Bundle = Bundle()
            bundle.putInt(RouterManger.KEY_TO_ID, value)
            bundle.putString(RouterManger.KEY_TO_ITEM, circleId)
            var medalFragment = MangerCircleFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

    override fun initView() {
        arguments?.getInt(RouterManger.KEY_TO_ID)?.let {
            index = it
        }
        arguments?.getString(RouterManger.KEY_TO_ITEM)?.let {
            circleId = it
        }

        binding.rcyCollect.rcyCommonView.adapter = circleAdapter

        viewModel.circleMember.observe(this, Observer {
            it?.dataList?.let { list ->
                list.forEach {
                    it.itemType = index
                }
                completeRefresh(list, circleAdapter)
            }
        })

        LiveDataBus.get()
            .with(LiveDataBusKey.MINE_DELETE_CIRCLE_USER, MangerCircleCheck::class.java)
            .observe(this, Observer {
                it?.let { c ->
                    circleCheck = c
                    binding.bottomLayout.visibility = if (c.isShow) View.VISIBLE else View.GONE
                    binding.btnCheckId.visibility = if (c.index == 0) View.VISIBLE else View.GONE
                    binding.btnApply.visibility = if (c.index == 1) View.VISIBLE else View.GONE
                    binding.btnCheckNotApply.visibility =
                        if (c.index == 1) View.VISIBLE else View.GONE
                    when (c.index) {
                        0 -> {
                            //设置身份
                        }
                        1 -> {
                            //审核
                        }
                    }
                    circleAdapter.notifyDataSetChanged()
                }
            })
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        when (index) {
            0 -> {
                viewModel.queryJoinCircle(pageSize, circleId)
            }
            1 -> {
                viewModel.queryJoinCreateCircle(pageSize, circleId)
            }
        }
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyCollect.smartCommonLayout
    }

    inner class CircleAdapter : BaseMultiItemQuickAdapter<CircleMemberData, BaseViewHolder>() {

        init {
            addItemType(0, R.layout.item_member_all)
            addItemType(1, R.layout.item_member)
        }

        override fun convert(holder: BaseViewHolder, item: CircleMemberData) {
            var checkBox: CheckBox = holder.getView(R.id.checkbox)
            when (getItemViewType(holder.layoutPosition)) {
                0 -> {
                    var icon: ShapeableImageView = holder.getView(R.id.item_icon)
                    var name: TextView = holder.getView(R.id.item_name)
                    var date: TextView = holder.getView(R.id.item_date)
                    icon.load(item.avatar)
                    name.text = item.nickname
                    date.text = item.createTime
                }
                1 -> {
                    var icon: ShapeableImageView = holder.getView(R.id.item_icon)
                    var name: TextView = holder.getView(R.id.item_name)
                    var date: TextView = holder.getView(R.id.item_date)
                    icon.load(item.avatar)
                    name.text = item.nickname
                    date.text = item.createTime
                }
            }

            checkBox.visibility =
                if (this@MangerCircleFragment.circleCheck.isShow) View.VISIBLE else View.GONE

            checkBox.setOnCheckedChangeListener { _, isChecked ->

            }
        }
    }
}