package com.changanford.shop.ui.order

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.toast
import com.changanford.common.web.AndroidBug5497Workaround
import com.changanford.shop.R
import com.changanford.shop.adapter.order.OrderEvaluationAdapter
import com.changanford.shop.bean.PostEvaluationListBean
import com.changanford.shop.databinding.ActPostEvaluationBinding
import com.changanford.shop.listener.UploadPicCallback
import com.changanford.shop.viewmodel.OrderViewModel
import com.changanford.shop.viewmodel.UploadViewModel
import com.google.gson.Gson
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
        fun start(item:PostEvaluationListBean) {
            JumpUtils.instans?.jump(112,Gson().toJson(item))
        }
    }

    private var orderNo=""
    private val uploadViewModel by lazy { UploadViewModel() }
    private var reviewEval=false//是否追评
    private var upI=0
    private lateinit var dialog: LoadDialog
    private val mAdapter by lazy { OrderEvaluationAdapter(this,reviewEval) }
    override fun initView() {
        AndroidBug5497Workaround.assistActivity(this)
        binding.apply {
            topBar.setActivity(this@PostEvaluationActivity)
            if(reviewEval)topBar.setTitle(getString(R.string.str_releasedAfterReview))
            recyclerView.adapter=mAdapter
            btnSubmit.clicks().throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    submitEvaluation()
                }, {})
        }
        dialog = LoadDialog(this)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setLoadingText("图片上传中..")
    }

    override fun initData() {
        intent.getStringExtra("info")?.apply {
            if(this.startsWith("{")){
                Gson().fromJson(this,PostEvaluationListBean::class.java).let {
                    orderNo=it.orderNo
                    reviewEval=it.reviewEval?:false
//                    if(reviewEval&&)viewModel.orderItemLiveData.postValue()
                }
            }else {
                orderNo=this
                viewModel.getOrderDetail(orderNo)
            }

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
        mAdapter.postBeanLiveData.observe(this){
            val isComplete= it.find { item-> !item.isComplete }
            binding.btnSubmit.setBtnEnabled(isComplete==null)
        }
    }
    private fun submitEvaluation(){
        if(reviewEval){
            viewModel.postEvaluation(orderNo,mAdapter.postBean,reviewEval)
        }else{
            dialog.show()
            uploadPic(0)
        }
    }
    private fun uploadPic(pos:Int=0){
        val postBean=mAdapter.postBean
        val item=mAdapter.selectPicArr[pos]
        //上传图片
        uploadViewModel.uploadFile(this,item.imgPathArr,object : UploadPicCallback {
            override fun onUploadSuccess(files: ArrayList<String>) {
                upI++
                postBean[pos].imgUrls=files
                if(upI==postBean.size){//图片以上传完成
                    viewModel.postEvaluation(orderNo,postBean,reviewEval)
                    dialog.dismiss()
                }else uploadPic(pos+1)
            }
            override fun onUploadFailed(errCode: String) {
                upI=0
                dialog.dismiss()
            }
            override fun onuploadFileprogress(progress: Long) {}
        })
    }
}