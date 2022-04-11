package com.changanford.shop.ui.order

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.toast
import com.changanford.shop.R
import com.changanford.shop.adapter.order.OrderEvaluationAdapter
import com.changanford.shop.databinding.ActPostEvaluationBinding
import com.changanford.shop.listener.UploadPicCallback
import com.changanford.shop.viewmodel.OrderViewModel
import com.changanford.shop.viewmodel.UploadViewModel
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * Author:wenke
 * Email:3158817509@qq.com
 * Create Time:2022/4/9
 * Update Time:
 * Note:订单评价、追评
 */
@Route(path = ARouterShopPath.PostEvaluationActivity)
class PostEvaluationActivity:BaseActivity<ActPostEvaluationBinding, OrderViewModel>() {
    companion object{
        fun start(orderNo:String) {
            JumpUtils.instans?.jump(112,orderNo)
        }
    }
    private val mAdapter by lazy { OrderEvaluationAdapter(this) }
    private var orderNo=""
    private val uploadViewModel by lazy { UploadViewModel() }
    private var reviewEval=false//是否追评
    override fun initView() {
        binding.apply {
            topBar.setActivity(this@PostEvaluationActivity)
            recyclerView.adapter=mAdapter
            btnSubmit.clicks().throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    submitEvaluation()
                }, {})
        }
    }

    override fun initData() {
        intent.getStringExtra("orderNo")?.apply {
            orderNo=this
            viewModel.getOrderDetail(orderNo)
        }
        viewModel.orderItemLiveData.observe(this){
            mAdapter.setList(it.skuList)
            mAdapter.initBean()
        }
        //订单评价回调
        viewModel.responseData.observe(this){
            getString(R.string.str_evaluationSuccess).toast()
            this.finish()
        }
    }
    private fun submitEvaluation(){
       var upI=0
        val postBean=mAdapter.postBean
        for ((i,item)in mAdapter.selectPicArr.withIndex()){
            //上传图片
            uploadViewModel.uploadFile(this,item.imgPathArr,object : UploadPicCallback{
                override fun onUploadSuccess(files: ArrayList<String>) {
                    upI++
                    var imgUrls=files
                    postBean[i].imgUrls=files
                    if(upI==postBean.size){//图片以上传完成
                        viewModel.postEvaluation(orderNo,postBean,reviewEval)
                    }
                }
                override fun onUploadFailed(errCode: String) {}
                override fun onuploadFileprogress(progress: Long) {}
            })
        }
    }
}