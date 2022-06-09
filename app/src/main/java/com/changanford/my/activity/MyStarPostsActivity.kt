package com.changanford.my.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.changanford.circle.adapter.CircleMainBottomAdapter
import com.changanford.common.basic.BaseLoadSirActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.evos.databinding.LayoutBaseRecyclerviewBinding
import com.changanford.home.PageConstant
import com.changanford.home.R
import com.changanford.my.adapter.MyJoinCircleAdapter
import com.changanford.my.adapter.MyJoinTopicAdapter
import com.changanford.my.request.MyJoinViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class MyStarPostsActivity : BaseLoadSirActivity<LayoutBaseRecyclerviewBinding, MyJoinViewModel>(),
    OnLoadMoreListener, OnRefreshListener {

    var userId = ""
    val postAdapter: CircleMainBottomAdapter by lazy {
        CircleMainBottomAdapter(this)
    }

    companion object {
        fun start(userId: String, activity: Activity) {
            var intent = Intent()
            intent.putExtra("userId", userId)
            intent.setClass(activity, MyStarPostsActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun initView() {
        StatusBarUtil.setStatusBarMarginTop(binding.collectToolbar.conTitle, this)
        binding.collectToolbar.tvTitle.text = "点赞的帖子"
        binding.collectToolbar.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.smartLayout.setOnRefreshListener(this)
        binding.smartLayout.setOnLoadMoreListener(this)
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = postAdapter
    }

    override fun initData() {
        userId = intent.getStringExtra("userId").toString()
        if (!TextUtils.isEmpty(userId)) {
            viewModel.getMyLikedPosts(userId, false)
        }
        postAdapter.setOnItemClickListener { adapter, view, position ->
            val bundle = Bundle()
            bundle.putString("postsId", postAdapter.getItem(position).postsId.toString())
            startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
        }
    }

    override fun observe() {
        super.observe()
        viewModel.myLikedPostsLiveData.observe(this, Observer {
            if (it.isSuccess) {
                val dataList = it.data.dataList
                if (it.isLoadMore) {
                    postAdapter.addData(dataList)
                    binding.smartLayout.finishLoadMore()
                } else {
                    if (it.data == null || dataList.size == 0) {
                        showEmpty()
                    }
                    showContent()
                    postAdapter.setNewInstance(dataList)
                    binding.smartLayout.finishRefresh()
                }
                if (it.data.dataList?.size!! < PageConstant.DEFAULT_PAGE_SIZE_THIRTY) {
                    binding.smartLayout.setEnableLoadMore(false)
                } else {
                    binding.smartLayout.setEnableLoadMore(true)
                }
            } else {
                when (it.message) {
                    getString(R.string.net_error) -> {
                        showTimeOut()
                    }
                    else -> {
                        showFailure(it.message)
                    }
                }
                ToastUtils.showShortToast(it.message, this)

            }

        })
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (!TextUtils.isEmpty(userId)) {
            viewModel.getMyLikedPosts(userId, true)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        if (!TextUtils.isEmpty(userId)) {
            viewModel.getMyLikedPosts(userId, false)
        }
    }

    override fun onRetryBtnClick() {

    }
}