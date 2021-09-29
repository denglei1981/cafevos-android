package com.changanford.my

import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.changanford.circle.adapter.CircleMainBottomAdapter
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.onSuccess
import com.changanford.my.databinding.FragmentPostBinding
import com.changanford.my.viewmodel.ActViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import java.lang.reflect.Method

/**
 *  文件名：PostFragment
 *  创建者: zcy
 *  创建日期：2021/9/29 9:35
 *  描述: 我的足迹，我的收藏 贴子列表
 *  修改描述：TODO
 */
class PostFragment : BaseMineFM<FragmentPostBinding, ActViewModel>() {

    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    private lateinit var mCheckForGapMethod: Method

    var type: String = ""

    val postAdapter: CircleMainBottomAdapter by lazy {
        CircleMainBottomAdapter(requireContext())
    }

    companion object {
        fun newInstance(value: String): PostFragment {
            var bundle: Bundle = Bundle()
            bundle.putString(RouterManger.KEY_TO_OBJ, value)
            var medalFragment = PostFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

    override fun initView() {
        arguments?.getString(RouterManger.KEY_TO_OBJ)?.let {
            type = it
        }

        mCheckForGapMethod =
            StaggeredGridLayoutManager::class.java.getDeclaredMethod("checkForGaps")
        mCheckForGapMethod.isAccessible = true

        staggeredGridLayoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        binding.rcyPost.rcyCommonView.layoutManager = staggeredGridLayoutManager
        binding.rcyPost.rcyCommonView.adapter = postAdapter

    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyPost.smartCommonLayout
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        var total: Int = 0
        when (type) {
            "collectPost" -> {
                viewModel.queryMineCollectPost(pageSize) { response ->
                    response?.data?.total?.let {
                        total = it
                    }
                    response.onSuccess {
                        completeRefresh(it?.dataList, postAdapter, total)
                    }
                }
            }
            "footPost" -> {
                viewModel.queryMineFootPost(pageSize) { response ->
                    response?.data?.total?.let {
                        total = it
                    }
                    response.onSuccess {
                        completeRefresh(it?.dataList, postAdapter, total)
                    }
                }
            }
        }
    }
}