package com.changanford.my

import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.changanford.circle.adapter.CircleMainBottomAdapter
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.onSuccess
import com.changanford.my.databinding.FragmentCollectBinding
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
class PostFragment : BaseMineFM<FragmentCollectBinding, ActViewModel>() {

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
        binding.rcyCollect.rcyCommonView.layoutManager = staggeredGridLayoutManager
        binding.rcyCollect.rcyCommonView.adapter = postAdapter

    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyCollect.smartCommonLayout
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        when (type) {
            "collectPost" -> {
                viewModel.queryMineCollectPost(pageSize) { response ->
                    response.onSuccess {
                        completeRefresh(it?.dataList, postAdapter)
                    }
                }
            }
        }
    }
}