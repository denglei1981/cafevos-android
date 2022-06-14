package com.changanford.my.activity

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.changanford.common.basic.BaseLoadSirActivity
import com.changanford.common.loadsir.*
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.evos.databinding.LayoutBaseRecyclerviewBinding
import com.changanford.home.R
import com.changanford.my.adapter.TaMedalAdapter
import com.changanford.my.request.OtherMedalViewModel
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.core.LoadSir
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class OtherMedalActivity : BaseLoadSirActivity<LayoutBaseRecyclerviewBinding, OtherMedalViewModel>(),
  OnRefreshListener {

    var userId = ""
    val taMedalAdapter: TaMedalAdapter by lazy {
        TaMedalAdapter()
    }
    companion object {
        fun start(userId: String,activity: Activity) {
             val intent = Intent()
            intent.putExtra("userId",userId)
            intent.setClass(activity,OtherMedalActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun setLoadSir(view: View?) {

            val loadSir = LoadSir.Builder()
                .addCallback(ErrorCallback()) //添加各种状态页
                .addCallback(EmptyMedalCallback())
                .addCallback(LoadingCallback())
                .addCallback(TimeoutCallback())
                .setDefaultCallback(LoadingCallback::class.java) //设置默认状态页
                .build()

            mLoadService = loadSir.register(view, Callback.OnReloadListener { v: View? ->

            })


    }

    override fun showEmpty() {
//        super.showEmpty()
        if (null != mLoadService) {
            mLoadService!!.showCallback(EmptyMedalCallback::class.java)
        }
    }
    override fun initView() {
        StatusBarUtil.setStatusBarMarginTop(binding.collectToolbar.conTitle, this)
        binding.collectToolbar.tvTitle.text = "TA的勋章"
        binding.collectToolbar.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.smartLayout.setOnRefreshListener(this)

        val g=GridLayoutManager(this,3)
        binding.recyclerView.layoutManager=g
        binding.recyclerView.adapter = taMedalAdapter
        binding.smartLayout.setEnableLoadMore(false)

    }

    override fun initData() {
        setLoadSir(binding.smartLayout)
        userId = intent.getStringExtra("userId").toString()
        if (!TextUtils.isEmpty(userId)) {
            viewModel.queryOtherUserMedal(userId)
        }
        taMedalAdapter.setOnItemClickListener { adapter, view, position ->
//            val item = myJoinTopicAdapter.getItem(position)
//            val bundle = Bundle()
//            bundle.putString("topicId", item.topicId.toString())
//            startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
        }
    }

    override fun observe() {
        super.observe()
        viewModel.medalLiveData.observe(this, Observer {
            if (it.isSuccess) {
                if(it.data.size==0){
                    showEmpty()
                }else{
                    showContent()
                    taMedalAdapter.setNewInstance(it.data)
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



    override fun onRefresh(refreshLayout: RefreshLayout) {
        if (!TextUtils.isEmpty(userId)) {
            viewModel.queryOtherUserMedal(userId)
        }
    }

    override fun onRetryBtnClick() {

    }
}