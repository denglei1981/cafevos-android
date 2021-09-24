package com.changanford.shop.ui.order

import android.content.Context
import android.content.Intent
import com.changanford.common.basic.BaseActivity
import com.changanford.shop.databinding.ActOrderEvaluationBinding
import com.changanford.shop.utils.WCommonUtil.onTextChanged

/**
 * @Author : wenke
 * @Time : 2021/9/23 0023
 * @Description : 订单评价
 */
class OrderEvaluationActivity:BaseActivity<ActOrderEvaluationBinding,OrderEvaluationViewModel>() {
    companion object{
        fun start(context: Context,orderId:String) {
            context.startActivity(Intent(context, OrderEvaluationActivity::class.java).putExtra("orderId",orderId))
        }
    }
    override fun initView() {
        binding.topBar.setActivity(this)
        val evaluationContent=binding.edtContent
        val contentLength=binding.tvContentLength
        viewModel.orderFormState.observe(this,{
            contentLength.setText("${it.contentLength}")
        })
        viewModel.evalutionDataChanged(evaluationContent.text.toString())
        evaluationContent.onTextChanged {
            viewModel.evalutionDataChanged(it.s.toString())
        }
    }

    override fun initData() {

    }

}