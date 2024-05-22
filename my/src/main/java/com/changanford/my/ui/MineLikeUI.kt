package com.changanford.my.ui

import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.bean.RetrunLike
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.MINE_LIKE
import com.changanford.common.utilext.toast
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.adapter.MineCommAdapter
import com.changanford.my.databinding.UiMineLikeBinding
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout

/**
 *  文件名：MineLikeUI
 *  创建者: zcy
 *  创建日期：2020/5/8 13:29
 *  描述: 兴趣标签列表
 *  修改描述：TODO
 */
//@Route(path = ARouterMyPath.MineLikeUI)
class MineLikeUI : BaseMineUI<UiMineLikeBinding, SignViewModel>() {

    var adapter = MineCommAdapter.LikeAdapter(R.layout.item_like_one)

    private var hobbyIds: String? = null

    override fun initView() {
        binding.mineToolbar.toolbarTitle.text = "兴趣爱好"
        binding.mineToolbar.toolbarSave.text = "保存"
        binding.mineToolbar.toolbar.setNavigationOnClickListener { back() }

        intent.getStringExtra("hobbyIds")?.let {
            hobbyIds = it
        }

        binding.mineToolbar.toolbarSave.setOnClickListener {
            var likeIds: String = ""
            var likeNames: String = ""

            adapter.labels.forEach {
                likeIds = likeIds + "${it.hobbyId}" + ","
                likeNames = likeNames + "${it.hobbyName}" + ","
            }
            if (likeIds.isNotEmpty()) {
                LiveDataBus.get().with(MINE_LIKE, RetrunLike::class.java)
                    .postValue(RetrunLike(likeIds, likeNames))
                finish()
            } else {
                "请先选择兴趣爱好".toast()
            }
        }
        initOne()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        binding.likeRv.refreshLayout
    }

    override fun initRefreshData(pageNo: Int) {
        super.initRefreshData(pageNo)
        viewModel.getHobbyList()
    }

    override fun initData() {
        viewModel.getHobbyList()

        viewModel._hobbyBean.observe(this, {
            var list = it?.let {
                adapter.addData(it)
            }
            if (null == list) {
                showEmptyView()?.let {
                    adapter.setEmptyView(it)
                }
            }
            hobbyIds?.let {
                adapter.hobbyIds(it)
            }
        })
    }

    /**
     * 初始化一级
     */
    fun initOne() {
        binding.likeRv.refreshRv.layoutManager = LinearLayoutManager(this)
        binding.likeRv.refreshRv.adapter = adapter
    }
}