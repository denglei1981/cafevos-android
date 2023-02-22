package com.changanford.my

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.changanford.circle.adapter.CircleMainBottomAdapter
import com.changanford.common.databinding.ViewEmptyTopBinding
import com.changanford.common.manger.RouterManger
import com.changanford.common.manger.UserManger
import com.changanford.common.net.onSuccess
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.gio.updatePersonalData
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
    var userId: String = ""

    val postAdapter: CircleMainBottomAdapter by lazy {
        CircleMainBottomAdapter(requireContext())
    }

    companion object {
        fun newInstance(value: String, userId: String = "", bg: Boolean = false): PostFragment {
            val bundle: Bundle = Bundle()
            bundle.putString(RouterManger.KEY_TO_OBJ, value)
            bundle.putString(RouterManger.KEY_TO_ID, userId)
            bundle.putBoolean("bg", bg)
            val medalFragment = PostFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

    var isRefresh: Boolean = false

    override fun onPause() {
        super.onPause()
        isRefresh = true
    }

    override fun onResume() {
        super.onResume()
        if (isRefresh) {
            isRefresh = false
            initRefreshData(1)
        }
    }

    override fun initView() {


        arguments?.getString(RouterManger.KEY_TO_OBJ)?.let {
            type = it
            if (it == "footPost") {
                postAdapter.type = "我的足迹-帖子"
            } else if (it == "collectPost") {
                postAdapter.type = "我的收藏-帖子"
            }
        }
        arguments?.getBoolean("bg")?.let {
            if (it) {
                binding.llBg.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.shape_gray_f4)
            }

        }

        userId = UserManger.getSysUserInfo()?.uid ?: ""
        arguments?.getString(RouterManger.KEY_TO_ID)?.let {
            userId = it
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

        postAdapter.setOnItemClickListener { _, view, position ->
            GioPageConstant.postEntrance = "发帖人个人主页"
            updatePersonalData(postAdapter.getItem(position).title.toString(), "帖子详情页")
            val bundle = Bundle()
            bundle.putString("postsId", postAdapter.getItem(position).postsId.toString())
            startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
        }
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyPost.smartCommonLayout
    }

    var searchKeys: String = ""

    fun mySerachInfo() {
        var total: Int = 0
        viewModel.queryMineCollectPost(1, searchKeys) { response ->
            response?.data?.total?.let {
                total = it
            }
            response.onSuccess {
                completeRefresh(it?.dataList, postAdapter, total)
            }
        }
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        var total: Int = 0
        when (type) {
            "collectPost" -> {
                viewModel.queryMineCollectPost(pageSize, searchKeys) { response ->
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
            "centerPost" -> {
                viewModel.queryMineSendPost(userId, pageSize) { response ->
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

    override fun showEmpty(): View? {
        return when (type) {
            "centerPost" -> {
                ViewEmptyTopBinding.inflate(layoutInflater).root
            }
            else -> {
                super.showEmpty()
            }
        }
    }
}