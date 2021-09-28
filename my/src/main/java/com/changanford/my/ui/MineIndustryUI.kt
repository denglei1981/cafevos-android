package com.changanford.my.ui

import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.bean.IndustryReturnBean
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.MINE_INDUSTRY
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.*
import com.changanford.my.adapter.MineCommAdapter
import com.changanford.my.viewmodel.SignViewModel

/**
 *  文件名：MineIndustryUI
 *  创建者: zcy
 *  创建日期：2020/5/22 17:44
 *  描述: TODO
 *  修改描述：TODO
 */
//@Route(path = ARouterMyPath.MineIndustryUI)
class MineIndustryUI : BaseMineUI<ViewRvBinding, SignViewModel>() {

    var adapter = MineCommAdapter.IndustryAdapter()

    var industryIds: String? = null

    override fun initView() {
        binding.mineToolbar.toolbarTitle.text = "行业"
        binding.mineToolbar.toolbar.setNavigationOnClickListener {
            back()
        }

        intent.getStringExtra("industryIds")?.let {
            industryIds = it
        }

        binding.mineToolbar.toolbarSave.text = "保存"
        binding.mineToolbar.toolbarSave.setOnClickListener {

            var likeIds: String = ""
            var likeNames: String = ""
            adapter.labels?.let {
                likeIds += "${it.industryId}"
                likeNames += "${it.industryName}"
            }

            LiveDataBus.get().with(MINE_INDUSTRY, IndustryReturnBean::class.java).postValue(
                IndustryReturnBean(likeIds, likeNames)
            )

            back()
        }

        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        binding.recyclerview.adapter = adapter

    }

    override fun initData() {
        viewModel.queryIndustryList {
            var list = it?.let {
                adapter.addData(it)
            }

            if (null == list) {
                showEmptyView()?.let {
                    adapter.setEmptyView(it)
                }
            }
            industryIds?.let {
                adapter.industryIds(it)
            }
        }
    }
}