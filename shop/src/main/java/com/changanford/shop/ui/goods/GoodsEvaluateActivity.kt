package com.changanford.shop.ui.goods

import android.text.TextUtils
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.toast.ToastUtils
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.GoodsEvalutaeAdapter
import com.changanford.shop.databinding.ActGoodsEvaluateBinding
import com.changanford.shop.viewmodel.GoodsViewModel
import com.google.gson.Gson
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 * @Author : wenke
 * @Time : 2021/9/22 0022
 * @Description : 商品评价
 */
@Route(path = ARouterShopPath.GoodsEvaluateActivity)
class GoodsEvaluateActivity:BaseActivity<ActGoodsEvaluateBinding, GoodsViewModel>(),
    OnRefreshLoadMoreListener {
    companion object{
        fun start(spuId:String?) {
//            context.startActivity(Intent(context,GoodsEvaluateActivity::class.java).putExtra("spuId",spuId))
            spuId?.let { JumpUtils.instans?.jump(111,it) }
        }
    }
    private val mAdapter by lazy { GoodsEvalutaeAdapter() }
    private var pageNo=1
    private var spuId:String="0"
    private var spuPageType:String?=null
    override fun initView() {
        binding.topBar.setActivity(this)
//        spuId=intent.getStringExtra("spuId")?:"0"
        val goodsInfo=intent.getStringExtra("goodsInfo")
        if(TextUtils.isEmpty(goodsInfo)){
            ToastUtils.showLongToast(getString(R.string.str_parameterIllegal),this)
            this.finish()
            return
        }
        goodsInfo?.apply {
            if(startsWith("{")){
                val dataBean= Gson().fromJson(goodsInfo, GoodsDetailBean::class.java)
                spuPageType=dataBean.spuPageType
                spuId=dataBean.spuId
            }else spuId=this
        }
        binding.recyclerView.adapter=mAdapter
        mAdapter.setEmptyView(R.layout.view_empty)
        binding.smartRl.setOnRefreshLoadMoreListener(this)
    }
    override fun initData() {
        viewModel.getGoodsEvalList(spuId,pageNo,spuPageType=spuPageType)
        viewModel.commentLiveData.observe(this,{
            it?.apply {
                val dataList=pageList?.dataList
                if(1==pageNo)mAdapter.setList(dataList)
                else dataList?.let { it1 -> mAdapter.addData(it1) }
                binding.model=this
            }
            if(it?.pageList == null ||mAdapter.data.size>=it.pageList?.total!!)binding.smartRl.setEnableLoadMore(false)
            else binding.smartRl.setEnableLoadMore(true)
            binding.smartRl.apply {
                finishLoadMore()
                finishRefresh()
            }
        })
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageNo=1
        viewModel.getGoodsEvalList(spuId,pageNo,spuPageType=spuPageType)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNo++
        viewModel.getGoodsEvalList(spuId,pageNo,spuPageType=spuPageType)
    }
}