package com.changanford.my.ui

import android.graphics.Color
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.GrowUpItem
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.ItemGrowUpBinding
import com.changanford.my.databinding.UiGrowUpBinding
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.launch

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
        StatusBarUtil.setColor(this, Color.WHITE)
        StatusBarUtil.setAndroidNativeLightStatusBar(this, true)
        val gAdapter by lazy {
            GrowUpAdapter()
        }

        binding.mineToolbar.toolbarTitle.text = "成长值详情"
        binding.mineToolbar.toolbar.setNavigationOnClickListener {
            back()
        }

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
            }
        })
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.growUp.smartCommonLayout
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        lifecycleScope.launch {
            viewModel.mineGrowUp(pageSize, "2")
        }
    }

    inner class GrowUpAdapter :
        BaseQuickAdapter<GrowUpItem, BaseDataBindingHolder<ItemGrowUpBinding>>(
            R.layout.item_grow_up
        ) {
        override fun convert(holder: BaseDataBindingHolder<ItemGrowUpBinding>, item: GrowUpItem) {

        }
    }
}