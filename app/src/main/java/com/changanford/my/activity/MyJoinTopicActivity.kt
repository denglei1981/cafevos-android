package com.changanford.my.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
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

class MyJoinTopicActivity : BaseLoadSirActivity<LayoutBaseRecyclerviewBinding, MyJoinViewModel>(),
    OnLoadMoreListener, OnRefreshListener {

    var userId = ""
    val myJoinTopicAdapter: MyJoinTopicAdapter by lazy {
        MyJoinTopicAdapter()
    }
    companion object {
        fun start(userId: String,activity: Activity) {
             var intent = Intent()
            intent.putExtra("userId",userId)
            intent.setClass(activity,MyJoinTopicActivity::class.java)
            activity.startActivity(intent)
        }
    }
    override fun initView() {
        StatusBarUtil.setStatusBarMarginTop(binding.collectToolbar.conTitle, this)
        binding.collectToolbar.tvTitle.text = "参与的话题"
        binding.collectToolbar.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.smartLayout.setOnRefreshListener(this)
        binding.smartLayout.setOnLoadMoreListener(this)
        binding.recyclerView.adapter = myJoinTopicAdapter
    }

    override fun initData() {
        userId = intent.getStringExtra("userId").toString()
        if (!TextUtils.isEmpty(userId)) {
            viewModel.getMyTopics(userId, false)
        }
        myJoinTopicAdapter.setOnItemClickListener { adapter, view, position ->
            val item = myJoinTopicAdapter.getItem(position)
            val bundle = Bundle()
            bundle.putString("topicId", item.topicId.toString())
            startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
        }
    }

    override fun observe() {
        super.observe()
        viewModel.myTopicsLiveData.observe(this, Observer {
            if (it.isSuccess) {
                val dataList = it.data.dataList
                if (it.isLoadMore) {
                    if (dataList != null) {
                        myJoinTopicAdapter.addData(dataList)
                    }
                    binding.smartLayout.finishLoadMore()
                } else {
                    if (dataList != null) {
                        if (it.data == null || dataList.size == 0) {
                            showEmpty()
                        }
                    }
                    showContent()
                    myJoinTopicAdapter.setNewInstance(dataList)
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
                ToastUtils.showShortToast(it.message,this)

            }

        })
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (!TextUtils.isEmpty(userId)) {
            viewModel.getMyTopics(userId, true)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        if (!TextUtils.isEmpty(userId)) {
            viewModel.getMyTopics(userId, false)
        }
    }

    override fun onRetryBtnClick() {

    }
}