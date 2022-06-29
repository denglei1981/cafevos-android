package com.changanford.my.activity

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import androidx.lifecycle.Observer
import com.changanford.common.basic.BaseLoadSirActivity
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.evos.databinding.LayoutBaseRecyclerviewBinding
import com.changanford.home.PageConstant
import com.changanford.home.R
import com.changanford.my.adapter.MyJoinCircleAdapter
import com.changanford.my.adapter.MyJoinCircleMoreAdapter
import com.changanford.my.request.MyJoinViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class MyJoinCircleActivity : BaseLoadSirActivity<LayoutBaseRecyclerviewBinding, MyJoinViewModel>(),
    OnLoadMoreListener, OnRefreshListener {

    var userId = ""
    val myJoinCircleAdapter: MyJoinCircleMoreAdapter by lazy {
        MyJoinCircleMoreAdapter()
    }
    companion object {
        fun start(userId: String,activity: Activity) {
             var intent = Intent()
            intent.putExtra("userId",userId)
            intent.setClass(activity,MyJoinCircleActivity::class.java)
            activity.startActivity(intent)
        }
    }
    override fun initView() {
        StatusBarUtil.setStatusBarMarginTop(binding.collectToolbar.conTitle, this)
        binding.collectToolbar.tvTitle.text = "加入的圈子"
        binding.collectToolbar.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.smartLayout.setOnRefreshListener(this)
        binding.smartLayout.setOnLoadMoreListener(this)
        binding.recyclerView.adapter = myJoinCircleAdapter
    }

    override fun initData() {
        userId = intent.getStringExtra("userId").toString()
        if (!TextUtils.isEmpty(userId)) {
            viewModel.getMyCircles(userId, false)
        }
        myJoinCircleAdapter.setOnItemClickListener { adapter, view, position ->
            JumpUtils.instans?.jump(
                6,
                myJoinCircleAdapter.getItem(position).circleId.toString()
            )
        }
    }

    override fun observe() {
        super.observe()
        viewModel.circlesListData.observe(this, Observer {
            if (it.isSuccess) {
                val dataList = it.data.dataList
                if (it.isLoadMore) {
                    if (dataList != null) {
                        myJoinCircleAdapter.addData(dataList)
                    }
                    binding.smartLayout.finishLoadMore()
                } else {
                    if (dataList != null) {
                        if (it.data == null || dataList.size == 0) {
                            showEmpty()
                        }
                    }
                    showContent()
                    myJoinCircleAdapter.setNewInstance(dataList)
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
            viewModel.getMyCircles(userId, true)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        if (!TextUtils.isEmpty(userId)) {
            viewModel.getMyCircles(userId, false)
        }
    }

    override fun onRetryBtnClick() {

    }
}